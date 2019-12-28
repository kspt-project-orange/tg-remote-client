package kspt.orange.tg_remote_client.api.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import kspt.orange.tg_remote_client.api.util.Parser;
import kspt.orange.tg_remote_client.api.util.TokenGenerator;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_client.result.Pass2FaResult;
import kspt.orange.tg_remote_client.tg_client.result.RequestCodeResult;
import kspt.orange.tg_remote_client.tg_client.result.SignInResult;
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
                            .flatMap(status -> db.addAuthAttempt(phone, token)
                            .map(__ -> RequestCodeResponse.of(status, token)));
                })
                .defaultIfEmpty(RequestCodeResponse.ERROR);
    }

    @PostMapping(path = "/signIn", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<SignInResponse> signIn(@RequestBody @NotNull final Mono<SignInRequest> body) {
        return body
                .flatMap(parser::validOrEmpty)
                .flatMap(requestBody -> {
                    final var token = requestBody.token;
                    final var code = requestBody.code;

                    return db.checkAuthAttemptToken(token)
                            .flatMap(__ -> tg.signIn(token, code))
                            .map(SignInResponse::of);
                })
                .defaultIfEmpty(SignInResponse.ERROR);
    }

    @PostMapping(path = "/pass2FA", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<Pass2FaResponse> pass2Fa(@RequestBody @NotNull final Mono<Pass2FaRequest> body) {
        return body
                .flatMap(parser::validOrEmpty)
                .flatMap(requestBody -> {
                    final var token = requestBody.token;
                    final var password = requestBody.password;

                    return db.checkAuthAttemptToken(token)
                            .flatMap(__ -> tg.pass2Fa(token, password))
                            .map(Pass2FaResponse::of);
                })
                .defaultIfEmpty(Pass2FaResponse.ERROR);
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class RequestCodeRequest implements Request {
        @Nullable
        final String phone;
    }

    @RequiredArgsConstructor
    private static final class RequestCodeResponse implements Response {
        @NotNull
        final static RequestCodeResponse ERROR = new RequestCodeResponse(RequestCodeResult.ERROR, null);

        @NotNull
        final RequestCodeResult status;
        @Nullable
        final String token;

        static RequestCodeResponse of(@NotNull final RequestCodeResult status, @Nullable final String token) {
            switch (status) {
                case OK:
                    return new RequestCodeResponse(RequestCodeResult.OK, token);
                case ERROR:
                default:
                    return ERROR;
            }
        }
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class SignInRequest implements Request {
        @Nullable
        final String token;
        @Nullable
        final String code;
    }

    @RequiredArgsConstructor
    private static final class SignInResponse implements Response {
        @NotNull
        static final SignInResponse ERROR_2FA_NEEDED = new SignInResponse(SignInResult.TFA_REQUIRED);
        @NotNull
        static final SignInResponse ERROR = new SignInResponse(SignInResult.ERROR);
        @NotNull
        static final SignInResponse OK = new SignInResponse(SignInResult.OK);

        @NotNull
        final SignInResult status;

        @NotNull
        static SignInResponse of(@NotNull final SignInResult status) {
            switch (status) {
                case OK:
                    return OK;
                case TFA_REQUIRED:
                    return ERROR_2FA_NEEDED;
                case ERROR:
                default:
                    return ERROR;
            }
        }
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class Pass2FaRequest implements Request {
        @Nullable
        final String token;
        @Nullable
        final String password;
    }

    @RequiredArgsConstructor
    private static final class Pass2FaResponse implements Response {
        @NotNull
        static final Pass2FaResponse ERROR = new Pass2FaResponse(Pass2FaResult.ERROR);
        @NotNull
        static final Pass2FaResponse OK = new Pass2FaResponse(Pass2FaResult.OK);

        @NotNull
        final Pass2FaResult status;

        @NotNull
        static Pass2FaResponse of(@NotNull final Pass2FaResult status) {
            switch (status) {
                case OK:
                    return OK;
                case ERROR:
                default:
                    return ERROR;
            }
        }
    }
}
