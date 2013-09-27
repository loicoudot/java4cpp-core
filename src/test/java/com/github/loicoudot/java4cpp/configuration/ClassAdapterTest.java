package com.github.loicoudot.java4cpp.configuration;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassAdapter;

public class ClassAdapterTest {

    @Test
    public void testMarshal() throws Exception {
        ClassAdapter adapter = new ClassAdapter();
        assertThat(adapter.marshal(Boolean.TYPE)).isEqualTo("boolean");
        assertThat(adapter.marshal(Byte.TYPE)).isEqualTo("byte");
        assertThat(adapter.marshal(Character.TYPE)).isEqualTo("char");
        assertThat(adapter.marshal(Double.TYPE)).isEqualTo("double");
        assertThat(adapter.marshal(Float.TYPE)).isEqualTo("float");
        assertThat(adapter.marshal(Integer.TYPE)).isEqualTo("int");
        assertThat(adapter.marshal(Long.TYPE)).isEqualTo("long");
        assertThat(adapter.marshal(Short.TYPE)).isEqualTo("short");
        assertThat(adapter.marshal(Void.TYPE)).isEqualTo("void");
        assertThat(adapter.marshal(Boolean.class)).isEqualTo("java.lang.Boolean");
        assertThat(adapter.marshal(List.class)).isEqualTo("java.util.List");
        double[] array1 = new double[0];
        assertThat(adapter.marshal(array1.getClass())).isEqualTo("[D");
        Double[] array2 = new Double[0];
        assertThat(adapter.marshal(array2.getClass())).isEqualTo("[Ljava.lang.Double;");
    }

    @Test
    public void testUnmarshal() throws Exception {
        ClassAdapter adapter = new ClassAdapter();
        assertThat(adapter.unmarshal("boolean").getName()).isEqualTo("boolean");
        assertThat(adapter.unmarshal("byte").getName()).isEqualTo("byte");
        assertThat(adapter.unmarshal("char").getName()).isEqualTo("char");
        assertThat(adapter.unmarshal("double").getName()).isEqualTo("double");
        assertThat(adapter.unmarshal("float").getName()).isEqualTo("float");
        assertThat(adapter.unmarshal("int").getName()).isEqualTo("int");
        assertThat(adapter.unmarshal("long").getName()).isEqualTo("long");
        assertThat(adapter.unmarshal("short").getName()).isEqualTo("short");
        assertThat(adapter.unmarshal("void").getName()).isEqualTo("void");
        assertThat(adapter.unmarshal("java.lang.Boolean").getName()).isEqualTo("java.lang.Boolean");
    }

    @Test(expectedExceptions = { ClassNotFoundException.class })
    public void testUnmarshalException() throws Exception {
        new ClassAdapter().unmarshal("unknown");
    }

}
