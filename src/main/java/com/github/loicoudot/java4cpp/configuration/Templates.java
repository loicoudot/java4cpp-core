package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "sourceTemplates", "globalTemplates", "datatypes", "copyFiles" })
public final class Templates {
    private final List<String> sourceTemplates = newArrayList();
    private final List<String> globalTemplates = newArrayList();
    private final List<String> copyFiles = newArrayList();
    private Datatypes datatypes = new Datatypes();

    @XmlElementWrapper
    @XmlElement(name = "sourceTemplate")
    public List<String> getSourceTemplates() {
        return sourceTemplates;
    }

    @XmlElementWrapper
    @XmlElement(name = "globalTemplate")
    public List<String> getGlobalTemplates() {
        return globalTemplates;
    }

    @XmlElementWrapper
    @XmlElement(name = "copyFile")
    public List<String> getCopyFiles() {
        return copyFiles;
    }

    public Datatypes getDatatypes() {
        return datatypes;
    }

    public void setDatatypes(Datatypes datatypes) {
        this.datatypes = datatypes;
    }
}
