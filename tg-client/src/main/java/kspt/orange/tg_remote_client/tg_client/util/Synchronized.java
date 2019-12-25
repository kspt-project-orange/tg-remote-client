package kspt.orange.tg_remote_client.tg_client.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

public final class Synchronized<T> {
    @NotNull
    private CountDownLatch latch;
    @NotNull
    private final StampedLock lock = new StampedLock();
    @Nullable
    private T o;

    private Synchronized() {
        latch = new CountDownLatch(1);
    }

    public static <T> Synchronized<T> acquired() {
        return new Synchronized<>();
    }

    public void acquire() {
        update(null, (self) -> self.latch = new CountDownLatch(1));
    }

    public void leave(@NotNull final T o) {
        update(o, null);
        latch.countDown();
    }

    @Nullable
    public T await() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return get();
    }

    public void update(@Nullable final T o, @Nullable final Consumer<Synchronized<T>> updater) {
        final var stamp = lock.writeLock();
        this.o = o;
        if (updater != null) {
            updater.accept(this);
        }
        lock.unlockWrite(stamp);
    }

    @Nullable
    private T get() {
        var stamp = lock.tryOptimisticRead();
        T val = o;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            val = o;
            lock.unlockRead(stamp);
        }
        return val;
    }
}
