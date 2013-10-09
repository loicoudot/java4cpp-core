package com.github.loicoudot.java4cpp.configuration;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;

public class ClassMappingTest {

    @Test
    public void testToString() throws Exception {
        assertThat(new ClassMapping(Boolean.class).toString()).isEqualTo("Clazz(java.lang.Boolean)");
        assertThat(new ClassMapping(Boolean.TYPE).toString()).isEqualTo("Clazz(boolean)");
    }

}
