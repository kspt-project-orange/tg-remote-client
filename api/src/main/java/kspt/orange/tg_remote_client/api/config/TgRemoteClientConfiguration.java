package kspt.orange.tg_remote_client.api.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kspt.orange.tg_remote_client.api.rest.TgToDrive;
import kspt.orange.tg_remote_client.api.util.RequestValidator;
import kspt.orange.tg_remote_client.api.util.TokenGenerator;
import kspt.orange.tg_remote_client.drive.DriveService;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_client.TgService;
import kspt.orange.tg_remote_client.tg_to_drive.TgToDriveService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.image.DataBuffer;

import static kspt.orange.tg_remote_client.api.util.TokenGenerator.Mode.DIGITS;
import static kspt.orange.tg_remote_client.api.util.TokenGenerator.Mode.FEW_SPECIAL_SYMBOLS;
import static kspt.orange.tg_remote_client.api.util.TokenGenerator.Mode.LETTERS;

@SuppressWarnings("unused")
@Configuration
public class TgRemoteClientConfiguration {
    @Bean
    @NotNull
    public Config apiConfig() {
        return ConfigFactory.load("tg-remote-client.conf");
    }

    @Bean
    @NotNull
    public Db db(@NotNull final Config apiConfig) {
        final var dbConfig = apiConfig.getConfig("tgRemoteClient.db");

        return new Db(dbConfig);
    }

    @Bean
    @NotNull
    public TgService telegram(@NotNull final Config apiConfig) {
        final var tgConfig = apiConfig.getConfig("tgRemoteClient.tg");

        return new TgService(tgConfig);
    }

    @Bean
    @NotNull
    public DriveService drive(@NotNull final Config apiConfig) {
        final var driveConfig = apiConfig.getConfig("tgRemoteClient.drive");

        return new DriveService(driveConfig);
    }

    @Bean
    @NotNull
    public RequestValidator requestValidator() {
        return new RequestValidator();
    }

    @Bean
    @NotNull
    public TokenGenerator tokenGenerator() {
        return new TokenGenerator(LETTERS | DIGITS | FEW_SPECIAL_SYMBOLS, 128);
    }

    @Bean
    @NotNull
    public TgToDriveService telegramToDrive(@NotNull final TgService telegram,
                                            @NotNull final DriveService drive,
                                            @NotNull final Db db) {
        return new TgToDriveService(telegram, drive, db);
    }
}
