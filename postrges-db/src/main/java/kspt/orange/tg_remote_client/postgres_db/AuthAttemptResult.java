package kspt.orange.tg_remote_client.postgres_db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public final class AuthAttemptResult {
    final boolean success;
    @NotNull
    final String phone;
    @NotNull
    final String token;
}
