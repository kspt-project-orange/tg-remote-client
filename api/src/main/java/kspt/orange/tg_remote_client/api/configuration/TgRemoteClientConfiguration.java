package kspt.orange.tg_remote_client.api.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kspt.orange.tg_remote_client.api.util.TokenGenerator;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_client.TgService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static kspt.orange.tg_remote_client.api.util.TokenGenerator.Mode.ALL_SYMBOLS;

@SuppressWarnings("unused")
@Configuration
public class TgRemoteClientConfiguration {
    static {
        System.setProperty("java.library.path", "tg-client/libs");
    }

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

    @NotNull
    @Bean
    public TokenGenerator tokenGenerator() {
        return new TokenGenerator(ALL_SYMBOLS, 128);
    }
}
