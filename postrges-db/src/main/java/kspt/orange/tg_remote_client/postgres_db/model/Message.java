package kspt.orange.tg_remote_client.postgres_db.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Message {
    private final long id;
    private final long chatId;
    private final long senderId;
}
