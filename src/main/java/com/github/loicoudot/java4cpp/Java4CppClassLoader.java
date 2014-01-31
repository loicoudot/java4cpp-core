package com.github.loicoudot.java4cpp;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Java4CppClassLoader extends URLClassLoader {

    private List<String> visibleJars = null;

    public Java4CppClassLoader() {
        this(new URL[0], Thread.currentThread().getContextClassLoader());
    }

    public Java4CppClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public String getJar(String jarName) {
        for (String jarPath : getJars()) {
            if (jarPath.contains(jarName)) {
                return jarPath;
            }
        }
        return null;
    }

    private List<String> getJars() {
        if (visibleJars == null) {
            visibleJars = new ArrayList<String>();
            try {
                Enumeration<URL> jars = getResources("META-INF/MANIFEST.MF");
                while (jars.hasMoreElements()) {
                    String path = jars.nextElement().getPath();
                    visibleJars.add(path.substring(0, path.indexOf('!')));
                }
            } catch (Exception e) {
            }
        }
        return visibleJars;
    }
}
