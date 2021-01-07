package com.ternsip.soil.common;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.lwjgl.BufferUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * Set of util methods that are not part of any object
 * Should contain only static methods
 * Must be thread safe
 */
@Slf4j
public class Utils {

    public static final String SOURCES_ROOT = "com.ternsip.soil";
    public static final String RESOURCES_ROOT = "soil";

    @SneakyThrows
    public static String getPath(File file) {
        // TODO think on such replacement (that was made to let uber-jar work properly)
        return file.getPath().replace("\\", "/");
    }

    @SneakyThrows
    public static synchronized BufferedReader loadResourceAsBufferedReader(File file) {
        return new BufferedReader(new InputStreamReader(loadResourceAsStream(file), StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static synchronized InputStream loadResourceAsStream(File file) {
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(getPath(file));
        if (in == null) {
            throw new FileNotFoundException("Can't find file: " + getPath(file));
        }
        return in;
    }

    public static byte[] bufferToArray(ByteBuffer buf) {
        buf.rewind();
        byte[] arr = new byte[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static int[] bufferToArray(IntBuffer buf) {
        buf.rewind();
        int[] arr = new int[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static IntBuffer arrayToBuffer(int[] array) {
        IntBuffer buf = BufferUtils.createIntBuffer(array.length);
        buf.put(array);
        buf.flip();
        return buf;
    }

    public static float[] bufferToArray(FloatBuffer buf) {
        buf.rewind();
        float[] arr = new float[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static FloatBuffer arrayToBuffer(float[] array) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(array.length);
        buf.put(array);
        buf.flip();
        return buf;
    }

    public static short[] bufferToArray(ShortBuffer buf) {
        buf.rewind();
        short[] arr = new short[buf.remaining()];
        buf.get(arr, 0, arr.length);
        return arr;
    }

    public static ShortBuffer arrayToBuffer(short[] array) {
        ShortBuffer buf = BufferUtils.createShortBuffer(array.length);
        buf.put(array);
        buf.flip();
        return buf;
    }

    public static int[] listToIntArray(List<Integer> list) {
        return ArrayUtils.toPrimitive(list.toArray(new Integer[0]), 0);
    }

    public static float[] listToFloatArray(List<Float> list) {
        return ArrayUtils.toPrimitive(list.toArray(new Float[0]), 0);
    }

    @SneakyThrows
    public static synchronized ByteBuffer loadResourceToByteBuffer(File file) {
        return arrayToBuffer(loadResourceAsByteArray(file));
    }

    public static ByteBuffer arrayToBuffer(byte[] array) {
        ByteBuffer buf = ByteBuffer.allocateDirect(array.length);
        buf.put(array);
        buf.rewind();
        return buf;
    }

    @SneakyThrows
    public static synchronized byte[] loadResourceAsByteArray(File file) {
        return IOUtils.toByteArray(loadResourceAsStream(file));
    }

    // Handle all such situations, it also can cause memory problems
    public static void assertThat(boolean condition) {
        if (!condition) {
            log.warn("Assertion failed!");
        }
    }

    public static synchronized <T> Set<Class<? extends T>> getAllClasses(Class<T> clazz) {
        Reflections reflections = new Reflections(SOURCES_ROOT, new SubTypesScanner());
        return reflections.getSubTypesOf(clazz);
    }

    public static synchronized List<File> getResourceListing(String[] extensions) {
        Reflections reflections = new Reflections(RESOURCES_ROOT, new ResourcesScanner());
        String pattern = "(.*\\." + String.join(")|(.*\\.", extensions) + ")";
        return reflections
                .getResources(Pattern.compile(pattern))
                .stream()
                .map(File::new)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public static Method findDeclaredMethodInHierarchy(Class<?> objectClass, String methodName, Class<?>... parameterTypes) {
        Class<?> targetClass = objectClass;
        while (targetClass != null) {
            try {
                return targetClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                targetClass = targetClass.getSuperclass();
            }
        }
        String msg = String.format("Can't find method %s anywhere in the class %s", methodName, objectClass.getName());
        throw new IllegalArgumentException(msg);
    }

    @SneakyThrows
    public static boolean isAnnotationPresentInHierarchy(Class<?> objectClass, Class<? extends Annotation> annotationClass) {
        Class<?> targetClass = objectClass;
        while (targetClass != null) {
            if (targetClass.isAnnotationPresent(annotationClass)) {
                return true;
            }
            targetClass = targetClass.getSuperclass();
        }
        return false;
    }

    public static Set<Object> findSubObjects(Object object) {
        return ReflectionUtils.getAllFields(object.getClass())
                .stream()
                .map(field -> getFieldValueSilently(field, object))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static Field findFieldInHierarchy(String fieldName, Class<?> objectClass) {
        return ReflectionUtils.getAllFields(objectClass)
                .stream()
                .filter(e -> e.getName().equals(fieldName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Field not found"));
    }

    @SneakyThrows
    public static <T> T createInstanceSilently(Class<? extends T> clazz) {
        return clazz.getDeclaredConstructor().newInstance();
    }

    @SneakyThrows
    public static Object invokeSilently(Method method, Object obj, Object... args) {
        return method.invoke(obj, args);
    }

    @SneakyThrows
    public static Object getFieldValueSilently(Field field, Object obj) {
        field.setAccessible(true);
        return field.get(obj);
    }


    public static boolean isSubDirectoryPresent(File file, String subDirectory) {
        for (File parentDirectory : getAllParentDirectories(file)) {
            if (parentDirectory.getName().equals(subDirectory)) {
                return true;
            }
        }
        return false;
    }

    public static Set<File> getAllParentDirectories(File file) {
        Set<File> parentDirectories = new HashSet<>();
        File parent = file.getParentFile();
        while (parent != null) {
            parentDirectories.add(parent);
            parent = parent.getParentFile();
        }
        return parentDirectories;
    }

    public static Map<File, Collection<File>> combineByParentDirectory(Collection<File> files) {
        Map<File, Collection<File>> parentToFiles = new HashMap<>();
        for (File file : files) {
            if (file.getParentFile() != null) {
                parentToFiles.computeIfAbsent(file.getParentFile(), e -> new ArrayList<>());
                parentToFiles.get(file.getParentFile()).add(file);
            }
        }
        return parentToFiles;
    }

    @SneakyThrows
    public static byte[] compress(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(out);
        dos.write(in);
        dos.flush();
        dos.close();
        return out.toByteArray();
    }

    @SneakyThrows
    public static byte[] decompress(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InflaterOutputStream ios = new InflaterOutputStream(out);
        ios.write(in);
        ios.flush();
        ios.close();
        return out.toByteArray();
    }

}
