package kspt.orange.tg_remote_client.tg_client;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
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
    private Mono<Client> client;

    public TgClient(@NotNull final Config config, @NotNull final String directory) {
        apiId = config.getInt("apiId");
        apiHash = config.getString("apiHash");
        appVersion = config.getString("appVersion");
        dbDir = Paths.get(config.getString("dbRootDir"), directory).toString();

        client = Mono.just(Client.create(this::handleTdLibEvent, null, null));
    }

    public Mono<Void> dispose() {
        return client.flatMap(client -> { client.close(); return null; });
    }

    public Mono<RequestCodeResult> requestCode(@NotNull final String phone) {
        return Mono.just(new RequestCodeResult(true, phone, ""));
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
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = dbDir;
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = apiId;
                parameters.apiHash = apiHash;
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Desktop";
                parameters.systemVersion = "Unknown";
                parameters.applicationVersion = appVersion;
                parameters.enableStorageOptimizer = true;
        }
    }
}
