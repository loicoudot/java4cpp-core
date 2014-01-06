package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newHashSet;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "fallback", "array", "enumeration", "templates" })
public final class Datatypes {
    private TypeTemplate fallback;
    private TypeTemplate array;
    private TypeTemplate enumeration;
    private final Set<TypeTemplate> templates = newHashSet();

    public TypeTemplate getFallback() {
        return fallback;
    }

    public void setFallback(TypeTemplate fallback) {
        this.fallback = fallback;
    }

    public TypeTemplate getArray() {
        return array;
    }

    public void setArray(TypeTemplate array) {
        this.array = array;
    }

    @XmlElement(name = "enum")
    public TypeTemplate getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(TypeTemplate enumeration) {
        this.enumeration = enumeration;
    }

    @XmlElementWrapper
    @XmlElement(name = "template")
    public Set<TypeTemplate> getTemplates() {
        return templates;
    }
}
