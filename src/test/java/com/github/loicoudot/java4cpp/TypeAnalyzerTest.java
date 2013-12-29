package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;
import com.github.loicoudot.java4cpp.model.ClassModel;
import com.github.loicoudot.java4cpp.model.ClassType;

import freemarker.template.TemplateModelException;

public class TypeAnalyzerTest {

    private Context context;
    private TypeAnalyzer analyzer;

    @BeforeClass
    public void init() {
        Settings settings = new Settings();
        settings.setTargetPath("target");
        context = new Context(settings);
        Templates other = new Templates();
        TypeTemplate classTemplate = new TypeTemplate();
        classTemplate.setClazz(Boolean.TYPE);
        classTemplate.setNeedAnalyzing(true);
        classTemplate.setCppType("cppType");
        classTemplate.setCppReturnType("cppReturnType");
        other.getDatatypes().getTemplates().add(classTemplate);
        other.getDatatypes().setArray(classTemplate);
        other.getDatatypes().setFallback(classTemplate);
        context.getTemplateManager().addTemplates(other);
        context.start();
        analyzer = new TypeAnalyzer(context);
    }

    @Test
    public void testFill() throws Exception {
        ClassModel model = new ClassModel(Boolean.TYPE);
        analyzer.fill(model);
        ClassType type = model.getType();
        assertThat(type.getJavaName()).isEqualTo("boolean");
        assertThat(type.isIsEnum()).isFalse();
        assertThat(type.isIsInterface()).isFalse();
        assertThat(type.isIsInnerClass()).isFalse();
        assertThat(type.isIsCheckedException()).isFalse();
        assertThat(type.isIsCloneable()).isFalse();
        assertThat(type.getOwner()).isEqualTo(model);
        assertThat(type.getCppFullName()).isEqualTo("boolean");
        assertThat(type.getCppShortName()).isEqualTo("boolean");
        assertThat(type.getJavaSignature()).isEqualTo("Z");
        assertThat(type.getJniSignature()).isEqualTo("jboolean");
        assertThat(type.getJniMethodName()).isEqualTo("Boolean");
        assertThat(type.getCppType()).isEqualTo("cppType");
        assertThat(type.getCppReturnType()).isEqualTo("cppReturnType");
        assertThat(type.getIncludes()).isEmpty();
        assertThat(type.getDependencies()).isEmpty();
    }

    @Test(expectedExceptions = { TemplateModelException.class })
    public void testDependencyException() throws Exception {
        ClassModel model = new ClassModel(Boolean.TYPE);
        analyzer.fill(model);
        model.getType().getAddDependencies().exec(new ArrayList<String>());
    }

    @Test(expectedExceptions = { TemplateModelException.class })
    public void testIncludeException() throws Exception {
        ClassModel model = new ClassModel(Boolean.TYPE);
        analyzer.fill(model);
        model.getType().getAddIncludes().exec(new ArrayList<String>());
    }

    @Test
    public void testInnerClass() {
        boolean[][] array = new boolean[1][1];
        ClassModel model = new ClassModel(array.getClass());
        analyzer.fill(model);
        assertThat(model.getType().getInnerType().getType().getJavaName()).isEqualTo("[Z");
        assertThat(model.getType().getFinalInnerType().getType().getJavaName()).isEqualTo("boolean");
    }
}
