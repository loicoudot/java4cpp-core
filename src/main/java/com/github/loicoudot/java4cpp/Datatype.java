package com.github.loicoudot.java4cpp;

import java.util.HashMap;
import java.util.Map;

final class Datatype {

    private static final Map<Class<?>, String> JAVA_SIGNATURE = new HashMap<Class<?>, String>();
    private static final Map<Class<?>, String> JNI_SIGNATURE = new HashMap<Class<?>, String>();
    private static final Map<Class<?>, String> JNI_PART_NAME = new HashMap<Class<?>, String>();

    static {
        JAVA_SIGNATURE.put(Boolean.TYPE, "Z");
        JAVA_SIGNATURE.put(Byte.TYPE, "B");
        JAVA_SIGNATURE.put(Character.TYPE, "C");
        JAVA_SIGNATURE.put(Double.TYPE, "D");
        JAVA_SIGNATURE.put(Float.TYPE, "F");
        JAVA_SIGNATURE.put(Integer.TYPE, "I");
        JAVA_SIGNATURE.put(Long.TYPE, "J");
        JAVA_SIGNATURE.put(Short.TYPE, "S");
        JAVA_SIGNATURE.put(Void.TYPE, "V");

        JNI_SIGNATURE.put(Boolean.TYPE, "jboolean");
        JNI_SIGNATURE.put(Byte.TYPE, "jbyte");
        JNI_SIGNATURE.put(Character.TYPE, "jchar");
        JNI_SIGNATURE.put(Double.TYPE, "jdouble");
        JNI_SIGNATURE.put(Float.TYPE, "jfloat");
        JNI_SIGNATURE.put(Integer.TYPE, "jint");
        JNI_SIGNATURE.put(Long.TYPE, "jlong");
        JNI_SIGNATURE.put(Short.TYPE, "jshort");

        JNI_PART_NAME.put(Boolean.TYPE, "Boolean");
        JNI_PART_NAME.put(Byte.TYPE, "Byte");
        JNI_PART_NAME.put(Character.TYPE, "Char");
        JNI_PART_NAME.put(Double.TYPE, "Double");
        JNI_PART_NAME.put(Float.TYPE, "Float");
        JNI_PART_NAME.put(Integer.TYPE, "Int");
        JNI_PART_NAME.put(Long.TYPE, "Long");
        JNI_PART_NAME.put(Short.TYPE, "Short");
        JNI_PART_NAME.put(Void.TYPE, "Void");
    }

    public static String getJavaSignature(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return JAVA_SIGNATURE.get(clazz);
        }
        if (clazz.isArray()) {
            return clazz.getName().replace('.', '/');
        }
        return 'L' + clazz.getName().replace('.', '/') + ';';
    }

    public static String getJNISignature(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return JNI_SIGNATURE.get(clazz);
        }
        if (clazz.isArray()) {
            if (clazz.getComponentType().isPrimitive()) {
                return JNI_SIGNATURE.get(clazz.getComponentType()) + "Array";
            }
            return "jobjectArray";
        }
        return "jobject";
    }

    public static String getJNIMethodName(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return JNI_PART_NAME.get(clazz);
        }
        return "Object";
    }

    /**
     * Construct a JNI signature string from a series of parameters.
     * 
     * @param params
     *            a list of paramters
     * @return a {@code String} containing the corresponding JNI signature.
     */
    public static String generateJNISignature(Class<?>[] params) {
        StringBuilder ret = new StringBuilder();

        for (Class<?> param : params) {
            ret.append(getJavaSignature(param));
        }

        return ret.toString();
    }
}