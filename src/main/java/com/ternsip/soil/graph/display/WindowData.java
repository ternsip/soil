package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.*;

@Slf4j
public class WindowData {

    public static final Vector4fc BACKGROUND_COLOR = new Vector4f(0f, 0f, 0f, 0f);

    private final ArrayList<Callback> callbacks = new ArrayList<>();
    private final long window;
    public final Cursor cursor;
    private Vector2i windowSize;
    private long gSync;

    public WindowData() {
        registerErrorEvent();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        Vector2i mainDisplaySize = getMainDisplaySize();
        this.windowSize = new Vector2i((int) (mainDisplaySize.x() * 0.8), (int) (mainDisplaySize.y() * 0.8));
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_FALSE); // output alpha in fragment shader affects this
        glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE);
        glfwWindowHint(GLFW_SAMPLES, 1); // TODO consider using another value
        glfwWindowHint(GLFW_ALPHA_BITS, 8);
        this.window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Soil", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        setWindowIcon(new File("soil/interface/lawn.png"));
        cursor = new Cursor();
        registerScrollEvent();
        registerCursorPosEvent();
        registerKeyEvent();
        registerFrameBufferSizeEvent();
        registerMouseButtonEvent();
        registerCharEvent();
        registerWindowPosEvent();
        registerWindowSizeEvent();
        registerWindowRefreshEvent();
        registerWindowFocusEvent();
        registerWindowIconifyEvent();
        registerWindowMaximizeEvent();
        registerWindowContentScaleEvent();
        registerWindowCloseEvent();
        glfwSetWindowPos(window, (int) (mainDisplaySize.x() * 0.1), (int) (mainDisplaySize.y() * 0.1));
        glfwMakeContextCurrent(window);
        createCapabilities();
        log.info("Running on version: " + glGetString(GL_VERSION));
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_DEBUG_OUTPUT);
        //glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        registerDebugEvent();
        glfwSwapInterval(0); // Disable vertical synchronization
        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), BACKGROUND_COLOR.w());
        registerEvent(new ResizeEvent(getWidth(), getHeight()));
    }

    public int getWidth() {
        return windowSize.x();
    }

    public int getHeight() {
        return windowSize.y();
    }

    public float getRatio() {
        return getWidth() / (float) getHeight();
    }

    public boolean isActive() {
        return !glfwWindowShouldClose(window);
    }

    public void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public void update() {
        cursor.update();
    }

    public void finish() {
        glfwDestroyWindow(window);
        for (Callback callback : callbacks) {
            callback.free();
        }
        glfwTerminate();
    }

    public void swapBuffers() {
        //glFinish();
        //glDrawBuffer(GL_FRONT);
        //https://stackoverflow.com/questions/41233696/opengl-prevent-double-buffers
        glfwSwapBuffers(window);
    }


    public void lockBuffer() {
        if (gSync > 0) {
            glDeleteSync(gSync);
        }
        gSync = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    public void waitBuffer() {
        if (gSync > 0) {
            while (true) {
                int waitReturn = glClientWaitSync(gSync, GL_SYNC_FLUSH_COMMANDS_BIT, 1);
                if (waitReturn == GL_ALREADY_SIGNALED || waitReturn == GL_CONDITION_SATISFIED)
                    return;
            }
        }
    }


    public void pollEvents() {
        glfwPollEvents();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void requestAttention() {
        glfwRequestWindowAttention(window);
    }

    public void focusWindow() {
        glfwFocusWindow(window);
    }

    public void hideWindow() {
        glfwHideWindow(window);
    }

    public void showWindow() {
        glfwShowWindow(window);
    }

    public void maximizeWindow() {
        glfwMaximizeWindow(window);
    }

    public void restoreWindow() {
        glfwRestoreWindow(window);
    }

    public void iconifyWindow() {
        glfwIconifyWindow(window);
    }

    public void setWindowOpacity(float opacity) {
        glfwSetWindowOpacity(window, opacity);
    }

    public void setWindowSize(int width, int height) {
        glfwSetWindowSize(window, width, height);
    }

    public void setWindowPos(int x, int y) {
        glfwSetWindowPos(window, x, y);
    }

    private void setWindowIcon(File file) {
        TextureRepository.Image imageT = new TextureRepository.Image(file);
        try (GLFWImage.Buffer images = GLFWImage.malloc(1); GLFWImage image = GLFWImage.malloc()) {
            image.set(imageT.width, imageT.height, Utils.arrayToBuffer(imageT.frameData[0]));
            images.put(0, image);
            glfwSetWindowIcon(window, images);
        }
    }

    private Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    private void registerCharEvent() {
        GLFWCharCallback charCallback = GLFWCharCallback.create(
                (window, unicodePoint) -> registerEvent(new CharEvent(unicodePoint))
        );
        callbacks.add(charCallback);
        glfwSetCharCallback(window, charCallback);
    }

    private void registerErrorEvent() {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(
                (error, description) -> GLFWErrorCallback.createPrint(System.err).invoke(error, description)
        );
        callbacks.add(errorCallback);
        glfwSetErrorCallback(errorCallback);
    }

    private void registerDebugEvent() {
        GLDebugMessageCallback debugMessageCallback = GLDebugMessageCallback.create(
                (source, type, id, severity, length, message, userParam) -> {
                    String messageString = memUTF8(memByteBuffer(message, length));
                    String stackTrace = Arrays.stream(Soil.THREADS.getMainThread().getStackTrace())
                            .map(StackTraceElement::toString)
                            .reduce((s1, s2) -> s1 + System.lineSeparator() + s2)
                            .orElse("No stack trace");
                    if (severity == GL_DEBUG_SEVERITY_HIGH) {
                        log.error("Message: {}\n{}", messageString, stackTrace);
                    } else {
                        log.debug("Message: {}\n{}", messageString, stackTrace);
                    }
                }
        );
        callbacks.add(debugMessageCallback);
        glDebugMessageCallback(debugMessageCallback, NULL);
    }

    private void registerScrollEvent() {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(
                (window, xOffset, yOffset) -> registerEvent(new ScrollEvent(xOffset, yOffset))
        );
        callbacks.add(scrollCallback);
        glfwSetScrollCallback(window, scrollCallback);
    }

    private void registerCursorPosEvent() {
        GLFWCursorPosCallback posCallback = GLFWCursorPosCallback.create((new GLFWCursorPosCallbackI() {
            private double prevX;
            private double prevY;

            @Override
            public void invoke(long window, double xPos, double yPos) {
                double dx = xPos - prevX;
                double dy = yPos - prevY;
                prevX = xPos;
                prevY = yPos;
                double normalX = 2f * (xPos / getWidth() - 0.5f);
                double normalY = -2f * (yPos / getHeight() - 0.5f);
                registerEvent(new CursorPosEvent(xPos, yPos, dx, dy, normalX, normalY));
            }
        }));
        callbacks.add(posCallback);
        glfwSetCursorPosCallback(window, posCallback);
    }

    private void registerKeyEvent() {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(
                (window, key, scanCode, action, mods) -> registerEvent(new KeyEvent(key, scanCode, action, mods))
        );
        callbacks.add(keyCallback);
        glfwSetKeyCallback(window, keyCallback);
    }

    private void registerFrameBufferSizeEvent() {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(
                (window, width, height) -> registerEvent(new ResizeEvent(width, height))
        );
        callbacks.add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);
    }

    private void registerMouseButtonEvent() {
        GLFWMouseButtonCallback mouseButtonCallback = GLFWMouseButtonCallback.create(
                (window, button, action, mods) -> registerEvent(new MouseButtonEvent(button, action, mods))
        );
        callbacks.add(mouseButtonCallback);
        glfwSetMouseButtonCallback(window, mouseButtonCallback);
    }

    private void registerWindowPosEvent() {
        GLFWWindowPosCallback windowPosCallback = GLFWWindowPosCallback.create(
                (window, x, y) -> registerEvent(new WindowPosEvent(x, y))
        );
        callbacks.add(windowPosCallback);
        glfwSetWindowPosCallback(window, windowPosCallback);
    }

    private void registerWindowSizeEvent() {
        GLFWWindowSizeCallback windowSizeCallback = GLFWWindowSizeCallback.create(
                (window, width, height) -> registerEvent(new WindowSizeEvent(width, height))
        );
        callbacks.add(windowSizeCallback);
        glfwSetWindowSizeCallback(window, windowSizeCallback);
    }

    private void registerWindowRefreshEvent() {
        GLFWWindowRefreshCallback windowRefreshCallback = GLFWWindowRefreshCallback.create(
                (window) -> registerEvent(new WindowRefreshEvent())
        );
        callbacks.add(windowRefreshCallback);
        glfwSetWindowRefreshCallback(window, windowRefreshCallback);
    }

    private void registerWindowFocusEvent() {
        GLFWWindowFocusCallback windowFocusCallback = GLFWWindowFocusCallback.create(
                (window, focused) -> registerEvent(new WindowFocusEvent(focused))
        );
        callbacks.add(windowFocusCallback);
        glfwSetWindowFocusCallback(window, windowFocusCallback);
    }

    private void registerWindowIconifyEvent() {
        GLFWWindowIconifyCallback windowIconifyCallback = GLFWWindowIconifyCallback.create(
                (window, iconified) -> registerEvent(new WindowIconifyEvent(iconified))
        );
        callbacks.add(windowIconifyCallback);
        glfwSetWindowIconifyCallback(window, windowIconifyCallback);
    }

    private void registerWindowMaximizeEvent() {
        GLFWWindowMaximizeCallback windowMaximizeCallback = GLFWWindowMaximizeCallback.create(
                (window, maximized) -> registerEvent(new WindowMaximizeEvent(maximized))
        );
        callbacks.add(windowMaximizeCallback);
        glfwSetWindowMaximizeCallback(window, windowMaximizeCallback);
    }

    private void registerWindowContentScaleEvent() {
        GLFWWindowContentScaleCallback windowContentScaleCallback = GLFWWindowContentScaleCallback.create(
                (window, xScale, yScale) -> registerEvent(new WindowContentScaleEvent(xScale, yScale))
        );
        callbacks.add(windowContentScaleCallback);
        glfwSetWindowContentScaleCallback(window, windowContentScaleCallback);
    }

    private void registerWindowCloseEvent() {
        GLFWWindowCloseCallback windowCloseCallback = GLFWWindowCloseCallback.create(
                (window) -> registerEvent(new WindowCloseEvent())
        );
        callbacks.add(windowCloseCallback);
        glfwSetWindowCloseCallback(window, windowCloseCallback);
    }

    private <T extends Event> void registerEvent(T event) {
        Soil.THREADS.client.eventIOReceiver.registerEvent(event);
    }

    @EventHook
    private void handleResize(ResizeEvent resizeEvent) {
        windowSize = new Vector2i(resizeEvent.getWidth(), resizeEvent.getHeight());
        glViewport(0, 0, getWidth(), getHeight());
    }

    @RequiredArgsConstructor
    public enum CursorType {

        SIMPLE(new File("soil/interface/cursor_simple.png")),
        SELECT(new File("soil/interface/grass.gif")),
        LOADING(new File("soil/interface/loading.gif"));

        public final File file;

    }

    public class Cursor {

        public long[][] cursorPointers;
        public CursorType currentCursorType;
        public long[] animationDelays;
        private boolean visible;

        Cursor() {
            cursorPointers = new long[CursorType.values().length][];
            animationDelays = new long[CursorType.values().length];
            int cursorNumber = 0;
            for (CursorType cursorType : CursorType.values()) {
                TextureRepository.Image imageT = new TextureRepository.Image(cursorType.file);
                cursorPointers[cursorNumber] = new long[imageT.frameData.length];
                animationDelays[cursorNumber] = imageT.frameDelay[0];
                for (int frame = 0; frame < imageT.frameData.length; ++frame) {
                    try (GLFWImage image = GLFWImage.malloc()) {
                        image.set(imageT.width, imageT.height, Utils.arrayToBuffer(imageT.frameData[frame]));
                        long cursorIndex = glfwCreateCursor(image, 0, 0);
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
            registerEvent(new CursorVisibilityEvent(true));
        }

        public void hide() {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            visible = false;
            registerEvent(new CursorVisibilityEvent(false));
        }

        public void move(int x, int y) {

        }

    }


}
