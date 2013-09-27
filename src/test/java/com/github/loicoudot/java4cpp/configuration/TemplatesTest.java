package com.github.loicoudot.java4cpp.configuration;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Datatypes;
import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;

class TemplatesOutputResolver extends SchemaOutputResolver {

    @Override
    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
        File file = new File("templates.xsd");
        StreamResult result = new StreamResult(file);
        result.setSystemId(file.toURI().toURL().toString());
        return result;
    }
}

public class TemplatesTest {

    @Test
    public void xsd() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Templates.class);
        SchemaOutputResolver sor = new TemplatesOutputResolver();
        jaxbContext.generateSchema(sor);
    }

    @Test
    public void deserialize() {
        Templates templates = JAXB.unmarshal(new File("target/test-classes/templates.xml"), Templates.class);
        assertThat(templates.getSourceTemplates()).containsOnly("tpl1", "tpl2");
        assertThat(templates.getGlobalTemplates()).containsOnly("gbl1", "gbl2");
        assertThat(templates.getCopyFiles()).containsOnly("file1", "file2");
        final Datatypes datatypes = templates.getDatatypes();
        assertThat(datatypes.getFallback().getCppType()).isEqualTo("fallback");
        final TypeTemplate template = datatypes.getTemplates().get(0);
        assertThat(template.getClazz()).hasSameClassAs(Boolean.TYPE);
        assertThat(template.getCppType()).isEqualTo("cppType");
        assertThat(template.getCppReturnType()).isEqualTo("cppReturnType");
        assertThat(template.getJava2cpp()).isEqualTo("java2cpp");
        assertThat(template.getCpp2java()).isEqualTo("cpp2java");
        assertThat(template.getCpp2javaClean()).isEqualTo("cpp2javaClean");
        assertThat(template.getDependencies()).isEqualTo("dependencies");
    }
}
