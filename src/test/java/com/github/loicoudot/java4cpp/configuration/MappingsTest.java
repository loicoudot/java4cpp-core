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

class MappingsOutputResolver extends SchemaOutputResolver {

    @Override
    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
        File file = new File("mappings.xsd");
        StreamResult result = new StreamResult(file);
        result.setSystemId(file.toURI().toURL().toString());
        return result;
    }

}

public class MappingsTest {

    @Test
    public void serialize() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Mappings.class);
        SchemaOutputResolver sor = new MappingsOutputResolver();
        jaxbContext.generateSchema(sor);
    }

    @Test
    public void deserialize() {
        Mappings mappings = JAXB.unmarshal(new File("target/test-classes/mappings.xml"), Mappings.class);
        assertThat(mappings.getKeywords()).containsExactly("true", "delete");
        assertThat(mappings.getNamespaces()).containsExactly(new Namespace("com.github.loicoudot", "java4cpp"), new Namespace("java.utils.*", "utils"));
        assertThat(mappings.getClasses()).hasSize(1);
        ClassMapping actual = mappings.getClasses().get(0);
        assertThat(actual.getClazz()).hasSameClassAs(Float.class);
        assertThat(actual.isExportAll()).isEqualTo(false);
        assertThat(actual.isExportFields()).isEqualTo(true);
        assertThat(actual.isInterfaceAll()).isEqualTo(true);
        assertThat(actual.isSuperclass()).isEqualTo(true);
        assertThat(actual.getCppName()).isEqualTo("cppName");
        assertThat(actual.getConstructors().getNoWrappes()).containsExactly("noWrappe1", "noWrappe2");
        assertThat(actual.getConstructors().getWrappes().toString()).isEqualTo("[Wrappe(, cppName), Wrappe(wrappe2, null)]");
        assertThat(actual.getInnerClasses().getNoWrappes()).containsExactly("noWrappe1");
        assertThat(actual.getInnerClasses().getWrappes().toString()).isEqualTo("[Wrappe(Inner1, cppInner)]");
        assertThat(actual.getInterfaces().getNoWrappes()).containsExactly("Interface1", "Interface2");
        assertThat(actual.getInterfaces().getWrappes().toString()).isEqualTo(
                "[Wrappe(com.github.loicoudot.java4cpp.Empty1, null), Wrappe(Interface2, cppInterface)]");
        assertThat(actual.getMethods().getNoWrappes()).containsExactly("method2()");
        assertThat(actual.getMethods().getWrappes().toString()).isEqualTo("[Wrappe(method1(), null)]");
        assertThat(actual.getStaticFields().getNoWrappes()).containsExactly("field1");
        assertThat(actual.getStaticFields().getWrappes().toString()).isEqualTo("[Wrappe(staticField1, fieldCpp)]");
    }
}
