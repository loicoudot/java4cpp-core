package com.github.loicoudot.java4cpp.configuration;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Clazz;

public class ClazzTest {

    @Test
    public void testToString() throws Exception {
        assertThat(new Clazz(Boolean.class).toString()).isEqualTo("Clazz(java.lang.Boolean)");
        assertThat(new Clazz(Boolean.TYPE).toString()).isEqualTo("Clazz(boolean)");
    }

}
