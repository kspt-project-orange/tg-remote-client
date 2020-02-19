package kspt.orange.tg_remote_client.tg_client;

import com.typesafe.config.Config;
import kspt.orange.tg_remote_client.tg_client.result.Pass2FaResult;
import kspt.orange.tg_remote_client.tg_client.result.RequestCodeResult;
import kspt.orange.tg_remote_client.tg_client.result.SignInResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class TgService {
    @NotNull
    private final Config config;
    @NotNull
    private final ExecutorService syncOperationExecutor;

    @NotNull
    private final ConcurrentHashMap<String, TgClient> clients = new ConcurrentHashMap<>();

    static {
        /// Application must be launched with -Djava.library.path=tg-client/libs
        System.loadLibrary("tdjni");
    }

    public TgService(@NotNull final Config config) {
        this.config = config;
        this.syncOperationExecutor = scalableThreadPool(config.getConfig("syncPool"));
    }

    @NotNull
    public Mono<RequestCodeResult> requestCode(@NotNull final String phone, @NotNull final String token) {
        return clients.computeIfAbsent(token, this::newClient).requestCode(phone);
    }

    @NotNull
    public Mono<SignInResult> signIn(@NotNull final String token, @NotNull final String code) {
        final var client = clients.get(token);

        return client != null ? client.signIn(code) : Mono.empty();
    }

    @NotNull
    public Mono<Pass2FaResult> pass2Fa(@NotNull final String token, @NotNull final String password) {
        final var client = clients.get(token);

        return client != null ? client.pass2Fa(password) : Mono.empty();
    }

    @NotNull
    private TgClient newClient(@NotNull final String directory) {
        return new TgClient(config, directory, syncOperationExecutor);
    }

    @NotNull
    private static ExecutorService scalableThreadPool(@NotNull final Config config) {
        final var minThreadCount = config.getInt("minThreadCount");
        final var maxThreadCount = config.getInt("maxThreadCount");
        final var maxIdleMillis = config.getInt("maxIdleMillis");

        return new ThreadPoolExecutor(
                minThreadCount,
                maxThreadCount,
                maxIdleMillis,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(minThreadCount)
        );
    }
}
