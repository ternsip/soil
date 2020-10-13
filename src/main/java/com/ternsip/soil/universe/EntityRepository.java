package com.ternsip.soil.universe;

import com.ternsip.soil.common.logic.Updatable;
import com.ternsip.soil.graph.shader.base.Shader;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Setter
public class EntityRepository {

    public final HashMap<UUID, Entity> uuidToEntity = new HashMap<>();
    public final HashMap<UUID, Updatable> uuidToUpdatable = new HashMap<>();

    public void update() {
        uuidToUpdatable.values().forEach(Updatable::update);
    }

    public UUID register(Entity entity) {
        UUID uuid = UUID.randomUUID();
        entity.uuid = uuid;
        uuidToEntity.put(uuid, entity);
        if (entity instanceof Updatable) {
            uuidToUpdatable.put(uuid, (Updatable) entity);
        }
        return uuid;
    }

    public void unregister(UUID uuid) {
        uuidToEntity.remove(uuid);
        uuidToUpdatable.remove(uuid);
    }

}
