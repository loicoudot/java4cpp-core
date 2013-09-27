package com.github.loicoudot.java4cpp.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public final class Clazz {

    private Class<?> clazz;
    private boolean superclass = false;
    private boolean interfaceAll = false;
    private boolean exportFields = false;
    private boolean exportAll = true;
    private String cppName;

    private Wrappes interfaces = new Wrappes();
    private Wrappes innerClasses = new Wrappes();
    private Wrappes staticFields = new Wrappes();
    private Wrappes constructors = new Wrappes();
    private Wrappes methods = new Wrappes();

    Clazz() {
    }

    public Clazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @XmlAttribute(name = "class")
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @XmlAttribute
    public boolean isSuperclass() {
        return superclass;
    }

    public void setSuperclass(boolean superclass) {
        this.superclass = superclass;
    }

    @XmlAttribute
    public boolean isInterfaceAll() {
        return interfaceAll;
    }

    public void setInterfaceAll(boolean interfaceAll) {
        this.interfaceAll = interfaceAll;
    }

    @XmlAttribute
    public boolean isExportFields() {
        return exportFields;
    }

    public void setExportFields(boolean exportFields) {
        this.exportFields = exportFields;
    }

    @XmlAttribute
    public boolean isExportAll() {
        return exportAll;
    }

    public void setExportAll(boolean exportAll) {
        this.exportAll = exportAll;
    }

    @XmlElement
    public String getCppName() {
        return cppName;
    }

    public void setCppName(String cppName) {
        this.cppName = cppName;
    }

    @XmlElement
    public Wrappes getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Wrappes interfaces) {
        this.interfaces = interfaces;
    }

    @XmlElement
    public Wrappes getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(Wrappes innerClasses) {
        this.innerClasses = innerClasses;
    }

    @XmlElement
    public Wrappes getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(Wrappes staticFields) {
        this.staticFields = staticFields;
    }

    @XmlElement
    public Wrappes getConstructors() {
        return constructors;
    }

    public void setConstructors(Wrappes constructors) {
        this.constructors = constructors;
    }

    @XmlElement
    public Wrappes getMethods() {
        return methods;
    }

    public void setMethods(Wrappes methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {
        return String.format("Clazz(%s)", clazz.getName());
    }
}
