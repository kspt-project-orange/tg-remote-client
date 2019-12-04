package kspt.orange.tg_remote_client.tg_client;

import com.typesafe.config.Config;
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

    public Mono<RequestCodeResult> requestCode(@NotNull final String phone, @NotNull final String token) {
        //TODO: pass token to client, use it as a part of db directory
        final var client = clients.computeIfAbsent(token, key -> new TgClient(config, key));

        return client.requestCode(phone);
    }
}
