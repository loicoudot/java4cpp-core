package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = { "generate", "cppType", "cppReturnType", "dependencies", "functions" })
public final class TypeTemplate {
    private Class<?> clazz;
    private Boolean generate = true;
    private String cppType;
    private String cppReturnType;
    private String dependencies;
    private final List<Function> functions = newArrayList();

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(ClassAdapter.class)
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Boolean getGenerate() {
        return generate;
    }

    public void setGenerate(Boolean generate) {
        this.generate = generate;
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

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    @XmlElementWrapper
    @XmlElement(name = "function")
    public List<Function> getFunctions() {
        return functions;
    }

    @Override
    public String toString() {
        return String.format("TypeTemplate(%s)", clazz.getName());
    }
}
