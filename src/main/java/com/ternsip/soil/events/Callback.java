package com.ternsip.soil.events;

@FunctionalInterface
public interface Callback<T extends Event> {

    void apply(T event);

}