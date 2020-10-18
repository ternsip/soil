package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

@Getter
public class AudioRepository {

    private static FloatBuffer ORIENTATION_BUFFER = BufferUtils.createFloatBuffer(6);

    private final Map<File, Integer> fileToBufferPointer = new HashMap<>();
    private final Map<Sound, SoundPlayer> soundToMetaInformation = new HashMap<>();
    private long device;
    private long context;

    public AudioRepository() {

        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        this.device = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        this.context = alcCreateContext(this.device, attributes);
        alcMakeContextCurrent(this.context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(this.device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

    }

    public void update() {

        SoundRepository soundRepository = Soil.THREADS.client.soundRepository;
        Set<Sound> soundsSet = soundRepository.getSounds();

        soundsSet.removeIf(sound -> {

            SoundPlayer soundPlayer = getSoundToMetaInformation().computeIfAbsent(sound, s -> {
                Integer bufferPointer = getFileToBufferPointer().computeIfAbsent(s.getFile(), this::loadSound);
                return new SoundPlayer(s, bufferPointer);
            });

            if (!soundPlayer.isPlaying()) {
                if (soundPlayer.getPlayedTimes() >= sound.getPlayTimes()) {
                    soundPlayer.finish();
                    getSoundToMetaInformation().remove(sound);
                    return true;
                }
                soundPlayer.play();
            }

            return false;

        });

        getSoundToMetaInformation().entrySet().removeIf(entry -> {
            if (soundsSet.contains(entry.getKey())) {
                return false;
            }
            entry.getValue().finish();
            return true;
        });

        // Orient listener in 3d space
        Vector3fc orientFront = soundRepository.getOrientationFront();
        Vector3fc orientUp = soundRepository.getOrientationUp();
        ORIENTATION_BUFFER.clear();
        ORIENTATION_BUFFER.put(orientFront.x());
        ORIENTATION_BUFFER.put(orientFront.y());
        ORIENTATION_BUFFER.put(orientFront.z());
        ORIENTATION_BUFFER.put(orientUp.x());
        ORIENTATION_BUFFER.put(orientUp.y());
        ORIENTATION_BUFFER.put(orientUp.z());
        ORIENTATION_BUFFER.rewind();
        alListener3f(AL_POSITION, soundRepository.x, soundRepository.y, 0);
        alListenerfv(AL_ORIENTATION, ORIENTATION_BUFFER);

    }


    public void finish() {
        getFileToBufferPointer().values().forEach(AL10::alDeleteBuffers);
        getSoundToMetaInformation().values().forEach(SoundPlayer::finish);
        alcDestroyContext(getContext());
        alcCloseDevice(getDevice());
    }

    @SneakyThrows
    public Integer loadSound(File file) {
        ShortBuffer rawAudioBuffer;
        int channels;
        int sampleRate;

        try (MemoryStack stack = stackPush()) {
            //Allocate space to store return information from the function
            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);

            rawAudioBuffer = stb_vorbis_decode_memory(Utils.loadResourceToByteBuffer(file), channelsBuffer, sampleRateBuffer);

            //Retreive the extra information that was stored in the buffers by the function
            channels = channelsBuffer.get(0);
            sampleRate = sampleRateBuffer.get(0);
        }

        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        int bufferPointer = alGenBuffers();

        alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

        free(rawAudioBuffer);

        return bufferPointer;
    }

    @Getter
    @Setter
    public static class SoundPlayer {

        private final int sourcePointer;
        private int playedTimes = 0;

        public SoundPlayer(Sound sound, int bufferPointer) {
            sourcePointer = alGenSources();
            alSourcei(sourcePointer, AL_BUFFER, bufferPointer);
            alSourcef(sourcePointer, AL_GAIN, sound.getMagnitude());
            alSourcef(sourcePointer, AL_PITCH, sound.getPitch());
            if (sound.local) {
                alSource3f(sourcePointer, AL_POSITION, sound.x, sound.y, 0);
            } else {
                alSource3f(sourcePointer, AL_POSITION, Soil.THREADS.client.soundRepository.x, Soil.THREADS.client.soundRepository.y, 0);
            }
        }

        public void finish() {
            alDeleteSources(getSourcePointer());
        }

        public boolean isPlaying() {
            int sourceState = alGetSourcei(getSourcePointer(), AL_SOURCE_STATE);
            return sourceState == AL_PLAYING;
        }

        public void play() {
            alSourcePlay(getSourcePointer());
            setPlayedTimes(getPlayedTimes() + 1);
        }

    }

}
