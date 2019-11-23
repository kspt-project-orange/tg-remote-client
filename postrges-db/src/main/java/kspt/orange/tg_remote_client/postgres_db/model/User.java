package kspt.orange.tg_remote_client.postgres_db.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public final class User {
    private final long id;
    @NotNull
    private final String phone;
}
