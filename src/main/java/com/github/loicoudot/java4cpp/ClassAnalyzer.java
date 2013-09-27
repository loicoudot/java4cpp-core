package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public final class ClassAnalyzer {
    private final Logger log = LoggerFactory.getLogger(ClassAnalyzer.class);
    private final Class<?> clazz;
    private final Context context;
    private final MappingsHelper mappings;

    public ClassAnalyzer(Class<?> clazz, Context context) {
        this.clazz = clazz;
        this.context = context;
        this.mappings = context.getMappings(clazz);
    }

    public ClassModel fillModel(ClassModel classModel) {

        class AddOutterDependency implements TemplateMethodModelEx {

            ClassModel model;

            public AddOutterDependency(ClassModel model) {
                this.model = model;
            }

            @Override
            @SuppressWarnings("rawtypes")
            public Object exec(List arguments) throws TemplateModelException {
                if (arguments.size() != 1) {
                    throw new TemplateModelException("AddOutterDependency need one parameter (a class name or a ClassModel instance).");
                }
                Object dependency = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
                if (dependency instanceof String) {
                    try {
                        model.getOutterDependencies().add(context.getClassModel(Class.forName((String) dependency)));
                    } catch (ClassNotFoundException e) {
                        log.error("Class not found: ", e);
                    }
                } else if (dependency instanceof ClassModel) {
                    model.getOutterDependencies().add((ClassModel) dependency);
                }
                return "";
            }
        }

        class AddOutterInclude implements TemplateMethodModelEx {

            ClassModel model;

            public AddOutterInclude(ClassModel model) {
                this.model = model;
            }

            @Override
            @SuppressWarnings("rawtypes")
            public Object exec(List arguments) throws TemplateModelException {
                if (arguments.size() != 1) {
                    throw new TemplateModelException("AddOutterInclude need one parameter.");
                }
                Object dependency = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
                if (dependency instanceof String) {
                    model.getOutterIncludes().add((String) dependency);
                }
                return "";
            }
        }

        classModel.setCheckedException(isCheckedException(clazz));
        classModel.setCloneable(isCloneable());
        classModel.setCppFullName(Joiner.on("::").join(mappings.getNamespaces()));
        classModel.setCppShortName(mappings.getNamespaces()[mappings.getNamespaces().length - 1]);
        classModel.setOwner(classModel.isIsInnerClass() ? context.getClassModel(clazz.getDeclaringClass()) : classModel);

        classModel.setJavaSignature(Datatype.getJavaSignature(clazz));
        classModel.setJniSignature(Datatype.getJNISignature(clazz));
        classModel.setJniMethodName(Datatype.getJNIMethodName(clazz));
        TypeTemplates typeTemplates = context.getTemplateManager().getTypeTemplates(clazz);
        classModel.setAddInclude(new AddOutterInclude(classModel));
        classModel.setAddDependency(new AddOutterDependency(classModel));
        classModel.setCppType(typeTemplates.getCppType(classModel));
        classModel.setCppReturnType(typeTemplates.getCppReturnType(classModel));
        classModel.setJava2cpp(typeTemplates.getJava2cpp(classModel));
        classModel.setCpp2java(typeTemplates.getCpp2Java(classModel));
        classModel.setCpp2javaClean(typeTemplates.getCpp2JavaClean(classModel));
        typeTemplates.processDependencies(classModel);

        if (getSuperClass() != null) {
            classModel.setSuperclass(context.getClassModel(getSuperClass()));
        }
        for (Class<?> interfac : getInterfaces()) {
            classModel.addInterface(context.getClassModel(interfac));
        }

        for (Class<?> nestedClass : getNestedClasses()) {
            classModel.addNestedClass(context.getClassModel(nestedClass));
        }

        for (Field field : getStaticFields()) {
            classModel.addField(new FieldAnalyzer(field, context).getModel());
        }

        for (String key : getEnumKeys()) {
            classModel.addEnumKey(key);
        }

        for (Constructor<?> constructor : getConstructors()) {
            classModel.addConstructor(new ConstructorAnalyzer(constructor, context).getModel());
        }

        for (Method method : getMethods()) {
            classModel.addMethod(new MethodAnalyzer(method, context).getModel());
        }

        for (ClassModel dependency : classModel.getDependencies()) {
            context.addClassToDo(dependency.getClazz());
        }

        return classModel;
    }

    private boolean isCloneable() {
        return Arrays.asList(clazz.getInterfaces()).contains(java.lang.Cloneable.class);
    }

    public static boolean isCheckedException(Class<?> type) {
        if (type == RuntimeException.class || type == Error.class) {
            return false;
        }
        if (type == Throwable.class) {
            return true;
        }

        Class<?> parent = type.getSuperclass();
        return parent != null && isCheckedException(parent);
    }

    private Class<?> getSuperClass() {
        if (mappings.exportSuperClass()) {
            Class<?> parent = clazz.getSuperclass();
            if (parent == null) {
                parent = Object.class;
            }
            return parent;
        }
        return null;
    }

    private List<Class<?>> getInterfaces() {
        List<Class<?>> interfaces = newArrayList();
        for (Class<?> interfac : clazz.getInterfaces()) {
            if (interfac != Cloneable.class && mappings.isInterfaceWrapped(interfac)) {
                interfaces.add(interfac);
            }
        }
        return interfaces;
    }

    private List<Class<?>> getNestedClasses() {
        List<Class<?>> list = newArrayList();
        for (Class<?> clas : clazz.getDeclaredClasses()) {
            if (Modifier.isPublic(clas.getModifiers()) && mappings.isInnerClassWrapped(clas)) {
                list.add(clas);
            }
        }
        return list;
    }

    private List<Field> getStaticFields() {
        ArrayList<Field> list = newArrayList();
        for (Field field : clazz.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && mappings.isFieldWrapped(field)) {
                list.add(field);
            }
        }
        return list;
    }

    private List<String> getEnumKeys() {
        List<String> enumKeys = new ArrayList<String>();
        for (Field field : clazz.getFields()) {
            if (field.isEnumConstant()) {
                enumKeys.add(context.escapeName(field.getName()));
            }
        }
        return enumKeys;
    }

    private List<Constructor<?>> getConstructors() {
        List<Constructor<?>> list = newArrayList();
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (mappings.isConstructorWrapped(constructor)) {
                list.add(constructor);
            }
        }
        return list;
    }

    private List<Method> getMethods() {
        List<Method> list = newArrayList();
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && !method.isSynthetic() && !method.getName().equals("clone") && mappings.isMethodWrapped(method)) {
                list.add(method);
            }
        }
        return list;
    }
}
