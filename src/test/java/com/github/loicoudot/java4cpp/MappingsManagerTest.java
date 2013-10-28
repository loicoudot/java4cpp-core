package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Namespace;

class NoAnnotationNoMappings {
}

@Java4Cpp
class AnnotFalse {
    public class Inner1 {
    }
}

@Java4Cpp(name = "AnnotTrueCpp")
class AnnotTrue {
    public class Inner2 {
        @Java4Cpp(name = "InnerInner")
        public class Inner21 {
        }
    }
}

class MappingsFalse {
    public class Inner1 {
    }
}

class MappingsTrue {
    public class Inner1 {
        public class Inner11 {
        }
    }
}

public class MappingsManagerTest {

    private Context context;
    private MappingsManager mappings;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalse.class);
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrue.class);
        clazzTrue.setCppName("MappingsTrueCpp");
        local.getClasses().add(clazzTrue);

        ClassMapping clazzInner = new ClassMapping(MappingsTrue.Inner1.class);
        clazzInner.setCppName("InnerInner");
        local.getClasses().add(clazzInner);

        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.NoAnnotationNoMappings", "com::github::loicoudot::java4cpp"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.*", "cglj"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.AnnotTrue", "cglj::java"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.MappingsTrue", ""));
        local.getKeywords().add("delete");

        Settings settings = new Settings();
        settings.setTargetPath("target");
        context = new Context(settings);
        context.getMappingsManager().addMappings(local);
        context.start();
        mappings = context.getMappingsManager();
    }

    @Test
    public void testGetCppName() throws Exception {
        assertThat(mappings.getCppName(NoAnnotationNoMappings.class)).isEqualTo("NoAnnotationNoMappings");
        assertThat(mappings.getCppName(AnnotFalse.class)).isEqualTo("AnnotFalse");
        assertThat(mappings.getCppName(AnnotTrue.class)).isEqualTo("AnnotTrueCpp");
        assertThat(mappings.getCppName(MappingsFalse.class)).isEqualTo("MappingsFalse");
        assertThat(mappings.getCppName(MappingsTrue.class)).isEqualTo("MappingsTrueCpp");
    }

    @Test
    public void testGetNamespaces() throws Exception {
        assertThat(mappings.getNamespace(NoAnnotationNoMappings.class)).containsOnly("com", "github", "loicoudot", "java4cpp", "NoAnnotationNoMappings");
        assertThat(mappings.getNamespace(AnnotFalse.class)).containsOnly("cglj", "AnnotFalse");
        assertThat(mappings.getNamespace(AnnotTrue.class)).containsOnly("cglj", "java", "AnnotTrueCpp");
        assertThat(mappings.getNamespace(MappingsFalse.class)).containsOnly("cglj", "MappingsFalse");
        assertThat(mappings.getNamespace(MappingsTrue.class)).containsOnly("MappingsTrueCpp");

        assertThat(mappings.getNamespace(AnnotFalse.Inner1.class)).containsOnly("cglj", "AnnotFalse", "Inner1");
        assertThat(mappings.getNamespace(AnnotTrue.Inner2.class)).containsOnly("cglj", "java", "AnnotTrueCpp", "Inner2");
        assertThat(mappings.getNamespace(AnnotTrue.Inner2.Inner21.class)).containsOnly("cglj", "java", "AnnotTrueCpp", "Inner2", "InnerInner");
        assertThat(mappings.getNamespace(MappingsFalse.Inner1.class)).containsOnly("cglj", "MappingsFalse", "Inner1");
        assertThat(mappings.getNamespace(MappingsTrue.Inner1.class)).containsOnly("MappingsTrueCpp", "InnerInner");
        assertThat(mappings.getNamespace(MappingsTrue.Inner1.Inner11.class)).containsOnly("MappingsTrueCpp", "InnerInner", "Inner11");
    }

    @Test
    public void testEscapeName() throws Exception {
        assertThat(mappings.escapeName("escape")).isEqualTo("escape");
        assertThat(mappings.escapeName("delete")).isEqualTo("delete_");
    }
}