package com.github.loicoudot.java4cpp.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public final class Wrappe {

    private String name;
    private String cppName;

    Wrappe() {
    }

    public Wrappe(String name) {
        this.name = name;
        this.cppName = "";
    }

    public Wrappe(String name, String cppName) {
        this.name = name;
        this.cppName = cppName;
    }

    @XmlValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getCppName() {
        return cppName;
    }

    public void setCppName(String cppName) {
        this.cppName = cppName;
    }

    @Override
    public String toString() {
        return String.format("Wrappe(%s, %s)", name, cppName);
    }
}
