package com.ternsip.soil.network;

@FunctionalInterface
public interface NetworkCallback<T> {

    void execute(Connection connection, T value);

}
