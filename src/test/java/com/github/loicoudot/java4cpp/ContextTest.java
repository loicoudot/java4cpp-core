package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        ClassModel model = context.getClassModel(ContextTest.class);

        String templateTest = "${type.functions.test('a', 'b')}";
        Template template = context.getTemplateManager().createTemplate(templateTest);
        StringWriter sw = new StringWriter();
        template.process(model, sw);

        assertThat(sw.toString()).isEqualTo("b + a + typeContextTest");
    }

    public List<Double> listType;
    public Map<String, Double> mapType;
    public List<Set<Double>> listOfSetType;

    public <E> void genericList(List<E> list) {
    }

    public <E extends ContextTest> void genericListExtends(List<E> list) {
    }

    public void genericListWildcard(List<?> list) {
    }

    public void genericListWildcardExtends(List<? extends ContextTest> list) {
    }

    @Test
    public void testList() throws Exception {
        Type type = ContextTest.class.getField("listType").getGenericType();
        ClassModel model = context.getClassModel(type);
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Double");
    }

    @Test
    public void testMap() throws Exception {
        Type type = ContextTest.class.getField("mapType").getGenericType();
        ClassModel model = context.getClassModel(type);
        assertThat(model.getParameters()).hasSize(2);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.String");
        assertThat(model.getParameters().get(1).getType().getJavaName()).isEqualTo("java.lang.Double");
    }

    @Test
    public void testListOfSet() throws Exception {
        Type type = ContextTest.class.getField("listOfSetType").getGenericType();
        ClassModel model = context.getClassModel(type);
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.util.Set");
        assertThat(model.getParameters().get(0).getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Double");
    }

    @Test
    public void testGenericList() throws Exception {
        Type type = ContextTest.class.getMethod("genericList", List.class).getGenericParameterTypes()[0];
        ClassModel model = context.getClassModel(type);
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Object");
    }

    @Test
    public void testGenericListExtends() throws Exception {
        Type type = ContextTest.class.getMethod("genericListExtends", List.class).getGenericParameterTypes()[0];
        ClassModel model = context.getClassModel(type);
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("com.github.loicoudot.java4cpp.ContextTest");
    }

    @Test
    public void testGenericListWildcard() throws Exception {
        Type type = ContextTest.class.getMethod("genericListWildcard", List.class).getGenericParameterTypes()[0];
        ClassModel model = context.getClassModel(type);
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("java.lang.Object");
    }

    @Test
    public void testGenericListWildcardExtends() throws Exception {
        Type type = ContextTest.class.getMethod("genericListWildcardExtends", List.class).getGenericParameterTypes()[0];
        ClassModel model = context.getClassModel(type);
        assertThat(model.getParameters()).hasSize(1);
        assertThat(model.getParameters().get(0).getType().getJavaName()).isEqualTo("com.github.loicoudot.java4cpp.ContextTest");
    }
}
