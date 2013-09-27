package com.github.loicoudot.java4cpp.configuration;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Namespace;

public class NamespaceTest {

    @Test
    public void testToString() throws Exception {
        assertThat(new Namespace("a", "b").toString()).isEqualTo("Namespace(a, b)");
    }

}
