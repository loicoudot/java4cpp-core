package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

class NoAnnotationNoMappingsMethods {
    public void method1() {
    }

    public void method2(String a) {
    }
}

@Java4Cpp(all = false)
class AnnotFalseMethods {
    @Java4CppWrappe("methodCpp")
    public void method1() {
    }

    public void method2(String a) {
    }
}

@Java4Cpp(all = true)
class AnnotTrueMethods {
    @Java4CppNoWrappe
    public void method1() {
    }

    @Java4CppWrappe("methodCpp")
    public void method2(String a) {
    }
}

class MappingsFalseMethods {
    public void method1() {
    }

    public void method2(String a) {
    }
}

class MappingsTrueMethods {
    public void method1() {
    }

    public void method2(String a) {
    }
}

public class MethodsAnalyzerTest {

    private Context context;
    private MethodsAnalyzer analyzer;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalseMethods.class);
        clazzFalse.setExportAll(false);
        clazzFalse.getMethods().getWrappes().add(new Wrappe("method1()", "methodCpp"));
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrueMethods.class);
        clazzTrue.setExportAll(true);
        clazzTrue.getMethods().getNoWrappes().add("method2(Ljava/lang/String;)");
        local.getClasses().add(clazzTrue);

        context = new Context(new Settings());
        context.getMappingsManager().addMappings(local);
        context.start();
        analyzer = new MethodsAnalyzer(context);
    }

    @Test
    public void testIsMethodWrapped() throws Exception {
        assertThat(analyzer.isMethodWrapped(NoAnnotationNoMappingsMethods.class.getMethod("method1"))).isTrue();
        assertThat(analyzer.isMethodWrapped(NoAnnotationNoMappingsMethods.class.getMethod("method2", String.class))).isTrue();
        assertThat(analyzer.isMethodWrapped(AnnotFalseMethods.class.getMethod("method1"))).isTrue();
        assertThat(analyzer.isMethodWrapped(AnnotFalseMethods.class.getMethod("method2", String.class))).isFalse();
        assertThat(analyzer.isMethodWrapped(AnnotTrueMethods.class.getMethod("method1"))).isFalse();
        assertThat(analyzer.isMethodWrapped(AnnotTrueMethods.class.getMethod("method2", String.class))).isTrue();
        assertThat(analyzer.isMethodWrapped(MappingsFalseMethods.class.getMethod("method1"))).isTrue();
        assertThat(analyzer.isMethodWrapped(MappingsFalseMethods.class.getMethod("method2", String.class))).isFalse();
        assertThat(analyzer.isMethodWrapped(MappingsTrueMethods.class.getMethod("method1"))).isTrue();
        assertThat(analyzer.isMethodWrapped(MappingsTrueMethods.class.getMethod("method2", String.class))).isFalse();
    }

    @Test
    public void testGetCppName() throws Exception {
        assertThat(analyzer.getCppName(AnnotFalseMethods.class.getMethod("method1"))).isEqualTo("methodCpp");
        assertThat(analyzer.getCppName(AnnotFalseMethods.class.getMethod("method2", String.class))).isEqualTo("method2");
        assertThat(analyzer.getCppName(AnnotTrueMethods.class.getMethod("method1"))).isEqualTo("method1");
        assertThat(analyzer.getCppName(AnnotTrueMethods.class.getMethod("method2", String.class))).isEqualTo("methodCpp");
        assertThat(analyzer.getCppName(MappingsFalseMethods.class.getMethod("method1"))).isEqualTo("methodCpp");
        assertThat(analyzer.getCppName(MappingsFalseMethods.class.getMethod("method2", String.class))).isEqualTo("method2");
        assertThat(analyzer.getCppName(MappingsTrueMethods.class.getMethod("method1"))).isEqualTo("method1");
        assertThat(analyzer.getCppName(MappingsTrueMethods.class.getMethod("method2", String.class))).isEqualTo("method2");
    }
}