package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = { "sourceTemplates", "needAnalyzing", "cppType", "cppReturnType", "dependencies", "functions" })
public final class TypeTemplate {
    private Class<?> clazz;
    private final List<String> sourceTemplates = newArrayList();
    private Boolean needAnalyzing = true;
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

    @XmlElementWrapper
    @XmlElement(name = "sourceTemplate")
    public List<String> getSourceTemplates() {
        return sourceTemplates;
    }

    public Boolean getNeedAnalyzing() {
        return needAnalyzing;
    }

    public void setNeedAnalyzing(Boolean needAnalyzing) {
        this.needAnalyzing = needAnalyzing;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TypeTemplate other = (TypeTemplate) obj;
        if (clazz == null) {
            if (other.clazz != null) {
                return false;
            }
        } else if (!clazz.getName().equals(other.clazz.getName())) {
            return false;
        }
        return true;
    }
}
