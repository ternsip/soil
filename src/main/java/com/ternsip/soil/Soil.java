package com.ternsip.soil;

import com.google.common.collect.Sets;
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
 */
public class Soil {

    public static void main(String[] args) {
        Set<String> input = Sets.newHashSet(args);
        //if (input.contains("--server")) {
        //    IUniverseServer.run();
        //} else {
        //    IGraphics.run();
        //}
    }

}
