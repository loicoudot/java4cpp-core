package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "namespaces", "classes", "keywords" })
public final class Mappings {

    private final List<String> keywords = newArrayList();
    private final List<ClassMapping> classes = newArrayList();
    private final List<Namespace> namespaces = newArrayList();

    @XmlElementWrapper
    @XmlElement(name = "keyword")
    public List<String> getKeywords() {
        return keywords;
    }

    @XmlElementWrapper
    @XmlElement(name = "class")
    public List<ClassMapping> getClasses() {
        return classes;
    }

    @XmlElementWrapper
    @XmlElement(name = "namespace")
    public List<Namespace> getNamespaces() {
        return namespaces;
    }
}
