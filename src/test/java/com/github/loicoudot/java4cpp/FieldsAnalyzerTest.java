package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

class NoAnnotationNoMappingsFields {
    public static int staticField1;
    public static int staticField2;
}

@Java4Cpp(staticFields = false)
class AnnotFalseFields {
    public static int staticField1;
    @Java4CppWrappe("staticCpp")
    public static int staticField2;
}

@Java4Cpp(staticFields = true)
class AnnotTrueFields {
    public static int staticField1;
    @Java4CppNoWrappe
    public static int staticField2;
}

class MappingsFalseFields {
    public static int staticField1;
    public static int staticField2;
}

class MappingsTrueFields {
    public static int staticField1;
    public static int staticField2;
}

public class FieldsAnalyzerTest {

    private Context context;
    private FieldsAnalyzer analyzer;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalseFields.class);
        clazzFalse.setExportFields(false);
        clazzFalse.getStaticFields().getWrappes().add(new Wrappe("staticField1", "staticCpp"));
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrueFields.class);
        clazzTrue.setExportFields(true);
        clazzTrue.getStaticFields().getNoWrappes().add("staticField2");
        local.getClasses().add(clazzTrue);

        context = new Context(new Settings());
        context.getMappingsManager().addMappings(local);
        context.start();
        analyzer = new FieldsAnalyzer(context);
    }

    @Test
    public void testIsFieldWrapped() throws Exception {
        assertThat(analyzer.isFieldWrapped(NoAnnotationNoMappingsFields.class.getDeclaredField("staticField1"))).isFalse();
        assertThat(analyzer.isFieldWrapped(NoAnnotationNoMappingsFields.class.getDeclaredField("staticField2"))).isFalse();
        assertThat(analyzer.isFieldWrapped(AnnotFalseFields.class.getDeclaredField("staticField1"))).isFalse();
        assertThat(analyzer.isFieldWrapped(AnnotFalseFields.class.getDeclaredField("staticField2"))).isTrue();
        assertThat(analyzer.isFieldWrapped(AnnotTrueFields.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(analyzer.isFieldWrapped(AnnotTrueFields.class.getDeclaredField("staticField2"))).isFalse();
        assertThat(analyzer.isFieldWrapped(MappingsFalseFields.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(analyzer.isFieldWrapped(MappingsFalseFields.class.getDeclaredField("staticField2"))).isFalse();
        assertThat(analyzer.isFieldWrapped(MappingsTrueFields.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(analyzer.isFieldWrapped(MappingsTrueFields.class.getDeclaredField("staticField2"))).isFalse();
    }

    @Test
    public void testGetCppName() throws Exception {
        assertThat(analyzer.getCppName(AnnotFalseFields.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(analyzer.getCppName(AnnotFalseFields.class.getDeclaredField("staticField2"))).isEqualTo("staticCpp");
        assertThat(analyzer.getCppName(AnnotTrueFields.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(analyzer.getCppName(AnnotTrueFields.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
        assertThat(analyzer.getCppName(MappingsFalseFields.class.getDeclaredField("staticField1"))).isEqualTo("staticCpp");
        assertThat(analyzer.getCppName(MappingsFalseFields.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
        assertThat(analyzer.getCppName(MappingsTrueFields.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(analyzer.getCppName(MappingsTrueFields.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
    }
}