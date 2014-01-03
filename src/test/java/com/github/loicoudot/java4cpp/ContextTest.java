package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Datatypes;
import com.github.loicoudot.java4cpp.configuration.Function;
import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;
import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ContextTest {

    private Context context;

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
    }

    @Test
    public void functionsTest() throws TemplateException, IOException {
        Templates other = new Templates();
        Datatypes datatypes = new Datatypes();
        TypeTemplate fallback = new TypeTemplate();
        fallback.setNeedAnalyzing(true);
        fallback.setCppType("type${class.type.cppShortName}");
        Function function = new Function("test", "${arg2} + ${arg1} + ${class.type.cppType}");
        fallback.getFunctions().add(function);
        datatypes.setFallback(fallback);
        datatypes.setEnumeration(fallback);
        other.setDatatypes(datatypes);
        context.getTemplateManager().addTemplates(other);

        context.start();
        Java4CppType type = Java4CppType.fromType(ContextTest.class);
        ClassModel model = context.analyzeClassModel(type);
        model = context.executeTypeTemplate(type);

        String templateTest = "${type.functions.test('a', 'b')}";
        Template template = context.getTemplateManager().createTemplate(templateTest);
        StringWriter sw = new StringWriter();
        template.process(model, sw);

        assertThat(sw.toString()).isEqualTo("b + a + typeContextTest");
    }

    @Test
    public void testList() throws Exception {
        ClassModel model = context.getClassModel(GenericProviderTest.getListOfDoubleType());
        assertThat(model.getType().getJavaName()).isEqualTo("java.util.List");
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Double");
    }

    @Test
    public void testMap() throws Exception {
        ClassModel model = context.getClassModel(GenericProviderTest.getMapOfStringDoubleType());
        assertThat(model.getType().getJavaName()).isEqualTo("java.util.Map");
        assertThat(model.getParameters()).hasSize(2);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.String");
        assertThat(model.getParameters().get(1).getType().getJavaName()).isEqualTo("java.lang.Double");
    }

    @Test
    public void testListOfSet() throws Exception {
        ClassModel model = context.getClassModel(GenericProviderTest.getListOfSetOfDouble());
        assertThat(model.getType().getJavaName()).isEqualTo("java.util.List");
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.util.Set");
        assertThat(model.getParameters().get(0).getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Double");
    }

    @Test
    public void testGeneric() throws Exception {
        ClassModel model = context.analyzeClassModel(Java4CppType.fromType(GenericProviderTest.getGenericType()));
        assertThat(model.getType().getJavaName()).isEqualTo("java.lang.Object");
        assertThat(model.getParameters()).isNull();
    }

    @Test
    public void testGenericExtends() throws Exception {
        ClassModel model = context.analyzeClassModel(Java4CppType.fromType(GenericProviderTest.getGenericExtendsType()));
        assertThat(model.getType().getJavaName()).isEqualTo("com.github.loicoudot.java4cpp.GenericProviderTest");
        assertThat(model.getParameters()).isNull();
    }

    @Test
    public void testGenericList() throws Exception {
        ClassModel model = context.analyzeClassModel(Java4CppType.fromType(GenericProviderTest.getGenericListType()));
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Object");
    }

    @Test
    public void testGenericListExtends() throws Exception {
        ClassModel model = context.analyzeClassModel(Java4CppType.fromType(GenericProviderTest.getGenericListExtendsType()));
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("com.github.loicoudot.java4cpp.GenericProviderTest");
    }

    @Test
    public void testGenericListWildcard() throws Exception {
        ClassModel model = context.analyzeClassModel(Java4CppType.fromType(GenericProviderTest.getWildcardListType()));
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Object");
    }

    @Test
    public void testGenericListWildcardExtends() throws Exception {
        ClassModel model = context.analyzeClassModel(Java4CppType.fromType(GenericProviderTest.getWildcardListExtendsType()));
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("com.github.loicoudot.java4cpp.GenericProviderTest");
    }
}
