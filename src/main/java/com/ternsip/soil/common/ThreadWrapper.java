package com.ternsip.soil.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
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
        task.deactivate();
    }

    @SneakyThrows
    public void join() {
        thread.join();
    }

    public boolean isActive() {
        return task.active.get();
    }

    public T getObjective() {
        return task.objective;
    }

    public void waitInitialization() {
        task.lock();
    }

    public void setTimeout(long timeout) {
        task.timer.setTimeout(timeout);
    }

    private static final class Task<T extends Threadable> implements Runnable {

        private final AtomicBoolean active = new AtomicBoolean(true);
        private final Supplier<T> supplier;
        private final Timer timer;
        private T objective = null;

        Task(Supplier<T> supplier, long timeout) {
            this.supplier = supplier;
            this.timer = new Timer(timeout);
        }

        @Override
        public final void run() {
            objective = supplier.get();
            objective.init();
            unlock();
            while (active.get()) {
                objective.update();
                timer.rest();
            }
            objective.finish();
        }

        final void deactivate() {
            active.set(false);
            if (objective != null) {
                objective.unlock();
            }
        }

        final synchronized void lock() {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted", e);
            }
        }

        final synchronized void unlock() {
            notify();
        }

    }

}
