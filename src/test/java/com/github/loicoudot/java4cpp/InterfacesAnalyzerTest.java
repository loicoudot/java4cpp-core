package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

interface Interface1 {
}

interface Interface2 {
}

class NoAnnotationNoMappingsInterface implements Interface1, Interface2 {
}

@Java4Cpp(interfaces = false, wrappeInterfaces = { Interface2.class })
class AnnotFalseInterface implements Interface1, Interface2 {
}

@Java4Cpp(interfaces = true, noWrappeInterfaces = { Interface2.class })
class AnnotTrueInterface implements Interface1, Interface2 {
}

class MappingsFalseInterface implements Interface1, Interface2 {
}

class MappingsTrueInterface implements Interface1, Interface2 {
}

public class InterfacesAnalyzerTest {

    private Context context;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalseInterface.class);
        clazzFalse.setInterfaceAll(false);
        clazzFalse.getInterfaces().getWrappes().add(new Wrappe("com.github.loicoudot.java4cpp.Interface1"));
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrueInterface.class);
        clazzTrue.setInterfaceAll(true);
        clazzTrue.getInterfaces().getNoWrappes().add("com.github.loicoudot.java4cpp.Interface2");
        local.getClasses().add(clazzTrue);

        context = new Context(new Settings());
        context.getMappingsManager().addMappings(local);
        context.start();
    }

    @Test
    public void testIsInterfaceWrapped() {
        InterfacesAnalyzer analyzer = new InterfacesAnalyzer(context);
        assertThat(analyzer.isInterfaceWrapped(NoAnnotationNoMappingsInterface.class, Interface1.class)).isFalse();
        assertThat(analyzer.isInterfaceWrapped(NoAnnotationNoMappingsInterface.class, Interface2.class)).isFalse();
        assertThat(analyzer.isInterfaceWrapped(AnnotFalseInterface.class, Interface1.class)).isFalse();
        assertThat(analyzer.isInterfaceWrapped(AnnotFalseInterface.class, Interface2.class)).isTrue();
        assertThat(analyzer.isInterfaceWrapped(AnnotTrueInterface.class, Interface1.class)).isTrue();
        assertThat(analyzer.isInterfaceWrapped(AnnotTrueInterface.class, Interface2.class)).isFalse();
        assertThat(analyzer.isInterfaceWrapped(MappingsFalseInterface.class, Interface1.class)).isTrue();
        assertThat(analyzer.isInterfaceWrapped(MappingsFalseInterface.class, Interface2.class)).isFalse();
        assertThat(analyzer.isInterfaceWrapped(MappingsTrueInterface.class, Interface1.class)).isTrue();
        assertThat(analyzer.isInterfaceWrapped(MappingsTrueInterface.class, Interface2.class)).isFalse();
    }
}