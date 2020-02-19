package kspt.orange.tg_remote_client.postgres_db;

import com.typesafe.config.Config;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import kspt.orange.rg_remote_client.commons.reactor.Monos;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.INITIAL_SIZE;
import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
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
        final var poolMinSize = config.getInt("pool.minSize");

        final var connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql")
                .option(HOST, host)
                .option(PORT, port)
                .option(USER, user)
                .option(PASSWORD, pass)
                .option(DATABASE, database)
                .option(INITIAL_SIZE, poolMinSize)
                .option(MAX_SIZE, poolMaxSize)
                .build());

        final var poolConfiguration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(poolMaxIdleTime)
                .maxSize(poolMaxSize)
                .build();

        pool = new ConnectionPool(poolConfiguration);
    }

    @NotNull
    public Mono<Void> close() {
        return Mono.fromRunnable(pool::dispose);
    }

    @NotNull
    public Mono<Boolean> isValidToken(@NotNull final String token) {
        return pool
                .create()
                .flatMap(connection -> {
                    final var hasToken = Mono.from(connection
                            .createStatement("SELECT id FROM session WHERE token = $1")
                            .bind("$1", token)
                            .execute())
                            .flatMap(result -> Mono.from(result.map((__, $) -> Boolean.TRUE)))
                            .defaultIfEmpty(Boolean.FALSE);

                    return Mono.from(connection.close()).then(hasToken);
                });
    }

    @NotNull
    public Mono<?> addSession(@NotNull final String token) {
        return pool.create()
                .flatMap(connection -> {
                    final var tokenAdded = Mono.from(connection
                            .createStatement("INSERT INTO session (token) VALUES ($1)")
                            .bind("$1", token)
                            .execute())
                            .flatMap(result -> Mono.from(result
                                    .getRowsUpdated())
                                    .flatMap(updatedRowCount -> !Integer.valueOf(1).equals(updatedRowCount)
                                            ? Monos.NOT_EMPTY
                                            : Mono.empty()));

                    return Mono.from(connection.close()).then(tokenAdded);
                });
    }
}
