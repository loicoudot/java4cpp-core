package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;
import com.github.loicoudot.java4cpp.model.ClassModel;

interface IA {
}

interface IB {
}

interface IC {
}

class D {
}

@Java4Cpp(superclass = true, interfaces = true, staticFields = true)
class TestClasse extends D implements IB, IA, IC {

    public TestClasse(int a) {
    }

    public TestClasse(long a) {
    }

    public TestClasse() {
    }

    public TestClasse(String a) {
    }

    public TestClasse(int a, int b) {
    }

    TestClasse(double a) {
    }

    public void add() {
    }

    public void additive() {
    }

    public void method(int a) {
    }

    public int method(long a) {
        return 0;
    }

    public long method() {
        return 0;
    }

    public String method(List<String> a) {
        return a.get(0);
    }

    public void method(int a, int b) {
    }

    public enum EA {
        EA1
    }

    public static String sC;

    public static class InnerB implements Cloneable {

    }

    public static String sA;

    public enum EB {
        EB1
    }

    public static String sB;

    public class InnerA {

    }
}

public class ClassAnalyzerTest {

    private Context context;
    private ClassAnalyzer primitiveAnalyzer;

    @BeforeClass
    public void init() {
        Settings settings = new Settings();
        context = new Context(settings);
        Templates other = new Templates();
        TypeTemplate classTemplate = new TypeTemplate();
        classTemplate.setClazz(Boolean.TYPE);
        classTemplate.setCppType("cppType");
        classTemplate.setCppReturnType("cppReturnType");
        other.getDatatypes().getTemplates().add(classTemplate);
        context.getTemplateManager().addTemplates(other);
        context.start();
        primitiveAnalyzer = new ClassAnalyzer(Boolean.TYPE, context);
    }

    @Test
    public void testGetModel() throws Exception {
        ClassModel model = new ClassModel(Boolean.TYPE);
        primitiveAnalyzer.fillModel(model);
        assertThat(model.getJavaName()).isEqualTo("boolean");
        assertThat(model.isIsEnum()).isFalse();
        assertThat(model.isIsInterface()).isFalse();
        assertThat(model.isIsInnerClass()).isFalse();
        assertThat(model.isIsCheckedException()).isFalse();
        assertThat(model.isIsCloneable()).isFalse();
        assertThat(model.getOwner()).isEqualTo(model);
        assertThat(model.getCppFullName()).isEqualTo("boolean");
        assertThat(model.getCppShortName()).isEqualTo("boolean");
        assertThat(model.getJavaSignature()).isEqualTo("Z");
        assertThat(model.getJniSignature()).isEqualTo("jboolean");
        assertThat(model.getJniMethodName()).isEqualTo("Boolean");
        assertThat(model.getCppType()).isEqualTo("cppType");
        assertThat(model.getCppReturnType()).isEqualTo("cppReturnType");
        assertThat(model.getSuperclass()).isNull();
        assertThat(model.getOutterIncludes()).isEmpty();
        assertThat(model.getOutterDependencies()).isEmpty();
    }
}
