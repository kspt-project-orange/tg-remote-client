package kspt.orange.tg_remote_client.tg_to_drive;

import kspt.orange.tg_remote_client.drive.DriveService;
import kspt.orange.tg_remote_client.tg_client.TgService;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_to_drive.result.StartProcessingResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TgToDriveService {
    @NotNull
    private final TgService telegram;
    @NotNull
    private final DriveService drive;
    @NotNull
    private final Db db;

    @NotNull
    public Mono<Boolean> isProcessing(@NotNull final String token) {
        return Mono.just(Boolean.TRUE);
    }

    @NotNull
    public Mono<StartProcessingResult> startProcessing(@NotNull final String token) {
        return Mono.just(StartProcessingResult.OK);
    }
}
