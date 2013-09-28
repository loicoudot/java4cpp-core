package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.Clazz;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Namespace;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

interface Empty1 {
}

interface Empty2 {
}

class NoAnnotationNoMappings implements Empty1, Empty2 {
    public static int staticField1;
    @Java4CppWrappe
    public static int staticField2;

    public NoAnnotationNoMappings() {
    }

    @Java4CppNoWrappe
    public NoAnnotationNoMappings(String a) {
    }

    @Java4CppWrappe
    public void method1() {
    }

    @Java4CppNoWrappe
    public void method2(String a) {
    }

    class Inner1 {
    }

    @Java4CppNoWrappe
    class Inner2 {
    }
}

@Java4Cpp(all = false, staticFields = false, superclass = false, interfaces = false, wrappeInterfaces = { Empty2.class })
class AnnotFalse implements Empty1, Empty2 {
    public static int staticField1;
    @Java4CppWrappe("staticCpp")
    public static int staticField2;

    public AnnotFalse() {
    }

    @Java4CppWrappe
    public AnnotFalse(String a) {
    }

    @Java4CppWrappe("methodCpp")
    public void method1() {
    }

    public void method2(String a) {
    }

    @Java4CppWrappe
    class Inner1 {
    }

    class Inner2 {
    }
}

@Java4Cpp(all = true, staticFields = true, superclass = true, interfaces = true, noWrappeInterfaces = { Empty2.class }, name = "AnnotTrueCpp")
class AnnotTrue implements Empty1, Empty2 {
    @Java4CppWrappe
    public static int staticField1;
    @Java4CppNoWrappe
    public static int staticField2;

    public AnnotTrue() {
    }

    @Java4CppNoWrappe
    public AnnotTrue(String a) {
    }

    @Java4CppNoWrappe
    public void method1() {
    }

    @Java4CppWrappe("methodCpp")
    public void method2(String a) {
    }

    @Java4CppNoWrappe
    class Inner1 {
    }

    class Inner2 {
        @Java4Cpp(name = "InerIner")
        class Inner21 {

        }
    }
}

class MappingsFalse implements Empty1, Empty2 {
    public static int staticField1;
    public static int staticField2;

    public MappingsFalse() {
    }

    public MappingsFalse(String a) {
    }

    public void method1() {
    }

    public void method2(String a) {
    }

    class Inner1 {
    }

    class Inner2 {
    }
}

class MappingsTrue implements Empty1, Empty2 {
    public static int staticField1;
    public static int staticField2;

    public MappingsTrue() {
    }

    public MappingsTrue(String a) {
    }

    public void method1() {
    }

    public void method2(String a) {
    }

    class Inner1 {
        class Inner11 {
        }
    }

    class Inner2 {
    }
}

public class MappingsHelperTest {

    private MappingsHelper noAnnotationNoMappings;
    private MappingsHelper annotFalse;
    private MappingsHelper annotTrue;
    private MappingsHelper mappingsFalse;
    private MappingsHelper mappingsTrue;
    private Context context;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        Clazz clazzFalse = new Clazz(MappingsFalse.class);
        clazzFalse.setSuperclass(false);
        clazzFalse.setInterfaceAll(false);
        clazzFalse.setExportFields(false);
        clazzFalse.setExportAll(false);
        clazzFalse.getInterfaces().getWrappes().add(new Wrappe("com.github.loicoudot.java4cpp.Empty1"));
        clazzFalse.getStaticFields().getWrappes().add(new Wrappe("staticField1", "staticCpp"));
        clazzFalse.getInnerClasses().getWrappes().add(new Wrappe("Inner1"));
        clazzFalse.getConstructors().getWrappes().add(new Wrappe(""));
        clazzFalse.getMethods().getWrappes().add(new Wrappe("method1()", "methodCpp"));
        local.getClasses().add(clazzFalse);

