package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = { "cppType", "cppReturnType", "functions" })
public final class TypeTemplate {
    private Class<?> clazz;
    private String cppType;
    private String cppReturnType;
    private final List<Function> functions = newArrayList();

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
