package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.events.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

@Getter
@Setter
@Slf4j
public class WindowData {

    public static final Vector4fc BACKGROUND_COLOR = new Vector4f(0f, 0f, 0f, 0f);

    private final ArrayList<Callback> callbacks = new ArrayList<>();
    private final long window;
    private Vector2i windowSize;
    private boolean cursorEnabled;
    private long gSync;
    public Cursor cursor;

    public WindowData() {

        Soil.THREADS.client.eventIOReceiver.register(this);

        registerErrorEvent();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        Vector2i mainDisplaySize = getMainDisplaySize();
        this.windowSize = new Vector2i((int) (mainDisplaySize.x() * 0.8), (int) (mainDisplaySize.y() * 0.8));
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE); // output alpha in fragment shader affects this
        glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE);
        //glfwWindowHint(GLFW_SAMPLES , 4);
        glfwWindowHint(GLFW_ALPHA_BITS, 8);
        this.window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Soil", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        setUpIcon();
        cursor = new Cursor();

        registerScrollEvent();
        registerCursorPosEvent();
        registerKeyEvent();
        registerFrameBufferSizeEvent();
        registerMouseButtonEvent();
        registerCharEvent();

        glfwSetWindowPos(window, (int) (mainDisplaySize.x() * 0.1), (int) (mainDisplaySize.y() * 0.1));

        // Create OpenGL context
        glfwMakeContextCurrent(window);
        createCapabilities();

        // Disable vertical synchronization
        glfwSwapInterval(0);

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glBlendEquation(GL_FUNC_ADD);
        //glBlendFuncSeparate(GL_DST_ALPHA, GL_ONE, GL_ZERO, GL_ONE_MINUS_SRC_ALPHA); // https://community.khronos.org/t/front-to-back-blending/65155
        //glBlendFunc(GL_ONE_MINUS_DST_ALPHA, GL_ONE); FOR front-back rendering, dont forget to tweak initial background alpha
        //glEnable(GL_ALPHA_TEST);
        // glAlphaFunc (GL_GREATER, 0.9f);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_DEBUG_OUTPUT);
        registerDebugEvent();
        //glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);

        glClearColor(BACKGROUND_COLOR.x(), BACKGROUND_COLOR.y(), BACKGROUND_COLOR.z(), BACKGROUND_COLOR.w());

        enableCursor();

        registerEvent(new ResizeEvent(getWidth(), getHeight()));
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
        Soil.THREADS.client.eventIOReceiver.unregister(this);
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
        registerEvent(new CursorVisibilityEvent(true));
    }

    public void disableCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        setCursorEnabled(false);
        registerEvent(new CursorVisibilityEvent(false));
    }


    private void setUpIcon() {
        TextureRepository.Image imageT = new TextureRepository.Image(new File("soil/interface/lawn.png"));
        GLFWImage.Buffer images = GLFWImage.malloc(1);
        GLFWImage image = GLFWImage.malloc();
        image.set(imageT.width, imageT.height, Utils.arrayToBuffer(imageT.frameData[0]));
        images.put(0, image);
        glfwSetWindowIcon(window, images);
        image.free();
        images.free();
    }

    private Vector2i getMainDisplaySize() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    private void registerCharEvent() {
        GLFWCharCallback charCallback = GLFWCharCallback.create(
                (window, unicodePoint) -> registerEvent(new CharEvent(unicodePoint))
        );
        getCallbacks().add(charCallback);
        glfwSetCharCallback(getWindow(), charCallback);
    }

    private void registerErrorEvent() {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.create(
                (error, description) -> registerEvent(new ErrorEvent(error, description))
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
                (window, xOffset, yOffset) -> registerEvent(new ScrollEvent(xOffset, yOffset))
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
                registerEvent(new CursorPosEvent(xPos, yPos, dx, dy, normalX, normalY));
            }
        }));
        getCallbacks().add(posCallback);
        glfwSetCursorPosCallback(getWindow(), posCallback);
    }

    private void registerKeyEvent() {
        GLFWKeyCallback keyCallback = GLFWKeyCallback.create(
                (window, key, scanCode, action, mods) -> registerEvent(new KeyEvent(key, scanCode, action, mods))
        );
        getCallbacks().add(keyCallback);
        glfwSetKeyCallback(getWindow(), keyCallback);
    }

    private void registerFrameBufferSizeEvent() {
        GLFWFramebufferSizeCallback framebufferSizeCallback = GLFWFramebufferSizeCallback.create(
                (window, width, height) -> registerEvent(new ResizeEvent(width, height))
        );
        getCallbacks().add(framebufferSizeCallback);
        glfwSetFramebufferSizeCallback(getWindow(), framebufferSizeCallback);
    }

    private void registerMouseButtonEvent() {
        GLFWMouseButtonCallback mouseButtonCallback = GLFWMouseButtonCallback.create(
                (window, button, action, mods) -> registerEvent(new MouseButtonEvent(button, action, mods))
        );
        getCallbacks().add(mouseButtonCallback);
        glfwSetMouseButtonCallback(getWindow(), mouseButtonCallback);
    }

    private <T extends Event> void registerEvent(T event) {
        Soil.THREADS.client.eventIOReceiver.registerEvent(event);
    }

    @EventHook
    private void handleError(ErrorEvent errorEvent) {
        GLFWErrorCallback.createPrint(System.err).invoke(errorEvent.getError(), errorEvent.getDescription());
    }

    @EventHook
    private void handleResize(ResizeEvent resizeEvent) {
        setWindowSize(new Vector2i(resizeEvent.getWidth(), resizeEvent.getHeight()));
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

        Cursor() {
            cursorPointers = new long[CursorType.values().length][];
            animationDelays = new long[CursorType.values().length];
            int cursorNumber = 0;
            for (CursorType cursorType : CursorType.values()) {
                TextureRepository.Image imageT = new TextureRepository.Image(cursorType.file);
                cursorPointers[cursorNumber] = new long[imageT.frameData.length];
                animationDelays[cursorNumber] = imageT.frameDelay[0];
                for (int frame = 0; frame < imageT.frameData.length; ++frame) {
                    GLFWImage image = GLFWImage.malloc();
                    image.set(imageT.width, imageT.height, Utils.arrayToBuffer(imageT.frameData[frame]));
                    long cursorIndex = glfwCreateCursor(image, 0, 0);
                    image.free();
                    if (cursorIndex == MemoryUtil.NULL) {
                        throw new RuntimeException("Error creating cursor");
                    }
                    cursorPointers[cursorNumber][frame] = cursorIndex;
                }
                ++cursorNumber;
            }
            selectCursorType(CursorType.SIMPLE);
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

    }


}
