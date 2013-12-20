package com.github.loicoudot.java4cpp.model;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

public final class MethodModel {
    private final String javaName;
    private String cppName;
    private boolean isStatic;
    private ClassModel returnType;
    private final List<ClassModel> parameters = newArrayList();

    public MethodModel(String javaName) {
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

    public boolean isIsStatic() {
        return isStatic;
    }

    public void setStatic(boolean statik) {
        this.isStatic = statik;
    }

    public ClassModel getReturnType() {
        return returnType;
    }

    public void setReturnType(ClassModel returnType) {
        this.returnType = returnType;
    }

    public List<ClassModel> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "method(" + javaName + ")";
    }
}
