package com.github.loicoudot.java4cpp.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public final class Function {

    private String name;
    private String template;

    Function() {
    }

    public Function(String name) {
        this.name = name;
        this.template = "";
    }

    public Function(String name, String template) {
        this.name = name;
        this.template = template;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlValue
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return String.format("Function(%s)", name);
    }
}
