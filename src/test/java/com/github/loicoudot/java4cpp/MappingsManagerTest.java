package com.github.loicoudot.java4cpp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
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

public class MappingsManagerTest {

    private Context context;
    private MappingsManager mappings;

    @BeforeClass
    public void init() {
        Mappings local = new Mappings();

        ClassMapping clazzFalse = new ClassMapping(MappingsFalse.class);
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

        ClassMapping clazzTrue = new ClassMapping(MappingsTrue.class);
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
        ClassMapping clazzInner = new ClassMapping(MappingsTrue.Inner1.class);
        clazzInner.setCppName("InnerInner");
        local.getClasses().add(clazzInner);

        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.NoAnnotationNoMappings", "com::github::loicoudot::java4cpp"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.*", "cglj"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.AnnotTrue", "cglj::bigjava"));
        local.getNamespaces().add(new Namespace("com.github.loicoudot.java4cpp.MappingsTrue", "cglj::bigjava"));

        context = new Context(new Settings());
        context.getMappingsManager().addMappings(local);
        context.start();
        mappings = context.getMappingsManager();
    }

    @Test
    public void testExportSuperClass() {
        assertThat(mappings.exportSuperClass(NoAnnotationNoMappings.class)).isFalse();
        assertThat(mappings.exportSuperClass(AnnotFalse.class)).isFalse();
        assertThat(mappings.exportSuperClass(AnnotTrue.class)).isTrue();
        assertThat(mappings.exportSuperClass(MappingsFalse.class)).isFalse();
        assertThat(mappings.exportSuperClass(MappingsTrue.class)).isTrue();
    }

    @Test
    public void testIsInterfaceWrapped() {
        assertThat(mappings.isInterfaceWrapped(NoAnnotationNoMappings.class, Empty1.class)).isFalse();
        assertThat(mappings.isInterfaceWrapped(NoAnnotationNoMappings.class, Empty2.class)).isFalse();
        assertThat(mappings.isInterfaceWrapped(AnnotFalse.class, Empty1.class)).isFalse();
        assertThat(mappings.isInterfaceWrapped(AnnotFalse.class, Empty2.class)).isTrue();
        assertThat(mappings.isInterfaceWrapped(AnnotTrue.class, Empty1.class)).isTrue();
        assertThat(mappings.isInterfaceWrapped(AnnotTrue.class, Empty2.class)).isFalse();
        assertThat(mappings.isInterfaceWrapped(MappingsFalse.class, Empty1.class)).isTrue();
        assertThat(mappings.isInterfaceWrapped(MappingsFalse.class, Empty2.class)).isFalse();
        assertThat(mappings.isInterfaceWrapped(MappingsTrue.class, Empty1.class)).isTrue();
        assertThat(mappings.isInterfaceWrapped(MappingsTrue.class, Empty2.class)).isFalse();
    }

    @Test
    public void testIsInnerClassWrapped() {
        assertThat(mappings.isInnerClassWrapped(NoAnnotationNoMappings.class, NoAnnotationNoMappings.Inner1.class)).isTrue();
        assertThat(mappings.isInnerClassWrapped(NoAnnotationNoMappings.class, NoAnnotationNoMappings.Inner2.class)).isFalse();
        assertThat(mappings.isInnerClassWrapped(AnnotFalse.class, AnnotFalse.Inner1.class)).isTrue();
        assertThat(mappings.isInnerClassWrapped(AnnotFalse.class, AnnotFalse.Inner2.class)).isFalse();
        assertThat(mappings.isInnerClassWrapped(AnnotTrue.class, AnnotTrue.Inner1.class)).isFalse();
        assertThat(mappings.isInnerClassWrapped(AnnotTrue.class, AnnotTrue.Inner2.class)).isTrue();
        assertThat(mappings.isInnerClassWrapped(MappingsFalse.class, MappingsFalse.Inner1.class)).isTrue();
        assertThat(mappings.isInnerClassWrapped(MappingsFalse.class, MappingsFalse.Inner2.class)).isFalse();
        assertThat(mappings.isInnerClassWrapped(MappingsTrue.class, MappingsTrue.Inner1.class)).isTrue();
        assertThat(mappings.isInnerClassWrapped(MappingsTrue.class, MappingsTrue.Inner2.class)).isFalse();
    }

