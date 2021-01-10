package com.ternsip.soil;

import com.google.common.collect.Sets;
import com.ternsip.soil.general.Threads;

import java.util.Set;

/**
 * The main entry point of the application
 * Initializes graphic, network and logic thread
 * Graphic thread should always be main by multi-platform purposes
 * <p>
 * In case you have GPU-dump crashes:
 * - checkout memory buffers (for instance that all of them rewind() after reading)
 * - try to avoid memory buffers if possible
 * - check memory buffers' explicit free calls
 * - check data that you send to GPU i.e. number of vertices/textures/indices/colors etc.
 * - in case you want to debug errors - use debug mode
 * - be careful with @Delegate sometimes it breaks navigation or debugger, also recursive delegate does not allowed
 *
 * @author Ternsip
 * TODO test double monitors swap (including different Hz and color pallet) and screen resize
 * TODO use nglfwGetJoystickAxes instead of glfwGetJoystickAxes to not copy buffer etc.
 */
public class Soil {

    public static final Threads THREADS = new Threads();

    public static void main(String[] args) {
        Set<String> input = Sets.newHashSet(args);
        if (input.contains("--server")) {
            THREADS.runServer();
        } else {
            THREADS.runClient();
        }
    }

}
