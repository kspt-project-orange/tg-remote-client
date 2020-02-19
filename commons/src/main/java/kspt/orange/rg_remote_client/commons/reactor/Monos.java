package kspt.orange.rg_remote_client.commons.reactor;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public final class Monos {
    @NotNull
    public static final Mono<Boolean> TRUE = Mono.just(Boolean.TRUE);
    @NotNull
    public static final Mono<Boolean> FALSE = Mono.just(Boolean.FALSE);
    @NotNull
    public static final Mono<?> NOT_EMPTY = Mono.just(new Object());

    private Monos() {
        throw new RuntimeException("Not instantiatable");
    }
}
