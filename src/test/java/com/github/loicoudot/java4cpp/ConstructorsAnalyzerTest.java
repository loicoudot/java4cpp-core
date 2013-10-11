package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

class NoAnnotationNoMappingsConstructors {
    public NoAnnotationNoMappingsConstructors() {
    }

    public NoAnnotationNoMappingsConstructors(String a) {
    }
}

@Java4Cpp(all = false)
class AnnotFalseConstructors {
    public AnnotFalseConstructors() {
    }

    @Java4CppWrappe
    public AnnotFalseConstructors(String a) {
    }
}

@Java4Cpp(all = true)
class AnnotTrueConstructors {
    public AnnotTrueConstructors() {
    }

    @Java4CppNoWrappe
    public AnnotTrueConstructors(String a) {
    }
}

class MappingsFalseConstructors {
    public MappingsFalseConstructors() {
    }

    public MappingsFalseConstructors(String a) {
    }
}

class MappingsTrueConstructors {
    public MappingsTrueConstructors() {
    }

    public MappingsTrueConstructors(String a) {
    }
}

public class ConstructorsAnalyzerTest {

    private Context context;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalseConstructors.class);
        clazzFalse.setExportAll(false);
        clazzFalse.getConstructors().getWrappes().add(new Wrappe(""));
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrueConstructors.class);
        clazzTrue.setExportAll(true);
        clazzTrue.getConstructors().getNoWrappes().add("Ljava/lang/String;");
        local.getClasses().add(clazzTrue);

        context = new Context(new Settings());
        context.getMappingsManager().addMappings(local);
        context.start();
    }

    @Test
    public void testIsConstructorWrapped() throws Exception {
        ConstructorsAnalyzer analyzer = new ConstructorsAnalyzer(context);
        assertThat(analyzer.isConstructorWrapped(NoAnnotationNoMappingsConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(NoAnnotationNoMappingsConstructors.class.getConstructor(String.class))).isTrue();
        assertThat(analyzer.isConstructorWrapped(AnnotFalseConstructors.class.getConstructor())).isFalse();
        assertThat(analyzer.isConstructorWrapped(AnnotFalseConstructors.class.getConstructor(String.class))).isTrue();
        assertThat(analyzer.isConstructorWrapped(AnnotTrueConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(AnnotTrueConstructors.class.getConstructor(String.class))).isFalse();
        assertThat(analyzer.isConstructorWrapped(MappingsFalseConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(MappingsFalseConstructors.class.getConstructor(String.class))).isFalse();
        assertThat(analyzer.isConstructorWrapped(MappingsTrueConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(MappingsTrueConstructors.class.getConstructor(String.class))).isFalse();
    }
}