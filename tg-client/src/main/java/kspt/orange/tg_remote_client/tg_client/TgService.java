package kspt.orange.tg_remote_client.tg_client;

import com.typesafe.config.Config;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class TgService {
    @NotNull
    private static final Mono<Boolean> MONO_TRUE = Mono.just(Boolean.TRUE);
    @NotNull
    private final Config config;

    @NotNull
    private final ConcurrentHashMap<String, TgClient> clients = new ConcurrentHashMap<>();

    static {
        //Application must be launched with -Djava.library.path=tg-client/libs
        System.loadLibrary("tdjni");
    }

    @NotNull
    public Mono<Boolean> requestCode(@NotNull final String phone, @NotNull final String token) {
        final var client = getClient(token);

        return client.requestCode(phone)
                .flatMap(this::trueOrEmpty);
    }

    @NotNull
    public Mono<Boolean> signIn(@NotNull final String phone, @NotNull final String token, @NotNull final String code) {
        final var client = getClient(token);

        return client.signIn(code)
                .flatMap(this::trueOrEmpty);
    }

    @NotNull
    private Mono<Boolean> trueOrEmpty(@NotNull final Boolean isTrue) {
        return isTrue ? MONO_TRUE : Mono.empty();
    }

    @NotNull
    private TgClient getClient(@NotNull final String token) {
        return clients.computeIfAbsent(token, this::newClient);
    }

    @NotNull
    private TgClient newClient(@NotNull final String directory) {
        return new TgClient(config, directory);
    }
}
