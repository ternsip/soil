package com.ternsip.soil.universe.audio;

import com.ternsip.soil.Soil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;

/**
 * This sound can be unregistered automatically or manually if such situation is needed
 */
@RequiredArgsConstructor
@Getter
public class Sound {

    private final File file;
    private final Vector3fc position;
    private final float magnitude;
    private final float pitch;
    private final int playTimes;
    private final boolean local;

    public Sound(File file) {
        this.file = file;
        this.position = new Vector3f(0);
        this.magnitude = 1;
        this.pitch = 1;
        this.playTimes = 1;
        this.local = true;
    }

    public Vector3fc getPosition() {
        return isLocal() ? Soil.THREADS.getUniverseClient().getSoundRepository().getListenerPosition() : position;
    }

    public void register() {
        Soil.THREADS.getUniverseClient().getSoundRepository().register(this);
    }

    public void unregister() {
        Soil.THREADS.getUniverseClient().getSoundRepository().unregister(this);
    }

}