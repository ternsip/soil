package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

/**
 * This sound can be unregistered automatically or manually if such situation is needed
 */
@RequiredArgsConstructor
@Getter
public class Sound {

    public final File file;
    public final float magnitude;
    public final float pitch;
    public final int playTimes;
    public final boolean local;
    public float x, y;

    public Sound(File file) {
        this.file = file;
        this.x = 0;
        this.y = 0;
        this.magnitude = 1;
        this.pitch = 1;
        this.playTimes = 1;
        this.local = true;
    }

    public void register() {
        Soil.THREADS.client.soundRepository.register(this);
    }

    public void unregister() {
        Soil.THREADS.client.soundRepository.unregister(this);
    }

}
