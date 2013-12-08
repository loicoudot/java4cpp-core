package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;
import com.github.loicoudot.java4cpp.configuration.Wrappe;
import com.github.loicoudot.java4cpp.model.ClassModel;
import com.github.loicoudot.java4cpp.model.ConstructorModel;

class NoAnnotationNoMappingsConstructors {
    public NoAnnotationNoMappingsConstructors() {
    }

    public NoAnnotationNoMappingsConstructors(String a) {
    }
}

@Java4Cpp(all = false)
class AnnotFalseConstructors {
    public AnnotFalseConstructors() {
    }

    @Java4CppWrappe
    public AnnotFalseConstructors(String a) {
    }
}

@Java4Cpp(all = true)
class AnnotTrueConstructors {
    public AnnotTrueConstructors() {
    }

    @Java4CppNoWrappe
    public AnnotTrueConstructors(String a) {
    }

    public AnnotTrueConstructors(List<String> list, Map<Iterable<Double>, String> map) {
    }
}

class MappingsFalseConstructors {
    public MappingsFalseConstructors() {
    }

    public MappingsFalseConstructors(String a) {
    }
}

class MappingsTrueConstructors {
    public MappingsTrueConstructors() {
    }

    public MappingsTrueConstructors(String a) {
    }
}

public class ConstructorsAnalyzerTest {

    private Context context;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalseConstructors.class);
        clazzFalse.setExportAll(false);
        clazzFalse.getConstructors().getWrappes().add(new Wrappe(""));
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrueConstructors.class);
        clazzTrue.setExportAll(true);
        clazzTrue.getConstructors().getNoWrappes().add("Ljava/lang/String;");
        local.getClasses().add(clazzTrue);

        Templates other = new Templates();
        TypeTemplate classTemplate = new TypeTemplate();
        classTemplate.setNeedAnalyzing(false);
        classTemplate.setCppType("cppType");
        classTemplate.setCppReturnType("cppReturnType");
        other.getDatatypes().setFallback(classTemplate);

        Settings settings = new Settings();
        settings.setTargetPath("target");
        context = new Context(settings);
        context.getMappingsManager().addMappings(local);
        context.getTemplateManager().addTemplates(other);
        context.start();
    }

    @Test
    public void testIsConstructorWrapped() throws Exception {
        ConstructorsAnalyzer analyzer = new ConstructorsAnalyzer(context);
        assertThat(analyzer.isConstructorWrapped(NoAnnotationNoMappingsConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(NoAnnotationNoMappingsConstructors.class.getConstructor(String.class))).isTrue();
        assertThat(analyzer.isConstructorWrapped(AnnotFalseConstructors.class.getConstructor())).isFalse();
        assertThat(analyzer.isConstructorWrapped(AnnotFalseConstructors.class.getConstructor(String.class))).isTrue();
        assertThat(analyzer.isConstructorWrapped(AnnotTrueConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(AnnotTrueConstructors.class.getConstructor(String.class))).isFalse();
        assertThat(analyzer.isConstructorWrapped(MappingsFalseConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(MappingsFalseConstructors.class.getConstructor(String.class))).isFalse();
        assertThat(analyzer.isConstructorWrapped(MappingsTrueConstructors.class.getConstructor())).isTrue();
        assertThat(analyzer.isConstructorWrapped(MappingsTrueConstructors.class.getConstructor(String.class))).isFalse();
    }

    @Test
    public void testGetModel() throws Exception {
        ConstructorsAnalyzer analyzer = new ConstructorsAnalyzer(context);
        ConstructorModel model = analyzer.getModel(AnnotTrueConstructors.class.getConstructor(List.class, Map.class));
        assertThat(model.getParameters()).hasSize(2);
        ClassModel first = model.getParameters().get(0);
        assertThat(first.getClazz().toString()).isEqualTo(List.class.toString());
        assertThat(first.isIsParameterized()).isTrue();
        assertThat(first.getParameterized()).hasSize(1);
        assertThat(first.getParameterized().get(0).getClazz().toString()).isEqualTo(String.class.toString());
        ClassModel second = model.getParameters().get(1);
        assertThat(second.getClazz().toString()).isEqualTo(Map.class.toString());
        assertThat(second.isIsParameterized()).isTrue();
        assertThat(second.getParameterized()).hasSize(2);
        assertThat(second.getParameterized().get(0).getClazz().toString()).isEqualTo(Iterable.class.toString());
        assertThat(second.getParameterized().get(0).isIsParameterized()).isTrue();
        assertThat(second.getParameterized().get(0).getParameterized()).hasSize(1);
        assertThat(second.getParameterized().get(0).getParameterized().get(0).getClazz().toString()).isEqualTo(Double.class.toString());
        assertThat(second.getParameterized().get(1).getClazz().toString()).isEqualTo(String.class.toString());
        assertThat(second.getParameterized().get(1).isIsParameterized()).isFalse();
    }
}