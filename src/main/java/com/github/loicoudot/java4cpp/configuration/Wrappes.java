package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public final class Wrappes {

    private final List<Wrappe> wrappes = newArrayList();
    private final List<String> noWrappes = newArrayList();

    Wrappes() {
    }

    @XmlElementWrapper
    @XmlElement(name = "wrappe")
    public List<Wrappe> getWrappes() {
        return wrappes;
    }

    @XmlElementWrapper
    @XmlElement(name = "noWrappe")
    public List<String> getNoWrappes() {
        return noWrappes;
    }

    public Wrappe findWrappe(String name) {
        for (Wrappe wrappe : getWrappes()) {
            if (wrappe.getName().equals(name)) {
                return wrappe;
            }
        }
        return null;
    }
}
