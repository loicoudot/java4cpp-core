package com.github.loicoudot.java4cpp.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@code ClassAdapter} is a {@code XmlAdapter} for the mapping between a
 * {@code Class<?>} and a {@code String}. The default JAXB adapter can marshal
 * all classes type, even primitive type (for exemple a {@code Boolean.TYPE} is
 * marshalling in {@code boolean}), but it cannot unmarshall {@code boolean}
 * into a {@code Boolean.TYPE}. This adapter add the ability of
 * marshalling/unmarshalling primitive types.
 * 
 * @author Loic Oudot
 * 
 */
final class ClassAdapter extends XmlAdapter<String, Class<?>> {

    private static final Map<String, Class<?>> PRIMITIVE_MAP = new HashMap<String, Class<?>>();

    static {
        PRIMITIVE_MAP.put("boolean", Boolean.TYPE);
        PRIMITIVE_MAP.put("byte", Byte.TYPE);
        PRIMITIVE_MAP.put("char", Character.TYPE);
        PRIMITIVE_MAP.put("double", Double.TYPE);
        PRIMITIVE_MAP.put("float", Float.TYPE);
        PRIMITIVE_MAP.put("int", Integer.TYPE);
        PRIMITIVE_MAP.put("long", Long.TYPE);
        PRIMITIVE_MAP.put("short", Short.TYPE);
        PRIMITIVE_MAP.put("void", Void.TYPE);
    }

    @Override
    public String marshal(Class<?> v) throws Exception {
        return v.getName();
    }

    @Override
    public Class<?> unmarshal(String v) throws Exception {
        if (PRIMITIVE_MAP.containsKey(v)) {
            return PRIMITIVE_MAP.get(v);
        }
        return Class.forName(v);
    }

}
