package com.github.loicoudot.java4cpp.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public final class Namespace {

    private String javaPackage;
    private String namespace;

    public Namespace() {
    }

    public Namespace(String javaPackage, String namespace) {
        this.javaPackage = javaPackage;
        this.namespace = namespace;
    }

    @XmlAttribute(name = "package")
    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    @XmlValue
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        return String.format("Namespace(%s, %s)", javaPackage, namespace);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((javaPackage == null) ? 0 : javaPackage.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Namespace other = (Namespace) obj;
        if (javaPackage == null) {
            if (other.javaPackage != null)
                return false;
        } else if (!javaPackage.equals(other.javaPackage))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        return true;
    }
}
