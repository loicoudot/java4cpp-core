package com.github.loicoudot.java4cpp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

import com.github.loicoudot.java4cpp.Context;
import com.github.loicoudot.java4cpp.Java4Cpp;
import com.github.loicoudot.java4cpp.Main;
import com.github.loicoudot.java4cpp.Settings;
import com.github.loicoudot.java4cpp.configuration.Clazz;
import com.github.loicoudot.java4cpp.configuration.Mappings;

class A {

}

interface B {

}

@Java4Cpp(superclass = true, interfaces = true)
class C extends A implements B, Cloneable {

    public static C instance;
    public static double constant;

    @Java4Cpp(staticFields = true)
    public static class D {
        public static long D;

        public class DD {

        }

        public class DE {

        }
    }

    public class E {

    }

    public C() {
    }

    public C(Object a) {
    }

    public C(long a, double b, List<String> c) {
    }

    public C(byte a) {
    }

    public C(char b) {
    }

    public List<Double> get(String a, double b, EnumA e) {
        return null;
    }

    public void get(Byte[] a) {
    }

    public void get(Character[] a) {
    }

}

enum EnumA {
    DEUX;

    public enum EnumB {
        UN
    }
}

public class Java4CppTest {

    @Test(enabled = true)
    public void ImmutableListTest() throws FileNotFoundException, IOException {
        Settings settings = new Settings();
        settings.setClean(true);
        settings.setTargetPath("/Users/maison/Documents/workspace/TestCpp/src/bigjava");
        settings.setNbThread(1);
        // settings.setJarFiles("/Users/maison/Documents/workspace/HvsJava4Cpp/NRJava4Cpp/java/target/testJava4cpp-1.0.62-SNAPSHOT.jar");

        Context context = new Context(settings);

        Mappings mappings = new Mappings();
        Clazz clazz = new Clazz(C.class);
        clazz.setSuperclass(true);
        clazz.setInterfaceAll(true);
        clazz.setExportFields(true);
        mappings.getClasses().add(clazz);
        context.addMappings(mappings);

        new Main().execute(context);
    }
}
