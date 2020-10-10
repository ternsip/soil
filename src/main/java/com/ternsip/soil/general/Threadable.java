package com.ternsip.soil.general;

import lombok.SneakyThrows;

public interface Threadable {

    void init();

    void update();

    void finish();

    @SneakyThrows
    default void lock() {
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    default void unlock() {
        synchronized (this) {
            this.notify();
        }
    }

}
