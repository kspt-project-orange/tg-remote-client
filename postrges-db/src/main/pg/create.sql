CREATE TABLE IF NOT EXISTS "user"
(
    id    BIGSERIAL PRIMARY KEY,
    phone VARCHAR(15) NOT NULL -- ITU E.164: up to 15 digits
);

CREATE TABLE IF NOT EXISTS user_session
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT  NOT NULL,
    token   VARCHAR NOT NULL,

    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS chat
(
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS chat_user
(
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT series_translation_pk PRIMARY KEY (chat_id, user_id),

    FOREIGN KEY (chat_id) REFERENCES chat (id),
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS message
(
    id        BIGSERIAL PRIMARY KEY,
    chat_id   BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,

    FOREIGN KEY (chat_id) REFERENCES chat (id),
    FOREIGN KEY (sender_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS photo
(
    id         BIGSERIAL PRIMARY KEY,
    message_id BIGINT       NOT NULL,
    name       VARCHAR(256) NOT NULL,
    url        VARCHAR(256) NOT NULL,

    FOREIGN KEY (message_id) REFERENCES message (id)
);

CREATE TABLE IF NOT EXISTS video
(
    id         BIGSERIAL PRIMARY KEY,
    message_id BIGINT       NOT NULL,
    name       VARCHAR(256) NOT NULL,
    url        VARCHAR(256) NOT NULL,

    FOREIGN KEY (message_id) REFERENCES message (id)
);

CREATE TABLE IF NOT EXISTS audio
(
    id         BIGSERIAL PRIMARY KEY,
    message_id BIGINT       NOT NULL,
    name       VARCHAR(256) NOT NULL,
    url        VARCHAR(256) NOT NULL,

    FOREIGN KEY (message_id) REFERENCES message (id)
);

CREATE TABLE IF NOT EXISTS document
(
    id         BIGSERIAL PRIMARY KEY,
    message_id BIGINT       NOT NULL,
    name       VARCHAR(256) NOT NULL,
    url        VARCHAR(256) NOT NULL,

    FOREIGN KEY (message_id) REFERENCES message (id)
);

CREATE TABLE IF NOT EXISTS link
(
    id         BIGSERIAL PRIMARY KEY,
    message_id BIGINT       NOT NULL,
    url        VARCHAR(256) NOT NULL,

    FOREIGN KEY (message_id) REFERENCES message (id)
);
