package kspt.orange.tg_remote_client.postgres_db;

import com.typesafe.config.Config;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
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
    private static final Mono<Boolean> MONO_TRUE = Mono.just(Boolean.TRUE);
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

    public void close() {
        pool.dispose();
    }

    public Mono<Boolean> addAuthAttempt(@NotNull final String phone, @NotNull final String token) {
//        return pool.create()
//                .flatMap(connection -> {
//                    final var uid = Mono.from(connection
//                            .createStatement("INSERT INTO \"user\" (gender, age, first_language) VALUES ($1, $2, $3)")
//                            .bind("$1", gender)
//                            .bind("$2", age)
//                            .bind("$3", firstLanguage)
//                            .returnGeneratedValues("id")
//                            .execute())
//                            .flatMap(result -> Mono.from(result.map((row, __) -> row.get("id", Long.class))));
//
//                    return Mono.from(connection.close()).then(uid);
//                });
        return MONO_TRUE;
    }

    public Mono<Boolean> checkAuthAttemptToken(@NotNull final String token) {
//        return pool.create()
//                .flatMap(connection -> {
//                    final var uid = Mono.from(connection
//                            .createStatement("INSERT INTO \"user\" (gender, age, first_language) VALUES ($1, $2, $3)")
//                            .bind("$1", gender)
//                            .bind("$2", age)
//                            .bind("$3", firstLanguage)
//                            .returnGeneratedValues("id")
//                            .execute())
//                            .flatMap(result -> Mono.from(result.map((row, __) -> row.get("id", Long.class))));
//
//                    return Mono.from(connection.close()).then(uid);
//                });
        return MONO_TRUE;
    }
}
