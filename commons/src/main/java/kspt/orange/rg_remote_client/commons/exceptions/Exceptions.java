package kspt.orange.rg_remote_client.commons.exceptions;

import org.jetbrains.annotations.NotNull;

public final class Exceptions {
    private Exceptions() {
        throw new RuntimeException();
    }

    public static RuntimeException uncheckedFromChecked(@NotNull final Throwable throwable) {
        return new RuntimeException(throwable);
    }
}
