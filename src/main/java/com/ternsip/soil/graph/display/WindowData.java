package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.events.*;
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

@Slf4j
public class WindowData {

    public static final Vector2i MINIMUM_WINDOW = new Vector2i(32, 32);
    public static final Vector4fc BACKGROUND_COLOR = new Vector4f(0f, 0f, 0f, 0f);
    public final Cursor cursor;
    private final ArrayList<Callback> callbacks = new ArrayList<>();
    private final long window;
    public int width;
    public int height;
    public long gSync;
    public boolean fullscreen = false;
    public boolean vsync = false;

    public WindowData() {
        registerErrorEvent();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long monitor = getPrimaryMonitor();
        GLFWVidMode glfwVidMode = getVideoMode(monitor);
        Vector2i monitorPhysicalSize = getMonitorPhysicalSize(monitor);
        this.width = (int) (glfwVidMode.width() * 0.8);
        this.height = (int) (glfwVidMode.height() * 0.8);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_TRUE);
        glfwWindowHint(GLFW_FLOATING, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        glfwWindowHint(GLFW_CENTER_CURSOR, GLFW_FALSE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_FALSE);
        glfwWindowHint(GLFW_RED_BITS, 8);
        glfwWindowHint(GLFW_GREEN_BITS, 8);
        glfwWindowHint(GLFW_BLUE_BITS, 8);
        glfwWindowHint(GLFW_ALPHA_BITS, 8);
        glfwWindowHint(GLFW_DEPTH_BITS, 0);
        glfwWindowHint(GLFW_STENCIL_BITS, 0);
        glfwWindowHint(GLFW_ACCUM_RED_BITS, 0);
        glfwWindowHint(GLFW_ACCUM_GREEN_BITS, 0);
        glfwWindowHint(GLFW_ACCUM_BLUE_BITS, 0);
        glfwWindowHint(GLFW_ACCUM_ALPHA_BITS, 0);
        glfwWindowHint(GLFW_AUX_BUFFERS, 0);
        glfwWindowHint(GLFW_SAMPLES, GLFW_DONT_CARE);
        glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE);
        glfwWindowHint(GLFW_STEREO, GLFW_FALSE);
        glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_CONTEXT_ROBUSTNESS, GLFW_NO_ROBUSTNESS);
        glfwWindowHint(GLFW_CONTEXT_RELEASE_BEHAVIOR, GLFW_ANY_RELEASE_BEHAVIOR);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        this.window = glfwCreateWindow(width, height, "Soil", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        setWindowIcon(new File("soil/interface/lawn.png"));
        glfwSetWindowSizeLimits(window, MINIMUM_WINDOW.x, MINIMUM_WINDOW.y, GLFW_DONT_CARE, GLFW_DONT_CARE);
        glfwSetWindowAspectRatio(window, GLFW_DONT_CARE, GLFW_DONT_CARE);
        this.cursor = new Cursor(window);
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
        registerCursorEnterLeaveEvent();
        registerCharModsEvent();
        registerMonitorEvent();
        registerWindowDropEvent();
        registerJoystickEvent();
        glfwSetWindowPos(window, (int) (glfwVidMode.width() * 0.1), (int) (glfwVidMode.height() * 0.1));
        glfwMakeContextCurrent(window);
        createCapabilities();
        log.info("Running on version: " + glGetString(GL_VERSION));
        log.info("Monitor: " + getMonitorName(monitor) + " " + monitorPhysicalSize.x() + "x" + monitorPhysicalSize.y());
        log.info("Video mode: " + glfwVidMode.width() + "x" + glfwVidMode.height() + " - " + glfwVidMode.refreshRate() + " Hz");
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DITHER);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_SCISSOR_TEST);
        glDisable(GL_STENCIL_TEST);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDisable(GL_POLYGON_OFFSET_FILL);
        glDisable(GL_PRIMITIVE_RESTART_FIXED_INDEX);
        glDisable(GL_RASTERIZER_DISCARD);
        glDisable(GL_SAMPLE_ALPHA_TO_COVERAGE);
        glDisable(GL_SAMPLE_MASK);
        registerDebugEvent();
        disableVsync();
        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), BACKGROUND_COLOR.w());
        registerEvent(new ResizeEvent(width, height));
    }

    public float getRatio() {
        return width / (float) height;
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

    public String getClipboard() {
        return glfwGetClipboardString(window);
    }

    public void setClipboard(String value) {
        glfwSetClipboardString(window, value);
    }

    public boolean isJoystickPresent(int joy) {
        return glfwJoystickPresent(joy);
    }

    public int getKeyState(int key) {
        return glfwGetKey(window, key);
    }

    public double getWindowTime() {
        return glfwGetTime();
    }

    public void setWindowTime(double time) {
        glfwSetTime(time);
    }

    public long getWindowTimer() {
        return glfwGetTimerValue();
    }

    public long getWindowTimerFrequency() {
        return glfwGetTimerFrequency();
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    public int getWindowAttribute(int attribute) {
        return glfwGetWindowAttrib(window, attribute);
    }

    public void setWindowAttribute(int attribute, int value) {
        glfwSetWindowAttrib(window, attribute, value);
    }

    public void enterFullscreen() {
        if (fullscreen) {
            return;
        }
        long monitor = getPrimaryMonitor();
        GLFWVidMode glfwVidMode = getVideoMode(monitor);
        glfwSetWindowMonitor(window, monitor, 0, 0, glfwVidMode.width(), glfwVidMode.height(), glfwVidMode.refreshRate());
        fullscreen = true;
    }

    public void enterWindowed() {
        if (!fullscreen) {
            return;
        }
        long monitor = getPrimaryMonitor();
        GLFWVidMode glfwVidMode = getVideoMode(monitor);
        int w = glfwVidMode.width();
        int h = glfwVidMode.height();
        glfwSetWindowMonitor(window, MemoryUtil.NULL, (int) (w * 0.1), (int) (h * 0.1), (int) (w * 0.8), (int) (h * 0.8), glfwVidMode.refreshRate());
        fullscreen = false;
    }

    public void enableVsync() {
        glfwSwapInterval(1);
        vsync = true;
    }

    public void disableVsync() {
        glfwSwapInterval(0);
        vsync = false;
    }

    public String getMonitorName(long monitor) {
        return glfwGetMonitorName(monitor);
    }

    public Vector2i getMonitorPhysicalSize(long monitor) {
        int[] x = {0}, y = {0};
        glfwGetMonitorPhysicalSize(monitor, x, y);
        return new Vector2i(x[0], y[0]);
    }

    public GLFWVidMode getVideoMode(long monitor) {
        return glfwGetVideoMode(monitor);
    }

    public long getPrimaryMonitor() {
        return glfwGetPrimaryMonitor();
    }

    private void setWindowIcon(File file) {
        TextureRepository.Image imageT = new TextureRepository.Image(file);
        try (GLFWImage.Buffer images = GLFWImage.malloc(1); GLFWImage image = GLFWImage.malloc()) {
            image.set(imageT.width, imageT.height, Utils.arrayToBuffer(imageT.frameData[0]));
            images.put(0, image);
            glfwSetWindowIcon(window, images);
        }
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
                    String messageString = MemoryUtil.memUTF8(MemoryUtil.memByteBuffer(message, length));
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
        glDebugMessageCallback(debugMessageCallback, MemoryUtil.NULL);
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
                double normalX = 2f * (xPos / width - 0.5f);
                double normalY = -2f * (yPos / height - 0.5f);
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

    private void registerCursorEnterLeaveEvent() {
        GLFWCursorEnterCallback cursorEnterCallback = GLFWCursorEnterCallback.create(
                (window, entered) -> registerEvent(new CursorEnterLeaveEvent(entered))
        );
        callbacks.add(cursorEnterCallback);
        glfwSetCursorEnterCallback(window, cursorEnterCallback);
    }

    private void registerCharModsEvent() {
        GLFWCharModsCallback charModsCallback = GLFWCharModsCallback.create(
                (window, codepoint, mods) -> registerEvent(new CharModsEvent(codepoint, mods))
        );
        callbacks.add(charModsCallback);
        glfwSetCharModsCallback(window, charModsCallback);
    }

    private void registerMonitorEvent() {
        GLFWMonitorCallback monitorCallback = GLFWMonitorCallback.create(
                (monitor, event) -> registerEvent(new MonitorEvent(monitor, event))
        );
        callbacks.add(monitorCallback);
        glfwSetMonitorCallback(monitorCallback);
    }

    private void registerWindowDropEvent() {
        GLFWDropCallback dropCallback = GLFWDropCallback.create(
                (window, count, names) -> registerEvent(new WindowDropEvent(count, names))
        );
        callbacks.add(dropCallback);
        glfwSetDropCallback(window, dropCallback);
    }

    private void registerJoystickEvent() {
        GLFWJoystickCallback joystickCallback = GLFWJoystickCallback.create(
                (jid, event) -> registerEvent(new JoystickEvent(jid, event))
        );
        callbacks.add(joystickCallback);
        glfwSetJoystickCallback(joystickCallback);
    }

    private <T extends Event> void registerEvent(T event) {
        Soil.THREADS.client.eventReceiver.registerEvent(event);
    }

    @EventHook
    private void handleResize(ResizeEvent resizeEvent) {
        width = resizeEvent.getWidth();
        height = resizeEvent.getHeight();
        glViewport(0, 0, width, height);
    }

    @EventHook
    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_F11 && event.getAction() == GLFW_PRESS) {
            if (fullscreen) {
                enterWindowed();
            } else {
                enterFullscreen();
            }
        }
        if (event.getKey() == GLFW_KEY_F12 && event.getAction() == GLFW_PRESS) {
            if (vsync) {
                disableVsync();
            } else {
                enableVsync();
            }
        }
    }

}
