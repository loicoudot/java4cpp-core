package com.github.loicoudot.java4cpp.model;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;
import static com.github.loicoudot.java4cpp.Utils.newHashSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import freemarker.template.TemplateMethodModelEx;

public final class ClassModel {

    private final Class<?> clazz;

    private final String javaName;
    private String cppFullName;
    private String cppShortName;
    private ClassModel owner;
    private final boolean isPrimitive;
    private final boolean isEnum;
    private final boolean isArray;
    private final boolean isInterface;
    private final boolean isInnerClass;
    private final boolean isThrowable;
    private final boolean isCheckedException;
    private final boolean isCloneable;
    private ClassModel superclass;
    private final List<ClassModel> interfaces = newArrayList();
    private final List<ClassModel> nestedClass = newArrayList();
    private final List<ConstructorModel> constructors = newArrayList();
    private final List<FieldModel> staticFields = newArrayList();
    private final List<MethodModel> methods = newArrayList();
    private final List<String> enumKeys = newArrayList();

    private boolean isParameterized;
    private final List<ClassModel> parameterized = newArrayList();
    private boolean needAnalyzing;
    private String javaSignature;
    private String jniSignature;
    private String jniMethodName;
    private String cppType;
    private String cppReturnType;
    private ClassModel innerType;
    private HashMap<String, Object> functions = newHashMap();
    private TemplateMethodModelEx addInclude;
    private TemplateMethodModelEx addDependency;
    private final Set<String> outterIncludes = newHashSet();
    private final Set<ClassModel> outterDependencies = newHashSet();

    private final Set<String> includes = newHashSet();
    private final Set<ClassModel> dependencies = newHashSet();

    public ClassModel(Class<?> clazz) {
        this.clazz = clazz;
        javaName = clazz.getName();
        isPrimitive = clazz.isPrimitive();
        isParameterized = false;
        isEnum = clazz.isEnum();
        isArray = clazz.isArray();
        isInterface = clazz.isInterface();
        isInnerClass = clazz.getEnclosingClass() != null;
        isThrowable = isThrowable();
        isCheckedException = isCheckedException();
        isCloneable = Arrays.asList(clazz.getInterfaces()).contains(java.lang.Cloneable.class);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getJavaName() {
        return javaName;
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

    public boolean isIsParameterized() {
        return isParameterized;
    }

    public List<ClassModel> getParameterized() {
        return parameterized;
    }

    public void addParameterized(ClassModel parameterized) {
        this.parameterized.add(parameterized);
    }

    public void setParameterized(boolean isParameterized) {
        this.isParameterized = isParameterized;
    }

    public ClassModel getSuperclass() {
        return superclass;
    }

    public void setSuperclass(ClassModel superclassModel) {
        this.superclass = superclassModel;
        dependencies.add(superclassModel);
    }

    public List<ClassModel> getInterfaces() {
        return interfaces;
    }

    public void addInterface(ClassModel interfaceModel) {
        interfaces.add(interfaceModel);
        dependencies.add(interfaceModel);
    }

    public List<ClassModel> getNestedClass() {
        return nestedClass;
    }

    public void addNestedClass(ClassModel nestedClassModel) {
        nestedClass.add(nestedClassModel);
        updateDependencies(nestedClassModel);
    }

    public List<ConstructorModel> getConstructors() {
        return constructors;
    }

    public void addConstructor(ConstructorModel constructorModel) {
        constructors.add(constructorModel);
        for (ClassModel parameter : constructorModel.getParameters()) {
            updateDependencies(parameter);
        }
    }

    public List<FieldModel> getFields() {
        return staticFields;
    }

    public void addField(FieldModel fieldModel) {
        staticFields.add(fieldModel);
        updateDependencies(fieldModel.getType());
    }

    public List<MethodModel> getMethods() {
        return methods;
    }

    public void addMethod(MethodModel methodModel) {
        methods.add(methodModel);
        updateDependencies(methodModel.getReturnType());
        for (ClassModel parameter : methodModel.getParameters()) {
            updateDependencies(parameter);
        }
    }

    public List<String> getEnumKeys() {
        return enumKeys;
    }

    public void addEnumKey(String key) {
        enumKeys.add(key);
    }

    private void updateDependencies(ClassModel otherModel) {
        includes.addAll(otherModel.getOutterIncludes());
        dependencies.addAll(otherModel.getOutterDependencies());
    }

    public boolean getNeedAnalyzing() {
        return needAnalyzing;
    }

    public void setNeedAnalyzing(boolean needAnalyzing) {
        this.needAnalyzing = needAnalyzing;
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

    public HashMap<String, Object> getFunctions() {
        return functions;
    }

    public void setFunctions(HashMap<String, Object> functions) {
        this.functions = functions;
    }

    public TemplateMethodModelEx getAddInclude() {
        return addInclude;
    }

    public void setAddInclude(TemplateMethodModelEx addInclude) {
        this.addInclude = addInclude;
    }

    public TemplateMethodModelEx getAddDependency() {
        return addDependency;
    }

    public void setAddDependency(TemplateMethodModelEx addDependency) {
        this.addDependency = addDependency;
    }

    public Set<String> getOutterIncludes() {
        return outterIncludes;
    }

    public Set<ClassModel> getOutterDependencies() {
        return outterDependencies;
    }

    public Set<String> getIncludes() {
        for (ClassModel nested : getNestedClass()) {
            includes.addAll(nested.getIncludes());
        }
        return includes;
    }

    public Set<ClassModel> getDependencies() {
        for (ClassModel nested : getNestedClass()) {
            dependencies.addAll(nested.getDependencies());
        }
        return dependencies;
    }

    private boolean isThrowable() {
        Class<?> current = clazz;
        do {
            if (current == Throwable.class) {
                return true;
            }
            current = current.getSuperclass();
        } while (current != null);
        return false;
    }

    private boolean isCheckedException() {
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
        return String.format("ClassModel(%s)", clazz.getName());
    }
}
