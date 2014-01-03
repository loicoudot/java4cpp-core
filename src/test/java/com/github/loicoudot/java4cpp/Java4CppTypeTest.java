package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.Type;

import org.testng.annotations.Test;

public class Java4CppTypeTest {

    @Test
    public void testListOfDouble() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getListOfDoubleType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.util.List");
        assertThat(j4c.getParameterizedTypes()).hasSize(1);
        assertThat(j4c.getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("java.lang.Double");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testMapOfStringDouble() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getMapOfStringDoubleType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.util.Map");
        assertThat(j4c.getParameterizedTypes()).hasSize(2);
        assertThat(j4c.getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("java.lang.String");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes()).isEmpty();
        assertThat(j4c.getParameterizedTypes().get(1).getRawClass().getName()).isEqualTo("java.lang.Double");
        assertThat(j4c.getParameterizedTypes().get(1).getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testListOfSetOfDouble() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getListOfSetOfDouble());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.util.List");
        assertThat(j4c.getParameterizedTypes()).hasSize(1);
        assertThat(j4c.getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("java.util.Set");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes()).hasSize(1);
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("java.lang.Double");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes().get(0).getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testGeneric() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getGenericType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.lang.Object");
        assertThat(j4c.getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testGenericExtends() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getGenericExtendsType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("com.github.loicoudot.java4cpp.GenericProviderTest");
        assertThat(j4c.getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testGenericList() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getGenericListType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.util.List");
        assertThat(j4c.getParameterizedTypes()).hasSize(1);
        assertThat(j4c.getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("java.lang.Object");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testGenericListExtends() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getGenericListExtendsType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.util.List");
        assertThat(j4c.getParameterizedTypes()).hasSize(1);
        assertThat(j4c.getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("com.github.loicoudot.java4cpp.GenericProviderTest");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testWildcardList() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getWildcardListType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.util.List");
        assertThat(j4c.getParameterizedTypes()).hasSize(1);
        assertThat(j4c.getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("java.lang.Object");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testWildcardListExtends() throws Exception {
        Java4CppType j4c = Java4CppType.fromType(GenericProviderTest.getWildcardListExtendsType());
        assertThat(j4c.getRawClass().getName()).isEqualTo("java.util.List");
        assertThat(j4c.getParameterizedTypes()).hasSize(1);
        assertThat(j4c.getParameterizedTypes().get(0).getRawClass().getName()).isEqualTo("com.github.loicoudot.java4cpp.GenericProviderTest");
        assertThat(j4c.getParameterizedTypes().get(0).getParameterizedTypes()).isEmpty();
    }

    @Test
    public void testEquals() throws Exception {
        Type one = GenericProviderTest.getGenericType();
        Type two = GenericProviderTest.getAnotherGenericType();
        assertThat(one == two).isFalse();
        Java4CppType j4cOne = Java4CppType.fromType(one);
        Java4CppType j4cTwo = Java4CppType.fromType(two);
        assertThat(j4cOne == j4cTwo).isTrue();

        one = GenericProviderTest.getGenericListType();
        two = GenericProviderTest.getAnotherGenericListType();
        assertThat(one == two).isFalse();
        j4cOne = Java4CppType.fromType(one);
        j4cTwo = Java4CppType.fromType(two);
        assertThat(j4cOne == j4cTwo).isTrue();
    }

    @Test
    public void testNotEquals() throws Exception {
        Type one = GenericProviderTest.getGenericListType();
        Type two = GenericProviderTest.getGenericListExtendsType();
        assertThat(one == two).isFalse();
        Java4CppType j4cOne = Java4CppType.fromType(one);
        Java4CppType j4cTwo = Java4CppType.fromType(two);
        assertThat(j4cOne == j4cTwo).isFalse();
    }

}
