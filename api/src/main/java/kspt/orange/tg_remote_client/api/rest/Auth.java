package kspt.orange.tg_remote_client.api.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import kspt.orange.tg_remote_client.api.util.RequestValidator;
import kspt.orange.tg_remote_client.api.util.TokenGenerator;
import kspt.orange.tg_remote_client.drive.DriveService;
import kspt.orange.tg_remote_client.drive.result.AttachTokenResult;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_client.TgService;
import kspt.orange.tg_remote_client.tg_client.result.Pass2FaResult;
import kspt.orange.tg_remote_client.tg_client.result.RequestCodeResult;
import kspt.orange.tg_remote_client.tg_client.result.SignInResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v0/auth")
public final class Auth implements Api {
    @NotNull
    private final Db db;
    @NotNull
    private final TgService telegram;
    @NotNull
    private final DriveService drive;
    @NotNull
    private final RequestValidator requestValidator;
    @NotNull
    private final TokenGenerator tokenGenerator;

    @PostMapping(path = "/requestCode", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    @NotNull
    public Mono<RequestCodeResponse> requestCode(@RequestBody @NotNull final Mono<RequestCodeRequest> body) {
        return body
                .flatMap(requestValidator::validOrEmpty)
                .flatMap(requestBody -> {
                    final var token = requestBody.token != null
                            ? requestBody.token
                            : tokenGenerator.nextToken();

                    return telegram.requestCode(requestBody.phone, token)
                            .map(result -> RequestCodeResponse.of(result, token));
                })
                .defaultIfEmpty(RequestCodeResponse.ERROR)
                .onErrorReturn(Auth::logging, RequestCodeResponse.ERROR);
    }

    @PostMapping(path = "/signIn", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    @NotNull
    public Mono<SignInResponse> signIn(@RequestBody @NotNull final Mono<SignInRequest> body) {
        return body
                .flatMap(requestValidator::validOrEmpty)
                .flatMap(requestBody -> telegram.signIn(requestBody.token, requestBody.code)
                        .flatMap(result -> {
                            if (result == SignInResult.OK) {
                                return db.addSession(requestBody.token)
                                        .thenReturn(SignInResponse.OK);
                            }

                            return Mono.just(SignInResponse.of(result));
                        }))
                .defaultIfEmpty(SignInResponse.ERROR)
                .onErrorReturn(Auth::logging, SignInResponse.ERROR);
    }

    @PostMapping(path = "/pass2FA", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    @NotNull
    public Mono<Pass2FaResponse> pass2Fa(@RequestBody @NotNull final Mono<Pass2FaRequest> body) {
        return body
                .flatMap(requestValidator::validOrEmpty)
                .flatMap(requestBody -> telegram.pass2Fa(requestBody.token, requestBody.password)
                        .flatMap(result -> {
                            if (result == Pass2FaResult.OK) {
                                return db.addSession(requestBody.token)
                                        .thenReturn(Pass2FaResponse.OK);
                            }

                            return Mono.just(Pass2FaResponse.of(result));
                        }))
                .defaultIfEmpty(Pass2FaResponse.ERROR)
                .onErrorReturn(Auth::logging, Pass2FaResponse.ERROR);
    }

    @PostMapping(path = "/attachDrive", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    @NotNull
    public Mono<AttachDriveResponse> attachDrive(@RequestHeader(name = "User-Agent") @NotNull final String userAgent,
                                                 @RequestBody @NotNull final Mono<AttachDriveRequest> body) {
        return body
                .flatMap(requestValidator::validOrEmpty)
                .flatMap(requestBody -> drive.attachToken(requestBody.token, requestBody.driveIdToken, requestBody.driveServerAuthCode))
                .map(AttachDriveResponse::new)
                .defaultIfEmpty(AttachDriveResponse.ERROR)
                .onErrorReturn(Auth::logging, AttachDriveResponse.ERROR);
    }

    private static <T extends Throwable> boolean logging(@NotNull final T throwable) {
        log.info("Error occurred", throwable);
        return true;
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class RequestCodeRequest implements Request {
        @NotNull
        final String phone;
        @Nullable
        @RequestValidator.OptionalField
        final String token;
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
        @NotNull
        final String token;
        @NotNull
        final String code;
    }

    @RequiredArgsConstructor
    private static final class SignInResponse implements Response {
        @NotNull
        static final SignInResponse TFA_REQUIRED = new SignInResponse(SignInResult.TFA_REQUIRED);
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
                    return TFA_REQUIRED;
                case ERROR:
                default:
                    return ERROR;
            }
        }
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class Pass2FaRequest implements Request {
        @NotNull
        final String token;
        @NotNull
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

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class AttachDriveRequest implements Request {
        @NotNull
        final String token;
        @NotNull
        final String driveIdToken;
        @NotNull
        final String driveServerAuthCode;
    }

    @RequiredArgsConstructor
    private static final class AttachDriveResponse implements Response {
        @NotNull
        static final AttachDriveResponse OK = new AttachDriveResponse(AttachTokenResult.OK);
        @NotNull
        static final AttachDriveResponse NOT_ENOUGH_RIGHTS = new AttachDriveResponse(AttachTokenResult.NOT_ENOUGH_RIGHTS);
        @NotNull
        static final AttachDriveResponse ERROR = new AttachDriveResponse(AttachTokenResult.ERROR);

        @NotNull
        final AttachTokenResult status;

        @NotNull
        static AttachDriveResponse of(@NotNull final AttachTokenResult status) {
            switch (status) {
                case OK:
                    return OK;
                case NOT_ENOUGH_RIGHTS:
                    return NOT_ENOUGH_RIGHTS;
                case ERROR:
                default:
                    return ERROR;
            }
        }
    }
}