    @Test
    public void testIsFieldWrapped() throws Exception {
        assertThat(mappings.isFieldWrapped(NoAnnotationNoMappings.class.getDeclaredField("staticField1"))).isFalse();
        assertThat(mappings.isFieldWrapped(NoAnnotationNoMappings.class.getDeclaredField("staticField2"))).isTrue();
        assertThat(mappings.isFieldWrapped(AnnotFalse.class.getDeclaredField("staticField1"))).isFalse();
        assertThat(mappings.isFieldWrapped(AnnotFalse.class.getDeclaredField("staticField2"))).isTrue();
        assertThat(mappings.isFieldWrapped(AnnotTrue.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(mappings.isFieldWrapped(AnnotTrue.class.getDeclaredField("staticField2"))).isFalse();
        assertThat(mappings.isFieldWrapped(MappingsFalse.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(mappings.isFieldWrapped(MappingsFalse.class.getDeclaredField("staticField2"))).isFalse();
        assertThat(mappings.isFieldWrapped(MappingsTrue.class.getDeclaredField("staticField1"))).isTrue();
        assertThat(mappings.isFieldWrapped(MappingsTrue.class.getDeclaredField("staticField2"))).isFalse();
    }

    @Test
    public void testIsConstructorWrapped() throws Exception {
        assertThat(mappings.isConstructorWrapped(NoAnnotationNoMappings.class.getConstructor())).isTrue();
        assertThat(mappings.isConstructorWrapped(NoAnnotationNoMappings.class.getConstructor(String.class))).isFalse();
        assertThat(mappings.isConstructorWrapped(AnnotFalse.class.getConstructor())).isFalse();
        assertThat(mappings.isConstructorWrapped(AnnotFalse.class.getConstructor(String.class))).isTrue();
        assertThat(mappings.isConstructorWrapped(AnnotTrue.class.getConstructor())).isTrue();
        assertThat(mappings.isConstructorWrapped(AnnotTrue.class.getConstructor(String.class))).isFalse();
        assertThat(mappings.isConstructorWrapped(MappingsFalse.class.getConstructor())).isTrue();
        assertThat(mappings.isConstructorWrapped(MappingsFalse.class.getConstructor(String.class))).isFalse();
        assertThat(mappings.isConstructorWrapped(MappingsTrue.class.getConstructor())).isTrue();
        assertThat(mappings.isConstructorWrapped(MappingsTrue.class.getConstructor(String.class))).isFalse();
    }

    @Test
    public void testIsMethodWrapped() throws Exception {
        assertThat(mappings.isMethodWrapped(NoAnnotationNoMappings.class.getMethod("method1"))).isTrue();
        assertThat(mappings.isMethodWrapped(NoAnnotationNoMappings.class.getMethod("method2", String.class))).isFalse();
        assertThat(mappings.isMethodWrapped(AnnotFalse.class.getMethod("method1"))).isTrue();
        assertThat(mappings.isMethodWrapped(AnnotFalse.class.getMethod("method2", String.class))).isFalse();
        assertThat(mappings.isMethodWrapped(AnnotTrue.class.getMethod("method1"))).isFalse();
        assertThat(mappings.isMethodWrapped(AnnotTrue.class.getMethod("method2", String.class))).isTrue();
        assertThat(mappings.isMethodWrapped(MappingsFalse.class.getMethod("method1"))).isTrue();
        assertThat(mappings.isMethodWrapped(MappingsFalse.class.getMethod("method2", String.class))).isFalse();
        assertThat(mappings.isMethodWrapped(MappingsTrue.class.getMethod("method1"))).isTrue();
        assertThat(mappings.isMethodWrapped(MappingsTrue.class.getMethod("method2", String.class))).isFalse();
    }

    @Test
    public void testGetCppName() throws Exception {
        assertThat(mappings.getCppName(NoAnnotationNoMappings.class)).isEqualTo("NoAnnotationNoMappings");
        assertThat(mappings.getCppName(AnnotFalse.class)).isEqualTo("AnnotFalse");
        assertThat(mappings.getCppName(AnnotTrue.class)).isEqualTo("AnnotTrueCpp");
        assertThat(mappings.getCppName(MappingsFalse.class)).isEqualTo("MappingsFalse");
        assertThat(mappings.getCppName(MappingsTrue.class)).isEqualTo("MappingsTrueCpp");
    }

    @Test
    public void testGetCppNameField() throws Exception {
        assertThat(mappings.getCppName(AnnotFalse.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(mappings.getCppName(AnnotFalse.class.getDeclaredField("staticField2"))).isEqualTo("staticCpp");
        assertThat(mappings.getCppName(AnnotTrue.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(mappings.getCppName(AnnotTrue.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
        assertThat(mappings.getCppName(MappingsFalse.class.getDeclaredField("staticField1"))).isEqualTo("staticCpp");
        assertThat(mappings.getCppName(MappingsFalse.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
        assertThat(mappings.getCppName(MappingsTrue.class.getDeclaredField("staticField1"))).isEqualTo("staticField1");
        assertThat(mappings.getCppName(MappingsTrue.class.getDeclaredField("staticField2"))).isEqualTo("staticField2");
    }

    @Test
    public void testGetCppNameMethod() throws Exception {
        assertThat(mappings.getCppName(AnnotFalse.class.getMethod("method1"))).isEqualTo("methodCpp");
        assertThat(mappings.getCppName(AnnotFalse.class.getMethod("method2", String.class))).isEqualTo("method2");
        assertThat(mappings.getCppName(AnnotTrue.class.getMethod("method1"))).isEqualTo("method1");
        assertThat(mappings.getCppName(AnnotTrue.class.getMethod("method2", String.class))).isEqualTo("methodCpp");
        assertThat(mappings.getCppName(MappingsFalse.class.getMethod("method1"))).isEqualTo("methodCpp");
        assertThat(mappings.getCppName(MappingsFalse.class.getMethod("method2", String.class))).isEqualTo("method2");
        assertThat(mappings.getCppName(MappingsTrue.class.getMethod("method1"))).isEqualTo("method1");
        assertThat(mappings.getCppName(MappingsTrue.class.getMethod("method2", String.class))).isEqualTo("method2");
    }

    @Test
    public void testGetNamespaces() throws Exception {
        assertThat(mappings.getNamespace(NoAnnotationNoMappings.class)).containsOnly("com", "github", "loicoudot", "java4cpp", "NoAnnotationNoMappings");
        assertThat(mappings.getNamespace(AnnotFalse.class)).containsOnly("cglj", "AnnotFalse");
        assertThat(mappings.getNamespace(AnnotFalse.Inner1.class)).containsOnly("cglj", "AnnotFalse", "Inner1");
        assertThat(mappings.getNamespace(AnnotTrue.class)).containsOnly("cglj", "bigjava", "AnnotTrueCpp");
        assertThat(mappings.getNamespace(AnnotTrue.Inner2.class)).containsOnly("cglj", "bigjava", "AnnotTrueCpp", "Inner2");
        assertThat(mappings.getNamespace(AnnotTrue.Inner2.Inner21.class)).containsOnly("cglj", "bigjava", "AnnotTrueCpp", "Inner2", "InerIner");
        assertThat(mappings.getNamespace(MappingsFalse.class)).containsOnly("cglj", "MappingsFalse");
        assertThat(mappings.getNamespace(MappingsFalse.Inner1.class)).containsOnly("cglj", "MappingsFalse", "Inner1");
        assertThat(mappings.getNamespace(MappingsTrue.class)).containsOnly("cglj", "bigjava", "MappingsTrueCpp");
        assertThat(mappings.getNamespace(MappingsTrue.Inner1.class)).containsOnly("cglj", "bigjava", "MappingsTrueCpp", "InnerInner");
        assertThat(mappings.getNamespace(MappingsTrue.Inner1.Inner11.class)).containsOnly("cglj", "bigjava", "MappingsTrueCpp", "InnerInner", "Inner11");
    }
}