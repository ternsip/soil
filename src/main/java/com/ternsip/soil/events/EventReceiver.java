package com.ternsip.soil.events;

import com.ternsip.soil.common.Utils;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * That class can potentially be used in any thread to access collected events
 * Essentially, it is receiver to obtain graphical or networking data thread-safely in one batch
 * You can work with fields in the thread without worrying about changes
 * The changes occurs only after calling update method
 * Receiving events is allowed from any thread
 * If you log event with unsafe variables like links to unsafe objects you should:
 * - do that only in the same thread with callbacks
 * - avoid such situation if it is possible
 * It is supposed to use callbacks in the original thread only
 * During update method some data may be refreshed in another thread and hence be applied in current
 *
 * @author Ternsip
 */
@Getter
public class EventReceiver {

    private final HashMap<Class, Set<Callback>> classToCallbacks = new HashMap<>();
    private final HashMap<Object, Set<Callback>> objectToCallbacks = new HashMap<>();
    private final LinkedList<Object> objectsToUnregister = new LinkedList<>();
    private final LinkedBlockingQueue events = new LinkedBlockingQueue<>();

    public EventReceiver() {
        for (Class<? extends Event> clazz : Utils.getAllClasses(Event.class)) {
            classToCallbacks.computeIfAbsent(clazz, e -> new HashSet<>());
        }
    }

    public <T extends Event> void registerEvent(T event) {
        events.add(event);
    }

    public void update() {
        while (!getEvents().isEmpty()) {
            Object event = events.poll();
            for (Callback callback : classToCallbacks.get(event.getClass())) {
                callback.apply((Event) event);
            }
        }
        purge();
    }

    public void purge() {
        while (!objectsToUnregister.isEmpty()) {
            Object obj = objectsToUnregister.poll();
            Set<Callback> callbacks = objectToCallbacks.remove(obj);
            if (callbacks == null) {
                continue;
            }
            for (Set<Callback> callbackSet : classToCallbacks.values()) {
                callbackSet.removeIf(callbacks::contains);
            }
        }
    }

    public void registerWithSubObjects(Object obj) {
        register(obj);
        Utils.findSubObjects(obj).forEach(this::register);
    }


    public void register(Object obj) {
        purge();
        if (objectToCallbacks.containsKey(obj)) {
            throw new IllegalArgumentException("This object is already registered");
        }
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHook.class)) {
                continue;
            }
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Method annotated with EventHook has to have only one argument");
            }
            if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                throw new IllegalArgumentException("Method annotated with EventHook must include Event heir as a parameter");
            }
            method.setAccessible(true);
            Callback callback = e -> Utils.invokeSilently(method, obj, e);
            classToCallbacks.get(method.getParameterTypes()[0]).add(callback);
            objectToCallbacks.computeIfAbsent(obj, e -> new HashSet<>()).add(callback);
        }
    }

    public void unregisterWithSubObjects(Object obj) {
        unregister(obj);
        Utils.findSubObjects(obj).forEach(this::unregister);
    }


    public void unregister(Object obj) {
        objectsToUnregister.add(obj);
    }

}
