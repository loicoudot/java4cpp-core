package com.github.loicoudot.java4cpp.model;

public final class FieldModel {
    private final String javaName;
    private String cppName;
    private ClassModel type;

    public FieldModel(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaName() {
        return javaName;
    }

    public String getCppName() {
        return cppName;
    }

    public void setCppName(String cppName) {
        this.cppName = cppName;
    }

    public ClassModel getType() {
        return type;
    }

    public void setType(ClassModel type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "field(" + javaName + ")";
    }
}
