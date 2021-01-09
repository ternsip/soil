package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.events.CursorVisibilityEvent;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

public class Cursor {

    private final long window;
    public long[][] cursorPointers;
    public CursorType currentCursorType;
    public long[] animationDelays;
    private boolean visible;

    Cursor(long window) {
        this.window = window;
        cursorPointers = new long[CursorType.values().length][];
        animationDelays = new long[CursorType.values().length];
        int cursorNumber = 0;
        for (CursorType cursorType : CursorType.values()) {
            Image image = new Image(cursorType.file);
            cursorPointers[cursorNumber] = new long[image.frameData.length];
            animationDelays[cursorNumber] = image.frameDelay[0];
            for (int frame = 0; frame < image.frameData.length; ++frame) {
                try (GLFWImage glfwImage = GLFWImage.malloc()) {
                    glfwImage.set(image.width, image.height, Utils.arrayToBuffer(image.frameData[frame]));
                    long cursorIndex = glfwCreateCursor(glfwImage, 0, 0);
                    if (cursorIndex == MemoryUtil.NULL) {
                        throw new RuntimeException("Error creating cursor");
                    }
                    cursorPointers[cursorNumber][frame] = cursorIndex;
                }
            }
            ++cursorNumber;
        }
        selectCursorType(CursorType.SIMPLE);
        show();
    }

    public void selectCursorType(CursorType cursorType) {
        currentCursorType = cursorType;
        update();
    }

    public void update() {
        int idx = currentCursorType.ordinal();
        int frame = (int) ((Math.abs(System.currentTimeMillis()) / Math.max(1, animationDelays[idx])) % cursorPointers[idx].length);
        glfwSetCursor(window, cursorPointers[idx][frame]);
    }

    public void show() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        visible = true;
        Soil.THREADS.client.eventReceiver.registerEvent(new CursorVisibilityEvent(true));
    }

    public void hide() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        visible = false;
        Soil.THREADS.client.eventReceiver.registerEvent(new CursorVisibilityEvent(false));
    }

    public void setPos(double x, double y) {
        glfwSetCursorPos(window, x, y);
    }

    public int getButtonState(int button) {
        return glfwGetMouseButton(window, button);
    }

}