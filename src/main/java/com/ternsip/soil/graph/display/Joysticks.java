package com.ternsip.soil.graph.display;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

@Slf4j
public class Joysticks {

    public static final int MAX_JOYSTICKS = 16;

    public final Joystick[] joysticks = new Joystick[MAX_JOYSTICKS];
    public final Set<Joystick> activeJoysticks = new HashSet<>();

    public Joysticks() {
        discover();
    }

    public void discover() {
        activeJoysticks.clear();
        for (int jid = 0; jid < MAX_JOYSTICKS; ++jid) {
            joysticks[jid] = new Joystick(jid);
            if (joysticks[jid].isPresent()) {
                activeJoysticks.add(joysticks[jid]);
            }
        }
    }

    /**
     * This is inefficient solution, however it is only one way to do this
     * The glfw's issue:
     * https://github.com/glfw/glfw/issues/601
     */
    public void update() {
        activeJoysticks.forEach(e -> {
            e.updateAxis();
            e.updateButtons();
            e.updateJoystickHats();
        });
    }

    @RequiredArgsConstructor
    public static class Joystick {

        public final int jid;
        public float[] axis = new float[16];
        public int[] buttons = new int[16];
        public int[] hats = new int[16];

        public boolean isPresent() {
            return glfwJoystickPresent(jid);
        }

        public String getName() {
            return glfwGetJoystickName(jid);
        }

        public String getGamepadName() {
            return glfwGetGamepadName(jid);
        }

        public String getGUID() {
            return glfwGetJoystickGUID(jid);
        }

        public boolean isGamepad() {
            return glfwJoystickIsGamepad(jid);
        }

        public void updateAxis() {
            FloatBuffer fb = glfwGetJoystickAxes(jid);
            if (fb == null) return; // TODO silent error?
            int maxAxis = Math.min(16, fb.limit());
            for (int index = 0; index < maxAxis; ++index) {
                axis[index] = fb.get(index);
            }
        }

        public void updateButtons() {
            ByteBuffer bb = glfwGetJoystickButtons(jid);
            if (bb == null) return; // TODO silent error?
            int maxButtons = Math.min(16, bb.limit());
            for (int index = 0; index < maxButtons; ++index) {
                buttons[index] = bb.get(index);
            }
        }

        public void updateJoystickHats() {
            ByteBuffer bb = glfwGetJoystickHats(jid);
            if (bb == null) return; // TODO silent error?
            int maxHats = Math.min(16, bb.limit());
            for (int index = 0; index < maxHats; ++index) {
                hats[index] = bb.get(index);
            }
        }

    }

}
