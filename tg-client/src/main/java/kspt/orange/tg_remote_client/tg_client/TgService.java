package kspt.orange.tg_remote_client.tg_client;

import com.typesafe.config.Config;
import kspt.orange.tg_remote_client.tg_client.result.Pass2FaResult;
import kspt.orange.tg_remote_client.tg_client.result.RequestCodeResult;
import kspt.orange.tg_remote_client.tg_client.result.SignInResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class TgService {
    @NotNull
    private final Config config;

    @NotNull
    private final ConcurrentHashMap<String, TgClient> clients = new ConcurrentHashMap<>();

    static {
        //Application must be launched with -Djava.library.path=tg-client/libs
        System.loadLibrary("tdjni");
    }

    @NotNull
    public Mono<RequestCodeResult> requestCode(@NotNull final String phone, @NotNull final String token) {
        return client(token).requestCode(phone);
    }

    @NotNull
    public Mono<SignInResult> signIn(@NotNull final String token, @NotNull final String code) {
        return client(token).signIn(code);
    }

    @NotNull
    public Mono<Pass2FaResult> pass2Fa(@NotNull final String token, @NotNull final String password) {
        return client(token).pass2Fa(password);
    }

    @NotNull
    private TgClient client(@NotNull final String token) {
        return clients.computeIfAbsent(token, this::newClient);
    }

    @NotNull
    private TgClient newClient(@NotNull final String directory) {
        return new TgClient(config, directory);
    }
}
