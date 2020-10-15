package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.CharEvent;
import com.ternsip.soil.common.events.base.ErrorEvent;
import com.ternsip.soil.common.events.base.Event;
import com.ternsip.soil.common.events.display.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.Callback;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.*;

@Getter
@Setter
@Slf4j
public class WindowData {

    public static final Vector4fc BACKGROUND_COLOR = new Vector4f(0f, 0f, 0f, 1f);

    private final ArrayList<Callback> callbacks = new ArrayList<>();
    private final long window;
    private Vector2i windowSize;
    private boolean cursorEnabled;
    private long gSync;

    public WindowData() {

        Soil.THREADS.client.eventIOReceiver.registerCallback(ErrorEvent.class, this::handleError);

        registerErrorEvent();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        Vector2i mainDisplaySize = getMainDisplaySize();
        this.windowSize = new Vector2i((int) (mainDisplaySize.x() * 0.8), (int) (mainDisplaySize.y() * 0.8));
        this.window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Glade", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        registerScrollEvent();
        registerCursorPosEvent();
        registerKeyEvent();
        registerFrameBufferSizeEvent();
        registerMouseButtonEvent();
        registerCharEvent();

        glfwSetWindowPos(window, (int) (mainDisplaySize.x() * 0.1), (int) (mainDisplaySize.y() * 0.1));

        // Create OpenGL context
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Disable vertical synchronization
        glfwSwapInterval(0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_DEBUG_OUTPUT);
        registerDebugEvent();
        //glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        OpenGlSettings.antialias(true);
        OpenGlSettings.enableDepthTesting(false);
        OpenGlSettings.goWireframe(false);

        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), BACKGROUND_COLOR.w());

        enableCursor();

        registerEvent(ResizeEvent.class, new ResizeEvent(getWidth(), getHeight()));

        Soil.THREADS.client.eventIOReceiver.registerCallback(ResizeEvent.class, this::handleResize);
    }

    public int getWidth() {
        return getWindowSize().x();
    }

    public int getHeight() {
        return getWindowSize().y();
    }

    public float getRatio() {
        return getWidth() / (float) getHeight();
    }

    public boolean isActive() {
        return !glfwWindowShouldClose(getWindow());
    }

    public void close() {
        glfwSetWindowShouldClose(getWindow(), true);
    }

    public void finish() {
        glfwDestroyWindow(getWindow());
        for (Callback callback : getCallbacks()) {
            callback.free();
        }
        glfwTerminate();
    }

    public void swapBuffers() {
        //glFinish();
        //glDrawBuffer(GL_FRONT);
        //https://stackoverflow.com/questions/41233696/opengl-prevent-double-buffers
        glfwSwapBuffers(getWindow());
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

    public void enableCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        setCursorEnabled(true);
        registerEvent(CursorVisibilityEvent.class, new CursorVisibilityEvent(true));
    }

    public void disableCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        setCursorEnabled(false);
        registerEvent(CursorVisibilityEvent.class, new CursorVisibilityEvent(false));
    }

    private Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    private void registerCharEvent() {
        GLFWCharCallback charCallback = GLFWCharCallback.create(
                (window, unicodePoint) -> registerEvent(CharEvent.class, new CharEvent(unicodePoint))
        );
        getCallbacks().add(charCallback);
        glfwSetCharCallback(getWindow(), charCallback);
    }

    private void registerErrorEvent() {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(
                (error, description) -> registerEvent(ErrorEvent.class, new ErrorEvent(error, description))
        );
        getCallbacks().add(errorCallback);
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
        getCallbacks().add(debugMessageCallback);
        glDebugMessageCallback(debugMessageCallback, NULL);
    }

    private void registerScrollEvent() {
        GLFWScrollCallback scrollCallback = GLFWScrollCallback.create(
                (window, xOffset, yOffset) -> registerEvent(ScrollEvent.class, new ScrollEvent(xOffset, yOffset))
        );
        getCallbacks().add(scrollCallback);
        glfwSetScrollCallback(getWindow(), scrollCallback);
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
                registerEvent(CursorPosEvent.class, new CursorPosEvent(xPos, yPos, dx, dy, normalX, normalY));
            }
        }));
        getCallbacks().add(posCallback);
        glfwSetCursorPosCallback(getWindow(), posCallback);
    }

    private void registerKeyEvent() {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(
                (window, key, scanCode, action, mods) -> registerEvent(KeyEvent.class, new KeyEvent(key, scanCode, action, mods))
        );
        getCallbacks().add(keyCallback);
        glfwSetKeyCallback(getWindow(), keyCallback);
    }

    private void registerFrameBufferSizeEvent() {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(
                (window, width, height) -> registerEvent(ResizeEvent.class, new ResizeEvent(width, height))
        );
        getCallbacks().add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(getWindow(), framebufferSizeCallback);
    }

    private void registerMouseButtonEvent() {
        GLFWMouseButtonCallback mouseButtonCallback = GLFWMouseButtonCallback.create(
                (window, button, action, mods) -> registerEvent(MouseButtonEvent.class, new MouseButtonEvent(button, action, mods))
        );
        getCallbacks().add(mouseButtonCallback);
        glfwSetMouseButtonCallback(getWindow(), mouseButtonCallback);
    }

    private <T extends Event> void registerEvent(Class<T> clazz, T event) {
        Soil.THREADS.client.eventIOReceiver.registerEvent(clazz, event);
    }

    private void handleError(ErrorEvent errorEvent) {
        GLFWErrorCallback.createPrint(System.err).invoke(errorEvent.getError(), errorEvent.getDescription());
    }

    private void handleResize(ResizeEvent resizeEvent) {
        setWindowSize(new Vector2i(resizeEvent.getWidth(), resizeEvent.getHeight()));
        glViewport(0, 0, getWidth(), getHeight());
    }

}
