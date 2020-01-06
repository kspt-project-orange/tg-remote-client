package kspt.orange.tg_remote_client.drive;

import com.typesafe.config.Config;
import kspt.orange.tg_remote_client.drive.result.AttachTokenResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
final public class DriveService {
    @NotNull
    private final Config config;
    @NotNull
    private final ConcurrentHashMap<String, DriveClient> clients = new ConcurrentHashMap<>();

    @NotNull
    public Mono<AttachTokenResult> attachToken(@NotNull final String token) {
        return DriveClient.attachToken(token)
                .map(result -> {
                    if (result == AttachTokenResult.OK) {
                        clients.put(token, new DriveClient(config, token));
                    }

                    return result;
                });
    }
}
