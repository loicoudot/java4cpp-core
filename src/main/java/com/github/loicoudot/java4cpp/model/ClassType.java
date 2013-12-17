package com.github.loicoudot.java4cpp.model;

import static com.github.loicoudot.java4cpp.Utils.newHashSet;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.github.loicoudot.java4cpp.Context;

import freemarker.template.TemplateMethodModelEx;

public class ClassType {
    private final Type type;
    private final String javaName;
    private final boolean isPrimitive;
    private final boolean isEnum;
    private final boolean isArray;
    private final boolean isInterface;
    private final boolean isInnerClass;
    private final boolean isThrowable;
    private final boolean isCheckedException;
    private final boolean isCloneable;
    private final boolean isAbstract;
    private String cppFullName;
    private String cppShortName;
    private ClassModel owner;
    private String javaSignature;
    private String jniSignature;
    private String jniMethodName;
    private String cppType;
    private String cppReturnType;
    private ClassModel innerType;
    private ClassModel finalInnerType;
    private HashMap<String, Object> functions;
    private TemplateMethodModelEx addIncludes;
    private TemplateMethodModelEx addDependencies;
    private final Set<String> includes = newHashSet();
    private final Set<ClassModel> dependencies = newHashSet();

    public ClassType(Type type) {
        this.type = type;
        Class<?> clazz = getClazz();
        this.javaName = clazz.getName();
        this.isAbstract = Modifier.isAbstract(clazz.getModifiers());
        this.isPrimitive = clazz.isPrimitive();
        this.isEnum = clazz.isEnum();
        this.isArray = clazz.isArray();
        this.isInterface = clazz.isInterface();
        this.isInnerClass = clazz.getEnclosingClass() != null;
        this.isThrowable = isThrowable(clazz);
        this.isCheckedException = isCheckedException(clazz);
        this.isCloneable = Arrays.asList(clazz.getInterfaces()).contains(java.lang.Cloneable.class);
    }

    public Type getType() {
        return type;
    }

    public Class<?> getClazz() {
        return Context.getRawClass(type);
    }

    public String getJavaName() {
        return javaName;
    }

    public boolean isIsPrimitive() {
        return isPrimitive;
    }

    public boolean isIsEnum() {
        return isEnum;
    }

    public boolean isIsArray() {
        return isArray;
    }

    public boolean isIsInterface() {
        return isInterface;
    }

    public boolean isIsInnerClass() {
        return isInnerClass;
    }

    public boolean isIsThrowable() {
        return isThrowable;
    }

    public boolean isIsCheckedException() {
        return isCheckedException;
    }

    public boolean isIsCloneable() {
        return isCloneable;
    }

    public boolean isIsAbstract() {
        return isAbstract;
    }

    public String getCppFullName() {
        return cppFullName;
    }

    public void setCppFullName(String cppFullName) {
        this.cppFullName = cppFullName;
    }

    public String getCppShortName() {
        return cppShortName;
    }

    public void setCppShortName(String cppShortName) {
        this.cppShortName = cppShortName;
    }

    public ClassModel getOwner() {
        return owner;
    }

    public void setOwner(ClassModel owner) {
        this.owner = owner;
    }

    public String getJavaSignature() {
        return javaSignature;
    }

    public void setJavaSignature(String javaSignature) {
        this.javaSignature = javaSignature;
    }

    public String getJniSignature() {
        return jniSignature;
    }

    public void setJniSignature(String jniSignature) {
        this.jniSignature = jniSignature;
    }

    public String getJniMethodName() {
        return jniMethodName;
    }

    public void setJniMethodName(String jniMethodName) {
        this.jniMethodName = jniMethodName;
    }

    public String getCppType() {
        return cppType;
    }

    public void setCppType(String cppType) {
        this.cppType = cppType;
    }

    public String getCppReturnType() {
        return cppReturnType;
    }

    public void setCppReturnType(String cppReturnType) {
        this.cppReturnType = cppReturnType;
    }

    public ClassModel getInnerType() {
        return innerType;
    }

    public void setInnerType(ClassModel innerType) {
        this.innerType = innerType;
    }

    public ClassModel getFinalInnerType() {
        return finalInnerType;
    }

    public void setFinalInnerType(ClassModel finalInnerType) {
        this.finalInnerType = finalInnerType;
    }

    public HashMap<String, Object> getFunctions() {
        return functions;
    }

    public void setFunctions(HashMap<String, Object> functions) {
        this.functions = functions;
    }

    public TemplateMethodModelEx getAddIncludes() {
        return addIncludes;
    }

    public void setAddIncludes(TemplateMethodModelEx addInclude) {
        this.addIncludes = addInclude;
    }

    public TemplateMethodModelEx getAddDependencies() {
        return addDependencies;
    }

    public void setAddDependencies(TemplateMethodModelEx addDependencies) {
        this.addDependencies = addDependencies;
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public Set<ClassModel> getDependencies() {
        return dependencies;
    }

    private boolean isThrowable(Class<?> clazz) {
        Class<?> current = clazz;
        do {
            if (current == Throwable.class) {
                return true;
            }
            current = current.getSuperclass();
        } while (current != null);
        return false;
    }

    private boolean isCheckedException(Class<?> clazz) {
        Class<?> current = clazz;
        do {
            if (current == RuntimeException.class || current == Error.class) {
                return false;
            }
            if (current == Throwable.class) {
                return true;
            }

            current = current.getSuperclass();
        } while (current != null);
        return false;
    }

    @Override
    public String toString() {
        return String.format("type(%s)", type);
    }
}