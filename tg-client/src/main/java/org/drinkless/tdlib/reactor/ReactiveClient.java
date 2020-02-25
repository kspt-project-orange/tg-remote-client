package org.drinkless.tdlib.reactor;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

@Slf4j
public final class ReactiveClient {
    @NotNull
    private static final TdApi.Error TD_API_ERROR = new TdApi.Error();
    @NotNull
    private final TdApi.TdlibParameters params;
    @NotNull
    private final Client client;
    @NotNull
    private volatile AuthState authState = AuthState.INITIAL;
    @NotNull
    private final CountDownLatch authStateLatch = new CountDownLatch(1);

    public ReactiveClient(@NotNull final Config config, @NotNull final String directory) {
        this.params = params(config, directory);
        this.client = Client.create(this::handleTdLibEvent, null, null);
    }

    @NotNull
    public Mono<Void> close() {
        return Mono.fromRunnable(client::close);
    }

    @NotNull
    public Mono<TdApi.Object> send(@NotNull final TdApi.Function query) {
        return Mono.create(sink -> {
            try {
                authStateLatch.await();
            } catch (InterruptedException e) {
                log.error("authStateLatch.await()", e);
                sink.success(TD_API_ERROR);
                return;
            }

            if (authState == AuthState.INITIAL) {
                sink.success(TD_API_ERROR);
                return;
            }

            client.send(query, sink::success);
        });
    }

    @NotNull
    public AuthState authState() {
        return authState;
    }

    private void handleTdLibEvent(@NotNull final TdApi.Object event) {
        switch (event.getConstructor()) {
            case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) event).authorizationState);
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
                client.send(new TdApi.SetTdlibParameters(params), null);
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                client.send(new TdApi.CheckDatabaseEncryptionKey(), null);
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                authState = AuthState.WAITING_FOR_PHONE;
                authStateLatch.countDown();
                break;
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                authState = AuthState.WAITING_FOR_CODE;
                authStateLatch.countDown();
                break;
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR:
                authState = AuthState.WAITING_FOR_PASSWORD;
                authStateLatch.countDown();
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                authState = AuthState.INITIAL;
                authStateLatch.countDown();
                break;
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                authState = AuthState.READY;
                authStateLatch.countDown();
                break;
            case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR:
                authState = AuthState.NOT_REGISTERED;
                authStateLatch.countDown();
                break;
            default:
                authState = AuthState.UNKNOWN;
                authStateLatch.countDown();
                assert false : "Unknown auth state";
                break;
        }
    }

    @NotNull
    private TdApi.TdlibParameters params(@NotNull final Config config, @NotNull final String directory) {
        final var params = new TdApi.TdlibParameters();
        params.databaseDirectory = Paths.get(config.getString("dbRootDir"), directory).toString();
        params.useMessageDatabase = true;
        params.useSecretChats = true;
        params.apiId = config.getInt("apiId");
        params.apiHash = config.getString("apiHash");
        params.systemLanguageCode = "en";
        params.deviceModel = "Desktop";
        params.systemVersion = "Ubuntu";
        params.applicationVersion = config.getString("appVersion");
        params.enableStorageOptimizer = true;
        params.useTestDc = config.getBoolean("useTestDc");

        if (config.getBoolean("disableLogging")) {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
            if (Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27))) instanceof TdApi.Error) {
                throw new IOError(new IOException("Write access to the current directory is required"));
            }
        }

        return params;
    }

    private static <T extends Throwable> boolean logging(@NotNull final T error) {
        log.info("Error occurred", error);
        return true;
    }

    public enum AuthState {
        INITIAL,
        UNKNOWN,
        WAITING_FOR_PHONE,
        WAITING_FOR_CODE,
        WAITING_FOR_PASSWORD,
        NOT_REGISTERED,
        READY,
        ;
    }
}
