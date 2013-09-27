package com.github.loicoudot.java4cpp;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.Context;
import com.github.loicoudot.java4cpp.Settings;

public class DatatypeTest {

    private Context context;

    @BeforeClass
    public void init() {
        context = new Context(new Settings());
    }

    @Test
    public void testGetJavaSignature() throws Exception {
        /*
         * assertThat(new Datatype(Boolean.TYPE,
         * context).getJavaSignature()).isEqualTo("Z"); assertThat(new
         * Datatype(Boolean.class,
         * context).getJavaSignature()).isEqualTo("Ljava/lang/Boolean;");
         * assertThat(new Datatype(new Boolean[1].getClass(),
         * context).getJavaSignature()).isEqualTo("[Ljava/lang/Boolean;");
         * assertThat(new Datatype(new boolean[1].getClass(),
         * context).getJavaSignature()).isEqualTo("[Z"); assertThat(new
         * Datatype(new Boolean[1][1].getClass(),
         * context).getJavaSignature()).isEqualTo("[[Ljava/lang/Boolean;");
         * assertThat(new Datatype(new boolean[1][1].getClass(),
         * context).getJavaSignature()).isEqualTo("[[Z");
         */}

    @Test
    public void testGetJNISignature() throws Exception {
        /*
         * assertThat(new Datatype(Boolean.TYPE,
         * context).getJNISignature()).isEqualTo("jboolean"); assertThat(new
         * Datatype(Boolean.class,
         * context).getJNISignature()).isEqualTo("jobject"); assertThat(new
         * Datatype(new Boolean[1].getClass(),
         * context).getJNISignature()).isEqualTo("jobjectArray"); assertThat(new
         * Datatype(new boolean[1].getClass(),
         * context).getJNISignature()).isEqualTo("jbooleanArray");
         * assertThat(new Datatype(new Boolean[1][1].getClass(),
         * context).getJNISignature()).isEqualTo("jobjectArray"); assertThat(new
         * Datatype(new boolean[1][1].getClass(),
         * context).getJNISignature()).isEqualTo("jobjectArray");
         */}

    @Test
    public void testGetJNIMethodName() throws Exception {
        /*
         * assertThat(new Datatype(Boolean.TYPE,
         * context).getJNIMethodName()).isEqualTo("Boolean"); assertThat(new
         * Datatype(Boolean.class,
         * context).getJNIMethodName()).isEqualTo("Object"); assertThat(new
         * Datatype(new Boolean[1].getClass(),
         * context).getJNIMethodName()).isEqualTo("Object"); assertThat(new
         * Datatype(new boolean[1].getClass(),
         * context).getJNIMethodName()).isEqualTo("Object"); assertThat(new
         * Datatype(new Boolean[1][1].getClass(),
         * context).getJNIMethodName()).isEqualTo("Object"); assertThat(new
         * Datatype(new boolean[1][1].getClass(),
         * context).getJNIMethodName()).isEqualTo("Object");
         */}

    @Test
    public void testGetCppReturnType() throws Exception {
        /*
         * assertThat(new Datatype(Boolean.TYPE,
         * context).getCppReturnType()).isEqualTo("bool"); assertThat(new
         * Datatype(Byte.TYPE,
         * context).getCppReturnType()).isEqualTo("unsigned char");
         * assertThat(new Datatype(Character.TYPE,
         * context).getCppReturnType()).isEqualTo("char"); assertThat(new
         * Datatype(Double.TYPE,
         * context).getCppReturnType()).isEqualTo("double"); assertThat(new
         * Datatype(Float.TYPE, context).getCppReturnType()).isEqualTo("float");
         * assertThat(new Datatype(Integer.TYPE,
         * context).getCppReturnType()).isEqualTo("int"); assertThat(new
         * Datatype(Long.TYPE, context).getCppReturnType()).isEqualTo("long");
         * assertThat(new Datatype(Short.TYPE,
         * context).getCppReturnType()).isEqualTo("short");
         */}

}
