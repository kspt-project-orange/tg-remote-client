package kspt.orange.tg_remote_client.postgres_db;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.jetbrains.annotations.NotNull;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

public final class Db {
    @NotNull
    private final ConnectionPool pool;

    public Db(@NotNull final Config config) {
        final var host = config.getString("host");
        final var port = config.getInt("port");
        final var user = config.getString("user");
        final var pass = config.getString("password");
        final var database = config.getString("database");

        final var poolMaxIdleTime = config.getDuration("pool.maxIdleTimeMillis");
        final var poolMaxSize = config.getInt("pool.maxSize");

        final var connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql")
                .option(HOST, host)
                .option(PORT, port)
                .option(USER, user)
                .option(PASSWORD, pass)
                .option(DATABASE, database)
                .build());

        final var poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(poolMaxIdleTime)
                .maxSize(poolMaxSize)
                .build();

        this.pool = new ConnectionPool(poolConfiguration);
    }

    public void close() {
        pool.dispose();
    }

    public void f() {
    }

    public static void main(String[] args) {
        final var config = ConfigFactory.load("tg-remote-client.conf");
        final var dbConfig = config.getConfig("tgRemoteClient.db");

        new Db(dbConfig);
    }
}
