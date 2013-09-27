package com.github.loicoudot.java4cpp.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public final class TypeTemplate {
    private Class<?> clazz;
    private String cppType;
    private String cppReturnType;
    private String java2cpp;
    private String cpp2java;
    private String cpp2javaClean;
    private String dependencies;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(ClassAdapter.class)
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getCppType() {
        return cppType;
    }

    public void setCppType(String cppType) {
        this.cppType = cppType;
    }

    public String getCppReturnType() {
        return cppReturnType;
    }

    public void setCppReturnType(String cppReturnType) {
        this.cppReturnType = cppReturnType;
    }

    public String getJava2cpp() {
        return java2cpp;
    }

    public void setJava2cpp(String java2cpp) {
        this.java2cpp = java2cpp;
    }

    public String getCpp2java() {
        return cpp2java;
    }

    public void setCpp2java(String cpp2java) {
        this.cpp2java = cpp2java;
    }

    public String getCpp2javaClean() {
        return cpp2javaClean;
    }

    public void setCpp2javaClean(String cpp2javaClean) {
        this.cpp2javaClean = cpp2javaClean;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return String.format("TypeTemplate(%s)", clazz.getName());
    }
}
