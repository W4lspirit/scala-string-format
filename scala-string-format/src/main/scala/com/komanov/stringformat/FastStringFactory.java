package com.komanov.stringformat;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class FastStringFactory {

    private static final MethodHandle stringBuilderValueGetter = getValueHandler();
    private static final MethodHandle newString = getNewStringHandler();

    public static String fastNewString(StringBuilder sb) throws Throwable {
        if (sb.capacity() != sb.length()) {
            throw new IllegalArgumentException("Expected filled StringBuilder!");
        }

        return fastNewString(getValue(sb));
    }

    public static byte[] getValue(StringBuilder sb) throws Throwable {
        return (byte[]) stringBuilderValueGetter.invoke(sb);
    }

    public static String fastNewString(byte[] bytes) throws Throwable {
        return (String) newString.invokeExact(bytes);
    }

    private static MethodHandle getValueHandler() {
        try {
            Field field = getValueField();
            return MethodHandles.lookup().unreflectGetter(field);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getValueField() throws NoSuchFieldException {
        Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
        field.setAccessible(true);
        return field;
    }

    private static MethodHandle getNewStringHandler() {
        try {
            Constructor<String> constructor = String.class.getDeclaredConstructor(byte[].class);
            constructor.setAccessible(true);
            return MethodHandles.lookup().unreflectConstructor(constructor);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
