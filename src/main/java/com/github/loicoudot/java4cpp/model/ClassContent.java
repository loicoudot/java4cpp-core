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

    public ClassModel getSuperclass() {
        return superclass;
    }

    public void setSuperclass(ClassModel superclass) {
        this.superclass = superclass;
    }

    public List<ClassModel> getInterfaces() {
        return interfaces;
    }

    public void addInterface(ClassModel interfaceModel) {
        interfaces.add(interfaceModel);
    }

    public List<ClassModel> getNestedClass() {
        return nestedClass;
    }

    public void addNestedClass(ClassModel nestedClassModel) {
        nestedClass.add(nestedClassModel);
    }

    public List<ConstructorModel> getConstructors() {
        return constructors;
    }

    public void addConstructor(ConstructorModel constructorModel) {
        constructors.add(constructorModel);
    }

    public List<FieldModel> getStaticFields() {
        return staticFields;
    }

    public void addField(FieldModel fieldModel) {
        staticFields.add(fieldModel);
    }

    public List<MethodModel> getMethods() {
        return methods;
    }

    public void addMethod(MethodModel methodModel) {
        methods.add(methodModel);
    }

    public List<String> getEnumKeys() {
        return enumKeys;
    }

    public void addEnumKey(String key) {
        enumKeys.add(key);
    }

    public Set<String> getIncludes() {
        Set<String> includes = newHashSet();
        for (ClassModel nested : getNestedClass()) {
            includes.addAll(nested.getContent().getIncludes());
        }
        for (ConstructorModel constructor : getConstructors()) {
            for (ClassModel parameter : constructor.getParameters()) {
                includes.addAll(parameter.getType().getIncludes());
            }
        }
        for (FieldModel field : getStaticFields()) {
            includes.addAll(field.getType().getType().getIncludes());
        }
        for (MethodModel method : getMethods()) {
            includes.addAll(method.getReturnType().getType().getIncludes());
            for (ClassModel parameter : method.getParameters()) {
                includes.addAll(parameter.getType().getIncludes());
            }
        }
        return includes;
    }

    public Set<ClassModel> getDependencies() {
        Set<ClassModel> dependencies = newHashSet();
        if (superclass != null) {
            dependencies.add(superclass);
        }
        for (ClassModel interfaze : getInterfaces()) {
            dependencies.add(interfaze);
        }
        for (ClassModel nested : getNestedClass()) {
            dependencies.addAll(nested.getContent().getDependencies());
        }
        for (ConstructorModel constructor : getConstructors()) {
            for (ClassModel parameter : constructor.getParameters()) {
                dependencies.addAll(parameter.getType().getDependencies());
            }
        }
        for (FieldModel field : getStaticFields()) {
            dependencies.addAll(field.getType().getType().getDependencies());
        }
        for (MethodModel method : getMethods()) {
            dependencies.addAll(method.getReturnType().getType().getDependencies());
            for (ClassModel parameter : method.getParameters()) {
                dependencies.addAll(parameter.getType().getDependencies());
            }
        }
        return dependencies;
    }
}