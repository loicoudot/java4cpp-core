package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class DatatypeTest {

    @Test
    public void testGetJavaSignature() throws Exception {
        assertThat(Datatype.getJavaSignature(Boolean.TYPE)).isEqualTo("Z");
        assertThat(Datatype.getJavaSignature(Boolean.class)).isEqualTo("Ljava/lang/Boolean;");
        assertThat(Datatype.getJavaSignature(new Boolean[1].getClass())).isEqualTo("[Ljava/lang/Boolean;");
        assertThat(Datatype.getJavaSignature(new boolean[1].getClass())).isEqualTo("[Z");
        assertThat(Datatype.getJavaSignature(new Boolean[1][1].getClass())).isEqualTo("[[Ljava/lang/Boolean;");
        assertThat(Datatype.getJavaSignature(new boolean[1][1].getClass())).isEqualTo("[[Z");
    }

    @Test
    public void testGetJNISignature() throws Exception {
        assertThat(Datatype.getJNISignature(Boolean.TYPE)).isEqualTo("jboolean");
        assertThat(Datatype.getJNISignature(Boolean.class)).isEqualTo("jobject");
        assertThat(Datatype.getJNISignature(new Boolean[1].getClass())).isEqualTo("jobjectArray");
        assertThat(Datatype.getJNISignature(new boolean[1].getClass())).isEqualTo("jbooleanArray");
        assertThat(Datatype.getJNISignature(new Boolean[1][1].getClass())).isEqualTo("jobjectArray");
        assertThat(Datatype.getJNISignature(new boolean[1][1].getClass())).isEqualTo("jobjectArray");
    }

    @Test
    public void testGetJNIMethodName() throws Exception {
        assertThat(Datatype.getJNIMethodName(Boolean.TYPE)).isEqualTo("Boolean");
        assertThat(Datatype.getJNIMethodName(Boolean.class)).isEqualTo("Object");
        assertThat(Datatype.getJNIMethodName(new Boolean[1].getClass())).isEqualTo("Object");
        assertThat(Datatype.getJNIMethodName(new boolean[1].getClass())).isEqualTo("Object");
        assertThat(Datatype.getJNIMethodName(new Boolean[1][1].getClass())).isEqualTo("Object");
        assertThat(Datatype.getJNIMethodName(new boolean[1][1].getClass())).isEqualTo("Object");
    }

    @Test
    public void testGenerateJNISignature() throws Exception {
        assertThat(Datatype.generateJNISignature(new Class<?>[] { Boolean.TYPE, Boolean.class })).isEqualTo("ZLjava/lang/Boolean;");
    }
}
