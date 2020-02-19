package kspt.orange.tg_remote_client.tg_client;

import com.typesafe.config.Config;
import kspt.orange.tg_remote_client.tg_client.result.Pass2FaResult;
import kspt.orange.tg_remote_client.tg_client.result.RequestCodeResult;
import kspt.orange.tg_remote_client.tg_client.result.SignInResult;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.reactor.ReactiveClient;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;

import static org.drinkless.tdlib.reactor.ReactiveClient.AuthState.READY;
import static org.drinkless.tdlib.reactor.ReactiveClient.AuthState.WAITING_FOR_CODE;
import static org.drinkless.tdlib.reactor.ReactiveClient.AuthState.WAITING_FOR_PASSWORD;

@Slf4j
final class TgClient {
    @NotNull
    private final ReactiveClient client;

    TgClient(@NotNull final Config config, @NotNull final String directory, @NotNull final ExecutorService syncOperationExecutor) {
        client = new ReactiveClient(config, directory, syncOperationExecutor);
    }

    @NotNull
    Mono<Void> close() {
        return client.close();
    }

    @NotNull
    Mono<RequestCodeResult> requestCode(@NotNull final String phone) {
        return client.send(new TdApi.SetAuthenticationPhoneNumber(phone, null))
                .map(result -> {
                    if (result.getConstructor() != TdApi.Ok.CONSTRUCTOR || client.authState() != WAITING_FOR_CODE) {
                        return RequestCodeResult.ERROR;
                    }

                    return RequestCodeResult.OK;
                });
    }

    @NotNull
    Mono<SignInResult> signIn(@NotNull final String code) {
        return client.send(new TdApi.CheckAuthenticationCode(code))
                .map(result -> {
                    if (result.getConstructor() != TdApi.Ok.CONSTRUCTOR) {
                        return SignInResult.ERROR;
                    }

                    if (client.authState() == WAITING_FOR_PASSWORD) {
                        return SignInResult.TFA_REQUIRED;
                    }

                    if (client.authState() == READY) {
                        return SignInResult.OK;
                    }

                    return SignInResult.ERROR;
                });
    }

    @NotNull
    Mono<Pass2FaResult> pass2Fa(@NotNull final String password) {
        return client.send(new TdApi.CheckAuthenticationPassword(password))
                .map(result -> {
                    if (result.getConstructor() != TdApi.Ok.CONSTRUCTOR || client.authState() != READY) {
                        return Pass2FaResult.ERROR;
                    }

                    return Pass2FaResult.OK;
                });
    }
}
