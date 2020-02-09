package kspt.orange.tg_remote_client.tg_to_drive;

import kspt.orange.tg_remote_client.drive.DriveService;
import kspt.orange.tg_remote_client.tg_client.TgService;
import kspt.orange.tg_remote_client.postgres_db.Db;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TgToDriveService {
    @NotNull
    private final TgService telegram;
    @NotNull
    private final DriveService drive;
    @NotNull
    private final Db db;
}
