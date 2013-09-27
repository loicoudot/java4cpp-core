package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
    private ClassAnalyzer analyzer;
    private ClassAnalyzer primitiveAnalyzer;

    @BeforeClass
    public void init() {
        Settings settings = new Settings();
        context = new Context(settings);
        analyzer = new ClassAnalyzer(TestClasse.class, context);
        primitiveAnalyzer = new ClassAnalyzer(Boolean.TYPE, context);
    }

    @DataProvider(name = "class")
    public Object[][] createData() {
        return new Object[][] { { Error.class, false }, { RuntimeException.class, false }, { ArrayIndexOutOfBoundsException.class, false },
                { Exception.class, true }, { ClassNotFoundException.class, true }, { String.class, false } };
    }

    @Test(dataProvider = "class")
    public void testIsCheckedException(Class<?> type, boolean isCheckedException) {
        assertThat(ClassAnalyzer.isCheckedException(type)).isEqualTo(isCheckedException);
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
        assertThat(model.getCppType()).isEqualTo("bool");
        assertThat(model.getCppReturnType()).isEqualTo("bool");
        assertThat(model.getSuperclass()).isNull();
        assertThat(model.getOutterIncludes()).isEmpty();
        assertThat(model.getOutterDependencies()).isEmpty();

        model = new ClassModel(TestClasse.class);
        analyzer.fillModel(model);
        assertThat(model.getJavaName()).isEqualTo("com.github.loicoudot.java4cpp.TestClasse");
        assertThat(model.isIsEnum()).isFalse();
        assertThat(model.isIsInterface()).isFalse();
        assertThat(model.isIsInnerClass()).isFalse();
        assertThat(model.isIsCheckedException()).isFalse();
        assertThat(model.isIsCloneable()).isFalse();
        assertThat(model.getOwner()).isEqualTo(model);
        assertThat(model.getCppFullName()).isEqualTo("com::github::loicoudot::java4cpp::TestClasse");
        assertThat(model.getCppShortName()).isEqualTo("TestClasse");
        assertThat(model.getJavaSignature()).isEqualTo("Lcom/github/loicoudot/java4cpp/TestClasse;");
        assertThat(model.getJniSignature()).isEqualTo("jobject");
        assertThat(model.getJniMethodName()).isEqualTo("Object");
        assertThat(model.getCppType()).isEqualTo("boost::shared_ptr<com::github::loicoudot::java4cpp::TestClasse>");
        assertThat(model.getCppReturnType()).isEqualTo("boost::shared_ptr<com::github::loicoudot::java4cpp::TestClasse>");
        assertThat(model.getSuperclass().getClazz()).hasSameClassAs(D.class);
        assertThat(model.getOutterIncludes()).contains("<boost/shared_ptr.hpp>");
        // assertThat(model.getDependencies()).isEmpty();
    }
}
