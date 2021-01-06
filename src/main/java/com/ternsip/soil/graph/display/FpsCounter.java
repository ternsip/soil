package com.ternsip.soil.graph.display;

import com.ternsip.soil.common.Timer;

public class FpsCounter {

    public volatile int fps = 0;
    private Timer timer = new Timer(1000);
    private int frameCount = 0;

    public void updateFps() {
        frameCount++;
        if (timer.isOver()) {
            timer.drop();
            fps = frameCount;
            frameCount = 0;
        }
    }

}
