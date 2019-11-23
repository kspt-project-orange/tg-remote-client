package kspt.orange.tg_remote_client.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//TODO
@SuppressWarnings("unused")
@RestController
@RequestMapping("/auth")
public final class Auth implements Api {
    @PostMapping(path = "/requestCode", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<? extends Response> requestCode(@RequestBody @NotNull final Mono<RequestCodeRequest> body) {
        return body.flatMap(request -> {
            if (request.isValid()) {
                return Mono.just(RequestCodeResponse.ok("jklj3kl4hj12l5hlhjh14kj5h21kl3j4l12h351j24h1kl45b1kjh32lk5h12;465l2k53"));
            }

            return Mono.just(RequestCodeResponse.ERROR);
        });
    }

    @PostMapping(path = "/signIn", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<? extends Response> signIn(@RequestBody @NotNull final Mono<SignInRequest> body) {
        return body.flatMap(request -> {
            if (request.isValid()) {
                return Mono.just(SignInResponse.ok("jkj13k2j4klhjlj1243lj5hkj1h3jkhl;hklj1lk3l51jlk"));
            }

            return Mono.just(SignInResponse.ERROR);
        });
    }

    @PostMapping(path = "/pass2FA", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public Mono<? extends Response> pass2Fa(@RequestBody @NotNull final Mono<Pass2FaRequest> body) {
        return body.flatMap(request -> {
            if (request.isValid()) {
                return Mono.just(Pass2FaResponse.ok("jhkljh1j4h5jkh1jk24h5kj21h4kj5h12jk4h5klj12hk5j"));
            }
            return Mono.just(Pass2FaResponse.ERROR);
        });
    }

    private static final class RequestCodeRequest implements Request {
        @Nullable
        private final String phone;

        @JsonCreator
        RequestCodeRequest(@Nullable @JsonProperty("phone") final String phone) {
            this.phone = phone;
        }
    }

    private static final class RequestCodeResponse implements Response {
        @NotNull
        @JsonIgnore
        private final static String STATUS_OK = "OK";
        @NotNull
        @JsonIgnore
        private final static String STATUS_ERROR = "ERROR";
        @NotNull
        @JsonIgnore
        private final static RequestCodeResponse ERROR = new RequestCodeResponse(STATUS_ERROR, null);

        @NotNull
        @JsonProperty("status")
        private final String status;
        @Nullable
        @JsonProperty("authAttemptToken")
        private final String authAttemptToken;

        private static RequestCodeResponse ok(@NotNull final String authAttemptToken) {
            return new RequestCodeResponse(STATUS_OK, authAttemptToken);
        }

        private RequestCodeResponse(@NotNull final String status, @Nullable final String authAttemptToken) {
            this.status = status;
            this.authAttemptToken = authAttemptToken;
        }
    }

    private static final class SignInRequest implements Request {
        @Nullable
        private final String authAttemptToken;
        @Nullable
        private final String phone;
        @Nullable
        private final String code;

        @JsonCreator
        private SignInRequest(@Nullable @JsonProperty("authAttemptToken") final String authAttemptToken,
                              @Nullable @JsonProperty("phone") final String phone,
                              @Nullable @JsonProperty("code") final String code) {
            this.authAttemptToken = authAttemptToken;
            this.phone = phone;
            this.code = code;
        }
    }

    private static final class SignInResponse implements Response {
        @NotNull
        @JsonIgnore
        private static final SignInResponse ERROR_2FA_NEEDED = new SignInResponse(Status.ERROR_2FA_NEEDED, null);
        @NotNull
        @JsonIgnore
        private static final SignInResponse ERROR = new SignInResponse(Status.ERROR, null);

        @NotNull
        @JsonProperty("status")
        private final String status;
        @Nullable
        @JsonProperty("authPermanentToken")
        private final String authPermanentToken;

        private static SignInResponse ok(@NotNull final String authPermanentToken) {
            return new SignInResponse(Status.OK, authPermanentToken);
        }

        private SignInResponse(@NotNull final Status status, @Nullable final String authPermanentToken) {
            this.status = status.toString();
            this.authPermanentToken = authPermanentToken;
        }

        private enum Status {
            OK,
            ERROR_2FA_NEEDED,
            ERROR,
        }
    }

    private static final class Pass2FaRequest implements Request {
        @Nullable
        private final String authAttemptToken;
        @Nullable
        private final String password;

        @JsonCreator
        private Pass2FaRequest(@Nullable @JsonProperty("authAttemptToken") final String authAttemptToken,
                               @Nullable @JsonProperty("password") final String password) {
            this.authAttemptToken = authAttemptToken;
            this.password = password;
        }
    }

    private static final class Pass2FaResponse implements Response {
        @NotNull
        @JsonIgnore
        private static final Pass2FaResponse ERROR = new Pass2FaResponse(Status.ERROR, null);

        @NotNull
        @JsonProperty("status")
        private final String status;
        @Nullable
        @JsonProperty("authPermanentToken")
        private final String authPermanentToken;

        private static Pass2FaResponse ok(@NotNull final String authPermanentToken) {
            return new Pass2FaResponse(Status.OK, authPermanentToken);
        }

        private Pass2FaResponse(@NotNull final Status status, @Nullable final String authPermanentToken) {
            this.status = status.toString();
            this.authPermanentToken = authPermanentToken;
        }

        private enum Status {
            OK,
            ERROR,
        }
    }
}
