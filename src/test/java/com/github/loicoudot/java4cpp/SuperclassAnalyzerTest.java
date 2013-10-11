package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;

class NoAnnotationNoMappingsSuperclass {
}

@Java4Cpp(superclass = false)
class AnnotFalseSuperclass {
}

@Java4Cpp(superclass = true)
class AnnotTrueSuperclass {
}

class MappingsFalseSuperclass {
}

class MappingsTrueSuperclass {
}

public class SuperclassAnalyzerTest {

    private Context context;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalseSuperclass.class);
        clazzFalse.setSuperclass(false);
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrueSuperclass.class);
        clazzTrue.setSuperclass(true);
        local.getClasses().add(clazzTrue);

        context = new Context(new Settings());
        context.getMappingsManager().addMappings(local);
        context.start();
    }

    @Test
    public void testExportSuperClass() {
        SuperclassAnalyzer analyzer = new SuperclassAnalyzer(context);
        assertThat(analyzer.exportSuperClass(NoAnnotationNoMappingsSuperclass.class)).isFalse();
        assertThat(analyzer.exportSuperClass(AnnotFalseSuperclass.class)).isFalse();
        assertThat(analyzer.exportSuperClass(AnnotTrueSuperclass.class)).isTrue();
        assertThat(analyzer.exportSuperClass(MappingsFalseSuperclass.class)).isFalse();
        assertThat(analyzer.exportSuperClass(MappingsTrueSuperclass.class)).isTrue();
    }
}