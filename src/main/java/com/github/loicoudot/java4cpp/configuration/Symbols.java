package com.github.loicoudot.java4cpp.configuration;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Symbols {
    private List<String> symbols = newArrayList();

    @XmlElementWrapper(name = "symbols")
    @XmlElement(name = "symbol")
    public List<String> getSymbols() {
        return symbols;
    }
}
