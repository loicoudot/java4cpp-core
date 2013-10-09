package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * Data-model builder for a {@code Class}
 * 
 * @author Loic Oudot
 * 
 */
final class ClassAnalyzer {
    private final Class<?> clazz;
    private final Context context;
    private final MappingsManager mappings;

    public ClassAnalyzer(Class<?> clazz, Context context) {
        this.clazz = clazz;
        this.context = context;
        this.mappings = context.getMappingsManager();
    }

    /**
     * Fill {@code classModel} data-model bean, with the content of
     * {@code clazz}.
     * 
     * @param classModel
     *            the data-model to fill
     */
    public void fillModel(ClassModel classModel) {

        /**
         * FreeMarker function availlable inside templates to add a direct
         * dependency for the class.
         * 
         * @author Loic Oudot
         * 
         */
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
                    model.getOutterDependencies().add(context.getClassModel((String) dependency));
                } else if (dependency instanceof ClassModel) {
                    model.getOutterDependencies().add((ClassModel) dependency);
                }
                return "";
            }
        }

        /**
         * FreeMarker function availlable inside templates to add an incldue
         * file for the class.
         * 
         * @author Loic Oudot
         * 
         */
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

        StringBuilder fullName = new StringBuilder();
        String shortName = "";
        String sep = "";
        for (String namespace : context.getMappingsManager().getNamespace(clazz)) {
            fullName.append(sep).append(namespace);
            sep = "::";
            shortName = namespace;
        }
        classModel.setCppFullName(fullName.toString());
        classModel.setCppShortName(shortName);
        classModel.setOwner(classModel.isIsInnerClass() ? context.getClassModel(clazz.getDeclaringClass()) : classModel);

        classModel.setJavaSignature(Datatype.getJavaSignature(clazz));
        classModel.setJniSignature(Datatype.getJNISignature(clazz));
        classModel.setJniMethodName(Datatype.getJNIMethodName(clazz));
        classModel.setAddInclude(new AddOutterInclude(classModel));
        classModel.setAddDependency(new AddOutterDependency(classModel));
        TypeTemplates typeTemplates = context.getTemplateManager().getTypeTemplates(clazz);
        classModel.setCppType(typeTemplates.getCppType(classModel));
        classModel.setCppReturnType(typeTemplates.getCppReturnType(classModel));
        typeTemplates.executeDependencies(classModel);
        classModel.setFunctions(typeTemplates.getFunctions(classModel));

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
    }

    private Class<?> getSuperClass() {
        if (mappings.exportSuperClass(clazz)) {
            return clazz.getSuperclass();
        }
        return null;
    }

    private List<Class<?>> getInterfaces() {
        List<Class<?>> interfaces = newArrayList();
        for (Class<?> interfac : clazz.getInterfaces()) {
            if (interfac != Cloneable.class && mappings.isInterfaceWrapped(clazz, interfac)) {
                interfaces.add(interfac);
            }
        }
        return interfaces;
    }

    private List<Class<?>> getNestedClasses() {
        List<Class<?>> list = newArrayList();
        for (Class<?> nested : clazz.getDeclaredClasses()) {
            if (Modifier.isPublic(nested.getModifiers()) && mappings.isInnerClassWrapped(clazz, nested)) {
                list.add(nested);
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
                enumKeys.add(context.getMappingsManager().escapeName(field.getName()));
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

    @Override
    public String toString() {
        return String.format("ClassAnalyzer(%s)", clazz.getName());
    }
}
