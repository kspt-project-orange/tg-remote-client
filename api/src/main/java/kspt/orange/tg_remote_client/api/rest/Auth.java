package kspt.orange.tg_remote_client.api.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import kspt.orange.tg_remote_client.api.util.Parser;
import kspt.orange.tg_remote_client.api.util.TokenGenerator;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_client.TgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//TODO
@SuppressWarnings("unused")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v0/auth")
public final class Auth implements Api {
    @NotNull
    private final Db db;
    @NotNull
    private final TgService tg;
    @NotNull
    private final Parser parser;
    @NotNull
    private final TokenGenerator tokenGenerator;

    @PostMapping(path = "/requestCode", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<RequestCodeResponse> requestCode(@RequestBody @NotNull final Mono<RequestCodeRequest> body) {
        return body
                .flatMap(parser::validOrEmpty)
                .flatMap(requestBody -> {
                    final var phone = requestBody.phone;
                    final var token = tokenGenerator.nextToken();

                    return tg.requestCode(phone, token)
                            .flatMap(__ -> db.addAuthAttempt(phone, token))
                            .map(__ -> RequestCodeResponse.ok(token));
                })
                .defaultIfEmpty(RequestCodeResponse.ERROR);
    }

    @PostMapping(path = "/signIn", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<? extends Response> signIn(@RequestBody @NotNull final Mono<SignInRequest> body) {
        return body
                .flatMap(parser::validOrEmpty)
                .flatMap(requestBody -> {
                    final var phone = requestBody.phone;
                    final var token = requestBody.token;
                    final var code = requestBody.code;

                    return db.checkAuthAttemptToken(phone, token)
                            .flatMap(__ -> tg.signIn(phone, token, code))
                            .map(__ -> SignInResponse.OK);
                })
                .defaultIfEmpty(SignInResponse.ERROR);
    }

    @PostMapping(path = "/pass2FA", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<? extends Response> pass2Fa(@RequestBody @NotNull final Mono<Pass2FaRequest> body) {
        return body
                .map(parser::validatedObject)
                .map(it -> Pass2FaResponse.OK)
                .onErrorReturn(Pass2FaResponse.ERROR);
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class RequestCodeRequest implements Request {
        @Nullable
        private final String phone;
    }

    @RequiredArgsConstructor
    private static final class RequestCodeResponse implements Response {
        @NotNull
        private final static RequestCodeResponse ERROR = new RequestCodeResponse(Status.ERROR, null);

        @NotNull
        private final Status status;
        @Nullable
        private final String token;

        private static RequestCodeResponse ok(@NotNull final String token) {
            return new RequestCodeResponse(Status.OK, token);
        }

        enum Status {
            OK,
            ERROR,
        }
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class SignInRequest implements Request {
        @Nullable
        private final String token;
        @Nullable
        private final String phone;
        @Nullable
        private final String code;
    }

    @RequiredArgsConstructor
    private static final class SignInResponse implements Response {
        @NotNull
        private static final SignInResponse ERROR_2FA_NEEDED = new SignInResponse(Status.ERROR_2FA_NEEDED);
        @NotNull
        private static final SignInResponse ERROR = new SignInResponse(Status.ERROR);
        @NotNull
        private static final SignInResponse OK = new SignInResponse(Status.OK);

        @NotNull
        private final Status status;

        private enum Status {
            OK,
            ERROR_2FA_NEEDED,
            ERROR,
        }
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class Pass2FaRequest implements Request {
        @Nullable
        private final String token;
        @Nullable
        private final String password;
    }

    @RequiredArgsConstructor
    private static final class Pass2FaResponse implements Response {
        @NotNull
        private static final Pass2FaResponse ERROR = new Pass2FaResponse(Status.ERROR);
        @NotNull
        private static final Pass2FaResponse OK = new Pass2FaResponse(Status.OK);

        @NotNull
        private final Status status;

        private enum Status {
            OK,
            ERROR,
        }
    }
}