        Clazz clazzTrue = new Clazz(MappingsTrue.class);
        clazzTrue.setSuperclass(true);
        clazzTrue.setInterfaceAll(true);
        clazzTrue.setExportFields(true);
        clazzTrue.setExportAll(true);
        clazzTrue.getInterfaces().getNoWrappes().add("com.github.loicoudot.java4cpp.Empty2");
        clazzTrue.getStaticFields().getNoWrappes().add("staticField2");
        clazzTrue.getStaticFields().getWrappes().add(new Wrappe("staticField1"));
        clazzTrue.getInnerClasses().getNoWrappes().add("Inner2");
        clazzTrue.getInnerClasses().getWrappes().add(new Wrappe("Inner1"));
        clazzTrue.getConstructors().getNoWrappes().add("Ljava/lang/String;");
        clazzTrue.getMethods().getNoWrappes().add("method2(Ljava/lang/String;)");
        clazzTrue.getMethods().getWrappes().add(new Wrappe("method1()"));
        clazzTrue.setCppName("MappingsTrueCpp");
        local.getClasses().add(clazzTrue);
        Clazz clazzInner = new Clazz(MappingsTrue.Inner1.class);
        clazzInner.setCppName("InnerInner");
        local.getClasses().add(clazzInner);

        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.NoAnnotationNoMappings", "com::github::loicoudot::java4cpp"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.*", "cglj"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.AnnotTrue", "cglj::bigjava"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.MappingsTrue", "cglj::bigjava"));

        context = new Context(new Settings());
        context.addMappings(local);
        context.start();
        noAnnotationNoMappings = context.getMappings(NoAnnotationNoMappings.class);
        annotFalse = context.getMappings(AnnotFalse.class);
        annotTrue = context.getMappings(AnnotTrue.class);
        mappingsFalse = context.getMappings(MappingsFalse.class);
        mappingsTrue = context.getMappings(MappingsTrue.class);
    }

    @Test
    public void testExportSuperClass() {
        assertThat(noAnnotationNoMappings.exportSuperClass()).isFalse();
        assertThat(annotFalse.exportSuperClass()).isFalse();
        assertThat(annotTrue.exportSuperClass()).isTrue();
        assertThat(mappingsFalse.exportSuperClass()).isFalse();
        assertThat(mappingsTrue.exportSuperClass()).isTrue();
    }

    @Test
    public void testIsInterfaceWrapped() {
        assertThat(noAnnotationNoMappings.isInterfaceWrapped(Empty1.class)).isFalse();
        assertThat(noAnnotationNoMappings.isInterfaceWrapped(Empty2.class)).isFalse();
        assertThat(annotFalse.isInterfaceWrapped(Empty1.class)).isFalse();
        assertThat(annotFalse.isInterfaceWrapped(Empty2.class)).isTrue();
        assertThat(annotTrue.isInterfaceWrapped(Empty1.class)).isTrue();
        assertThat(annotTrue.isInterfaceWrapped(Empty2.class)).isFalse();
        assertThat(mappingsFalse.isInterfaceWrapped(Empty1.class)).isTrue();
        assertThat(mappingsFalse.isInterfaceWrapped(Empty2.class)).isFalse();
        assertThat(mappingsTrue.isInterfaceWrapped(Empty1.class)).isTrue();
        assertThat(mappingsTrue.isInterfaceWrapped(Empty2.class)).isFalse();
    }

    @Test
    public void testIsInnerClassWrapped() {
        assertThat(noAnnotationNoMappings.isInnerClassWrapped(NoAnnotationNoMappings.Inner1.class)).isTrue();
        assertThat(noAnnotationNoMappings.isInnerClassWrapped(NoAnnotationNoMappings.Inner2.class)).isFalse();
        assertThat(annotFalse.isInnerClassWrapped(AnnotFalse.Inner1.class)).isTrue();
        assertThat(annotFalse.isInnerClassWrapped(AnnotFalse.Inner2.class)).isFalse();
        assertThat(annotTrue.isInnerClassWrapped(AnnotTrue.Inner1.class)).isFalse();
        assertThat(annotTrue.isInnerClassWrapped(AnnotTrue.Inner2.class)).isTrue();
        assertThat(mappingsFalse.isInnerClassWrapped(MappingsFalse.Inner1.class)).isTrue();
        assertThat(mappingsFalse.isInnerClassWrapped(MappingsFalse.Inner2.class)).isFalse();
        assertThat(mappingsTrue.isInnerClassWrapped(MappingsTrue.Inner1.class)).isTrue();
        assertThat(mappingsTrue.isInnerClassWrapped(MappingsTrue.Inner2.class)).isFalse();
    }

    @Test
    public void testIsFieldWrapped() throws Exception {
        assertThat(noAnnotationNoMappings.isFieldWrapped(NoAnnotationNoMappings.class.getDeclaredField("staticField1"))).isFalse();
        assertThat(noAnnotationNoMappings.isFieldWrapped(NoAnnotationNoMappings.class.getDeclaredField("staticField2"))).isTrue();
        assertThat(annotFalse.isFieldWrapped(AnnotFalse.class.getDeclaredField("staticField1"))).isFalse();
        assertThat(annotFalse.isFieldWrapped(AnnotFalse.class.getDeclaredField("staticField2"))).isTrue();
        assertThat(annotTrue.isFieldWrapped(AnnotTrue.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(annotTrue.isFieldWrapped(AnnotTrue.class.getDeclaredField("staticField2"))).isFalse();
        assertThat(mappingsFalse.isFieldWrapped(MappingsFalse.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(mappingsFalse.isFieldWrapped(MappingsFalse.class.getDeclaredField("staticField2"))).isFalse();
        assertThat(mappingsTrue.isFieldWrapped(MappingsTrue.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(mappingsTrue.isFieldWrapped(MappingsTrue.class.getDeclaredField("staticField2"))).isFalse();
    }

    @Test
    public void testIsConstructorWrapped() throws Exception {
        assertThat(noAnnotationNoMappings.isConstructorWrapped(NoAnnotationNoMappings.class.getConstructor())).isTrue();
        assertThat(noAnnotationNoMappings.isConstructorWrapped(NoAnnotationNoMappings.class.getConstructor(String.class))).isFalse();
        assertThat(annotFalse.isConstructorWrapped(AnnotFalse.class.getConstructor())).isFalse();
        assertThat(annotFalse.isConstructorWrapped(AnnotFalse.class.getConstructor(String.class))).isTrue();
        assertThat(annotTrue.isConstructorWrapped(AnnotTrue.class.getConstructor())).isTrue();
        assertThat(annotTrue.isConstructorWrapped(AnnotTrue.class.getConstructor(String.class))).isFalse();
        assertThat(mappingsFalse.isConstructorWrapped(MappingsFalse.class.getConstructor())).isTrue();
        assertThat(mappingsFalse.isConstructorWrapped(MappingsFalse.class.getConstructor(String.class))).isFalse();
        assertThat(mappingsTrue.isConstructorWrapped(MappingsTrue.class.getConstructor())).isTrue();
        assertThat(mappingsTrue.isConstructorWrapped(MappingsTrue.class.getConstructor(String.class))).isFalse();
    }

    @Test
    public void testIsMethodWrapped() throws Exception {
        assertThat(noAnnotationNoMappings.isMethodWrapped(NoAnnotationNoMappings.class.getMethod("method1"))).isTrue();
        assertThat(noAnnotationNoMappings.isMethodWrapped(NoAnnotationNoMappings.class.getMethod("method2", String.class))).isFalse();
        assertThat(annotFalse.isMethodWrapped(AnnotFalse.class.getMethod("method1"))).isTrue();
        assertThat(annotFalse.isMethodWrapped(AnnotFalse.class.getMethod("method2", String.class))).isFalse();
        assertThat(annotTrue.isMethodWrapped(AnnotTrue.class.getMethod("method1"))).isFalse();
        assertThat(annotTrue.isMethodWrapped(AnnotTrue.class.getMethod("method2", String.class))).isTrue();
        assertThat(mappingsFalse.isMethodWrapped(MappingsFalse.class.getMethod("method1"))).isTrue();
        assertThat(mappingsFalse.isMethodWrapped(MappingsFalse.class.getMethod("method2", String.class))).isFalse();
        assertThat(mappingsTrue.isMethodWrapped(MappingsTrue.class.getMethod("method1"))).isTrue();
        assertThat(mappingsTrue.isMethodWrapped(MappingsTrue.class.getMethod("method2", String.class))).isFalse();
    }

    @Test
    public void testGetCppName() throws Exception {
        assertThat(noAnnotationNoMappings.getCppName()).isEqualTo("NoAnnotationNoMappings");
        assertThat(annotFalse.getCppName()).isEqualTo("AnnotFalse");
        assertThat(annotTrue.getCppName()).isEqualTo("AnnotTrueCpp");
        assertThat(mappingsFalse.getCppName()).isEqualTo("MappingsFalse");
        assertThat(mappingsTrue.getCppName()).isEqualTo("MappingsTrueCpp");
    }

    @Test
    public void testGetCppNameField() throws Exception {
        assertThat(annotFalse.getCppName(AnnotFalse.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(annotFalse.getCppName(AnnotFalse.class.getDeclaredField("staticField2"))).isEqualTo("staticCpp");
        assertThat(annotTrue.getCppName(AnnotTrue.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(annotTrue.getCppName(AnnotTrue.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
        assertThat(mappingsFalse.getCppName(MappingsFalse.class.getDeclaredField("staticField1"))).isEqualTo("staticCpp");
        assertThat(mappingsFalse.getCppName(MappingsFalse.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
        assertThat(mappingsTrue.getCppName(MappingsTrue.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(mappingsTrue.getCppName(MappingsTrue.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
    }

    @Test
    public void testGetCppNameMethod() throws Exception {
        assertThat(annotFalse.getCppName(AnnotFalse.class.getMethod("method1"))).isEqualTo("methodCpp");
        assertThat(annotFalse.getCppName(AnnotFalse.class.getMethod("method2", String.class))).isEqualTo("method2");
        assertThat(annotTrue.getCppName(AnnotTrue.class.getMethod("method1"))).isEqualTo("method1");
        assertThat(annotTrue.getCppName(AnnotTrue.class.getMethod("method2", String.class))).isEqualTo("methodCpp");
        assertThat(mappingsFalse.getCppName(MappingsFalse.class.getMethod("method1"))).isEqualTo("methodCpp");
        assertThat(mappingsFalse.getCppName(MappingsFalse.class.getMethod("method2", String.class))).isEqualTo("method2");
        assertThat(mappingsTrue.getCppName(MappingsTrue.class.getMethod("method1"))).isEqualTo("method1");
        assertThat(mappingsTrue.getCppName(MappingsTrue.class.getMethod("method2", String.class))).isEqualTo("method2");
    }

    @Test
    public void testGetNamespaces() throws Exception {
        assertThat(noAnnotationNoMappings.getNamespaces()).containsOnly("com", "github", "loicoudot", "java4cpp", "NoAnnotationNoMappings");
        assertThat(annotFalse.getNamespaces()).containsOnly("cglj", "AnnotFalse");
        assertThat(context.getMappings(AnnotFalse.Inner1.class).getNamespaces()).containsOnly("cglj", "AnnotFalse", "Inner1");
        assertThat(annotTrue.getNamespaces()).containsOnly("cglj", "bigjava", "AnnotTrueCpp");
        assertThat(context.getMappings(AnnotTrue.Inner2.class).getNamespaces()).containsOnly("cglj", "bigjava", "AnnotTrueCpp", "Inner2");
        assertThat(context.getMappings(AnnotTrue.Inner2.Inner21.class).getNamespaces()).containsOnly("cglj", "bigjava", "AnnotTrueCpp", "Inner2", "InerIner");
        assertThat(mappingsFalse.getNamespaces()).containsOnly("cglj", "MappingsFalse");
        assertThat(context.getMappings(MappingsFalse.Inner1.class).getNamespaces()).containsOnly("cglj", "MappingsFalse", "Inner1");
        assertThat(mappingsTrue.getNamespaces()).containsOnly("cglj", "bigjava", "MappingsTrueCpp");
        assertThat(context.getMappings(MappingsTrue.Inner1.class).getNamespaces()).containsOnly("cglj", "bigjava", "MappingsTrueCpp", "InnerInner");
        assertThat(context.getMappings(MappingsTrue.Inner1.Inner11.class).getNamespaces()).containsOnly("cglj", "bigjava", "MappingsTrueCpp", "InnerInner",
                "Inner11");
    }
}