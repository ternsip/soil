package com.ternsip.soil.game.entities;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Updatable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityStatistics extends Entity implements Updatable {

    private final EntityText entityText = new EntityText("FPS:XXX", 10, -1, 1, 0.1f , 0.1f, false, true);

    @Override
    public void update() {
        entityText.setText("FPS:" + Soil.THREADS.client.fpsCounter.fps);
    }

    @Override
    public void register() {
        super.register();
        entityText.register();
    }

    @Override
    public void unregister() {
        super.unregister();
        entityText.unregister();
    }


}
