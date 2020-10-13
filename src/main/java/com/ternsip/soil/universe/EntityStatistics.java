package com.ternsip.soil.universe;

import com.ternsip.soil.common.logic.Updatable;
import com.ternsip.soil.graph.display.FpsCounter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityStatistics extends Entity implements Updatable {

    private final FpsCounter fpsCounter;
    private final EntityText entityText = new EntityText("FPS:XXX", 10, -1, 1, 0.1f , 0.1f, false, true);

    @Override
    public void update() {
        entityText.setText("FPS:" + fpsCounter.fps);
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
