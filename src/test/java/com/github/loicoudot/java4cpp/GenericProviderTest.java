package com.github.loicoudot.java4cpp;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericProviderTest {

    public static List<Double> listType;
    public static Map<String, Double> mapType;
    public static List<Set<Double>> listOfSetType;

    private GenericProviderTest() {
    }

    public static <E> void generic(E arg) {
    }

    public static <T> void anotherGeneric(T arg) {
    }

    public static <E extends GenericProviderTest> void genericExtends(E arg) {
    }

    public static <E> void genericList(List<E> list) {
    }

    public static <T> void anotherGenericList(List<T> list) {
    }

    public static <E extends GenericProviderTest> void genericListExtends(List<E> list) {
    }

    public static void genericListWildcard(List<?> list) {
    }

    public static void genericListWildcardExtends(List<? extends GenericProviderTest> list) {
    }

    public static Type getListOfDoubleType() throws SecurityException, NoSuchFieldException {
        return GenericProviderTest.class.getField("listType").getGenericType();
    }

    public static Type getMapOfStringDoubleType() throws SecurityException, NoSuchFieldException {
        return GenericProviderTest.class.getField("mapType").getGenericType();
    }

    public static Type getListOfSetOfDouble() throws SecurityException, NoSuchFieldException {
        return GenericProviderTest.class.getField("listOfSetType").getGenericType();
    }

    public static Type getGenericType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("generic", Object.class).getGenericParameterTypes()[0];
    }

    public static Type getAnotherGenericType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("anotherGeneric", Object.class).getGenericParameterTypes()[0];
    }

    public static Type getGenericExtendsType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("genericExtends", GenericProviderTest.class).getGenericParameterTypes()[0];
    }

    public static Type getGenericListType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("genericList", List.class).getGenericParameterTypes()[0];
    }

    public static Type getAnotherGenericListType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("anotherGenericList", List.class).getGenericParameterTypes()[0];
    }

    public static Type getGenericListExtendsType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("genericListExtends", List.class).getGenericParameterTypes()[0];
    }

    public static Type getWildcardListType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("genericListWildcard", List.class).getGenericParameterTypes()[0];
    }

    public static Type getWildcardListExtendsType() throws SecurityException, NoSuchMethodException {
        return GenericProviderTest.class.getMethod("genericListWildcardExtends", List.class).getGenericParameterTypes()[0];
    }
}
