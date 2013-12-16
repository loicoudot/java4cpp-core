package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;

import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Datatypes;
import com.github.loicoudot.java4cpp.configuration.Function;
import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;
import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ContextTest {

    @Test
    public void functionsTest() throws TemplateException, IOException {
        Settings settings = new Settings();
        settings.setTargetPath("target");
        Context context = new Context(settings);

        Templates other = new Templates();
        Datatypes datatypes = new Datatypes();
        TypeTemplate fallback = new TypeTemplate();
        fallback.setNeedAnalyzing(true);
        fallback.setCppType("type${class.type.cppShortName}");
        Function function = new Function("test", "${arg2} + ${arg1} + ${class.type.cppType}");
        fallback.getFunctions().add(function);
        datatypes.setFallback(fallback);
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
}
