package kspt.orange.tg_remote_client.postgres_db.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public final class Document {
    private final long id;
    private final long messageId;
    @NotNull
    private final String name;
    @NotNull
    private final String url;
}
