package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

class NoAnnotationNoMappingsInner {
    class Inner1 {
    }

    class Inner2 {
    }
}

@Java4Cpp(all = false)
class AnnotFalseInner {
    @Java4CppWrappe
    class Inner1 {
    }

    class Inner2 {
    }
}

@Java4Cpp(all = true)
class AnnotTrueInner {
    @Java4CppNoWrappe
    class Inner1 {
    }

    class Inner2 {
        class Inner21 {
        }
    }
}

class MappingsFalseInner {
    class Inner1 {
    }

    class Inner2 {
    }
}

class MappingsTrueInner {
    class Inner1 {
        class Inner11 {
        }
    }

    class Inner2 {
    }
}

public class InnerClassAnalyzerTest {

    private Context context;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalseInner.class);
        clazzFalse.setExportAll(false);
        clazzFalse.getInnerClasses().getWrappes().add(new Wrappe("Inner1"));
        local.getClasses().add(clazzFalse);

        ClassMapping clazzTrue = new ClassMapping(MappingsTrueInner.class);
        clazzTrue.setExportAll(true);
        clazzTrue.getInnerClasses().getNoWrappes().add("Inner2");
        local.getClasses().add(clazzTrue);

        context = new Context(new Settings());
        context.getMappingsManager().addMappings(local);
        context.start();
    }

    @Test
    public void testIsInnerClassWrapped() {
        InnerClassAnalyzer analyzer = new InnerClassAnalyzer(context);
        assertThat(analyzer.isInnerClassWrapped(NoAnnotationNoMappingsInner.class, NoAnnotationNoMappingsInner.Inner1.class)).isTrue();
        assertThat(analyzer.isInnerClassWrapped(NoAnnotationNoMappingsInner.class, NoAnnotationNoMappingsInner.Inner2.class)).isTrue();
        assertThat(analyzer.isInnerClassWrapped(AnnotFalseInner.class, AnnotFalseInner.Inner1.class)).isTrue();
        assertThat(analyzer.isInnerClassWrapped(AnnotFalseInner.class, AnnotFalseInner.Inner2.class)).isFalse();
        assertThat(analyzer.isInnerClassWrapped(AnnotTrueInner.class, AnnotTrueInner.Inner1.class)).isFalse();
        assertThat(analyzer.isInnerClassWrapped(AnnotTrueInner.class, AnnotTrueInner.Inner2.class)).isTrue();
        assertThat(analyzer.isInnerClassWrapped(MappingsFalseInner.class, MappingsFalseInner.Inner1.class)).isTrue();
        assertThat(analyzer.isInnerClassWrapped(MappingsFalseInner.class, MappingsFalseInner.Inner2.class)).isFalse();
        assertThat(analyzer.isInnerClassWrapped(MappingsTrueInner.class, MappingsTrueInner.Inner1.class)).isTrue();
        assertThat(analyzer.isInnerClassWrapped(MappingsTrueInner.class, MappingsTrueInner.Inner2.class)).isFalse();
    }
}