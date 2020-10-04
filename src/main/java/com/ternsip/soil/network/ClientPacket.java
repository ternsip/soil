package com.ternsip.soil.network;

import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class ClientPacket implements Serializable {

    public abstract void apply(Connection connection);

}
