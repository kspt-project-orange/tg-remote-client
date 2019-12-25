package kspt.orange.tg_remote_client.tg_client;

import com.typesafe.config.Config;
import kspt.orange.tg_remote_client.tg_client.util.Synchronized;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;

//@Slf4j
public final class TgClient {
    private final int apiId;
    @NotNull
    private final String apiHash;
    @NotNull
    private final String appVersion;
    @NotNull
    private final String dbDir;

    @NotNull
    private Client client;
    @NotNull
    private final Synchronized<String> phone = Synchronized.acquired();
    @NotNull
    private final Synchronized<String> code = Synchronized.acquired();
    @NotNull
    private final Synchronized<String> password = Synchronized.acquired();
    @NotNull
    private final Synchronized<AuthState> authState = Synchronized.acquired();

    public TgClient(@NotNull final Config config, @NotNull final String directory) {
        apiId = config.getInt("apiId");
        apiHash = config.getString("apiHash");
        appVersion = config.getString("appVersion");
        dbDir = Paths.get(config.getString("dbRootDir"), directory).toString();

        client = Client.create(this::handleTdLibEvent, null, null);
    }

    public Mono<Void> dispose() {
//        return client.flatMap(client -> { client.close(); return Mono.empty(); });
        client.close();
        return Mono.empty();
    }

    @NotNull
    public Mono<Boolean> requestCode(@NotNull final String phone) {
        return Mono.fromSupplier(() -> {
            final var state = authState.await();
            if (state != AuthState.WAITING_FOR_PHONE) {
                return false;
            }

            authState.acquire();
            this.phone.leave(phone);

            return authState.await() == AuthState.WAITING_FOR_CODE;
        });
    }

    @NotNull
    public Mono<Boolean> signIn(@NotNull final String code) {
        return Mono.fromSupplier(() -> {
            final var state = authState.await();
            if (state != AuthState.WAITING_FOR_CODE) {
                return false;
            }

            authState.acquire();
            this.code.leave(code);

            final var newState = authState.await();
            return newState == AuthState.WAITING_FOR_PASSWORD || newState == AuthState.READY;
        });
    }

    private void handleTdLibEvent(@NotNull final TdApi.Object event) {
        switch (event.getConstructor()) {
            case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) event).authorizationState);
                break;
            case TdApi.Ok.CONSTRUCTOR:
                break;
            default:
                break;
        }
    }

    private void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState == null) {
            return;
        }

        switch (authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = dbDir;
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = apiId;
                parameters.apiHash = apiHash;
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Desktop";
                parameters.systemVersion = "Ubuntu";
                parameters.applicationVersion = appVersion;
                parameters.enableStorageOptimizer = true;

                client.send(new TdApi.SetTdlibParameters(parameters), null);
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                client.send(new TdApi.CheckDatabaseEncryptionKey(), null);
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                authState.leave(AuthState.WAITING_FOR_PHONE);

                final var phoneNumber = phone.await();
                client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), this::onAuthResult);
                break;
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                authState.leave(AuthState.WAITING_FOR_CODE);

                final var code = this.code.await();
                client.send(new TdApi.CheckAuthenticationCode(code), this::onAuthResult);
                break;
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR:
                authState.leave(AuthState.WAITING_FOR_PASSWORD);

                final var pass = password.await();
                client.send(new TdApi.CheckAuthenticationPassword(pass), this::onAuthResult);
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                authState.leave(AuthState.INITIAL);
                break;
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                authState.leave(AuthState.READY);
                break;
            case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR:
                //fallthrough
            default:
                authState.leave(AuthState.NOT_REGISTERED);
                break;
        }
    }

    private void onAuthResult(TdApi.Object object) {
        if (object.getConstructor() == TdApi.Ok.CONSTRUCTOR) {
            return;
        }
    }

    public enum AuthState {
        INITIAL,
        WAITING_FOR_PHONE,
        WAITING_FOR_CODE,
        WAITING_FOR_PASSWORD,
        NOT_REGISTERED,
        READY,
    }
}
