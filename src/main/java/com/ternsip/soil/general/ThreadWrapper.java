package com.ternsip.soil.general;

import com.ternsip.soil.common.logic.Timer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class ThreadWrapper<T extends Threadable> {

    private final Task<T> task;
    private final Thread thread;

    public ThreadWrapper(Supplier<T> supplier, long timeout) {
        this.task = new Task<>(supplier, timeout);
        this.thread = new Thread(task);
    }

    public ThreadWrapper(Supplier<T> supplier) {
        this(supplier, 0L);
    }

    public void start() {
        this.thread.start();
    }

    public void stop() {
        getTask().deactivate();
    }

    @SneakyThrows
    public void join() {
        getThread().join();
    }

    public boolean isActive() {
        return getTask().isActive();
    }

    public T getObjective() {
        return getTask().getObjective();
    }

    public void setTimeout(long timeout) {
        getTask().getTimer().setTimeout(timeout);
    }

    @RequiredArgsConstructor
    @Getter
    private static final class Task<T extends Threadable> implements Runnable {

        private final AtomicBoolean active = new AtomicBoolean(true);
        private final Supplier<T> supplier;
        private final Timer timer;

        @Getter(lazy = true)
        private final T objective = supplier.get();

        Task(Supplier<T> supplier, long timeout) {
            this.supplier = supplier;
            this.timer = new Timer(timeout);
        }

        @Override
        public final void run() {
            getObjective().init();
            while (isActive()) {
                getObjective().update();
                getTimer().rest();
            }
            getObjective().finish();
        }

        final boolean isActive() {
            return getActive().get();
        }

        final void deactivate() {
            getActive().set(false);
            getObjective().unlock();
        }
    }

}
