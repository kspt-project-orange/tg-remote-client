package kspt.orange.tg_remote_client.api;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kspt.orange.tg_remote_client.postgres_db.Db;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TgRemoteClientService {
    public static void main(String[] args) {
        SpringApplication.run(TgRemoteClientService.class, args);
    }

    @NotNull
    @Bean
    public Config apiConfig() {
        return ConfigFactory.load("tg-remote-client.conf");
    }

    @NotNull
    @Bean
    public Db db() {
        final var apiConfig = apiConfig();
        final var dbConfig = apiConfig.getConfig("tgRemoteClient.db");

        return new Db(dbConfig);
    }
}
