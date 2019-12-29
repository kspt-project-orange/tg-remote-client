package kspt.orange.tg_remote_client.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kspt.orange.tg_remote_client.api.util.RequestValidator;
import kspt.orange.tg_remote_client.api.util.TokenGenerator;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_client.TgService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static kspt.orange.tg_remote_client.api.util.TokenGenerator.Mode.DIGITS;
import static kspt.orange.tg_remote_client.api.util.TokenGenerator.Mode.FEW_SPECIAL_SYMBOLS;
import static kspt.orange.tg_remote_client.api.util.TokenGenerator.Mode.LETTERS;

@SuppressWarnings("unused")
@Configuration
public class TgRemoteClientConfiguration {
    @NotNull
    @Bean
    public Config apiConfig() {
        return ConfigFactory.load("tg-remote-client.conf");
    }

    @NotNull
    @Bean
    public Db db(@NotNull final Config apiConfig) {
        final var dbConfig = apiConfig.getConfig("tgRemoteClient.db");

        return new Db(dbConfig);
    }

    @NotNull
    @Bean
    public TgService tg(@NotNull final Config apiConfig) {
        final var tgConfig = apiConfig.getConfig("tgRemoteClient.tg");

        return new TgService(tgConfig);
    }

    @Bean
    @NotNull
    public RequestValidator requestValidator() {
        return new RequestValidator();
    }

    @NotNull
    @Bean
    public TokenGenerator tokenGenerator() {
        return new TokenGenerator(LETTERS | DIGITS | FEW_SPECIAL_SYMBOLS, 128);
    }
}
