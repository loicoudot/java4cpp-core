package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;
import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.TemplateModelException;

public class ClassAnalyzerTest {

    private Context context;
    private ClassAnalyzer analyzer;

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
        analyzer = new ClassAnalyzer(context);
    }

    @Test
    public void testFill() throws Exception {
        ClassModel model = new ClassModel(Boolean.TYPE);
        analyzer.fill(model);
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

    @Test(expectedExceptions = { TemplateModelException.class })
    public void testDependencyException() throws Exception {
        ClassModel model = new ClassModel(Boolean.TYPE);
        analyzer.fill(model);
        model.getAddDependency().exec(new ArrayList<String>());
    }

    @Test(expectedExceptions = { TemplateModelException.class })
    public void testIncludeException() throws Exception {
        ClassModel model = new ClassModel(Boolean.TYPE);
        analyzer.fill(model);
        model.getAddInclude().exec(new ArrayList<String>());
    }

    @Test
    public void testToString() throws Exception {
        assertThat(analyzer.toString()).isEqualTo("ClassAnalyzer(boolean)");
    }
}
