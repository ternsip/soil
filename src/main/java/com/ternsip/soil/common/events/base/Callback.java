package com.ternsip.soil.common.events.base;

@FunctionalInterface
public interface Callback<T extends Event> {

    void apply(T event);

}