package com.github.loicoudot.java4cpp.model;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashSet;

import java.util.List;
import java.util.Set;

public class ClassContent {
    private ClassModel superclass;
    private final List<ClassModel> interfaces = newArrayList();
    private final List<ClassModel> nestedClass = newArrayList();
    private final List<ConstructorModel> constructors = newArrayList();
    private final List<FieldModel> staticFields = newArrayList();
    private final List<MethodModel> methods = newArrayList();
    private final List<String> enumKeys = newArrayList();
    private final Set<String> includes = newHashSet();
    private final Set<ClassModel> dependencies = newHashSet();

    public ClassModel getSuperclass() {
        return superclass;
    }

    public void setSuperclass(ClassModel superclass) {
        this.superclass = superclass;
        dependencies.add(superclass);
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

    public List<FieldModel> getStaticFields() {
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
        includes.addAll(otherModel.getType().getIncludes());
        dependencies.addAll(otherModel.getType().getDependencies());
    }

    public Set<String> getIncludes() {
        for (ClassModel nested : getNestedClass()) {
            includes.addAll(nested.getContent().getIncludes());
        }
        return includes;
    }

    public Set<ClassModel> getDependencies() {
        for (ClassModel nested : getNestedClass()) {
            dependencies.addAll(nested.getContent().getDependencies());
        }
        return dependencies;
    }
}