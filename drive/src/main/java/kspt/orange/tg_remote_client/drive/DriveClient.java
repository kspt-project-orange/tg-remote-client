package kspt.orange.tg_remote_client.drive;

import com.typesafe.config.Config;
import kspt.orange.tg_remote_client.drive.result.AttachTokenResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

final class DriveClient {
    @NotNull
    private final String token;

    DriveClient(@NotNull final Config config, @NotNull final String token) {
        this.token = token;
    }

    @NotNull
    static Mono<AttachTokenResult> attachToken(@NotNull final String token) {
        return Mono.just(AttachTokenResult.OK);
    }
}
