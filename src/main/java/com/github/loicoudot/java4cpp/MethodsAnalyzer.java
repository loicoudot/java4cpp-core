package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Wrappe;
import com.github.loicoudot.java4cpp.model.ClassModel;
import com.github.loicoudot.java4cpp.model.MethodModel;

/**
 * Data-model builder for class methods
 * 
 * @author Loic Oudot
 * 
 */
final class MethodsAnalyzer extends Analyzer {

    public MethodsAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        for (Method method : getMethods(classModel.getClazz())) {
            classModel.addMethod(getModel(method));
        }
    }

    private List<Method> getMethods(Class<?> clazz) {
        List<Method> list = newArrayList();
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && !method.isSynthetic() && !method.getName().equals("clone") && isMethodWrapped(method)) {
                list.add(method);
            }
        }
        return list;
    }

    public boolean isMethodWrapped(Method method) {
        ClassMapping mapping = mappings.get(method.getDeclaringClass());
        Java4Cpp annotation = method.getDeclaringClass().getAnnotation(Java4Cpp.class);

        if (mapping != null) {
            String name = method.getName() + "(" + Datatype.generateJNISignature(method.getParameterTypes()) + ")";
            if (mapping.isExportAll()) {
                return !mapping.getMethods().getNoWrappes().contains(name);
            }
            return mapping.getMethods().findWrappe(name) != null;
        }
        if (annotation == null || annotation.all()) {
            return !method.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return method.isAnnotationPresent(Java4CppWrappe.class);
    }

    public MethodModel getModel(Method method) {
        MethodModel methodModel = new MethodModel(method.getName());
        methodModel.setCppName(getCppName(method));
        methodModel.setStatic(Modifier.isStatic(method.getModifiers()));

        methodModel.setReturnType(context.getClassModel(method.getReturnType()));
        updateGenericDependency(method.getGenericReturnType());

        for (Class<?> param : method.getParameterTypes()) {
            methodModel.getParameters().add(context.getClassModel(param));
        }
        for (Type type : method.getGenericParameterTypes()) {
            updateGenericDependency(type);
        }
        return methodModel;
    }

    /**
     * Return a valid C++ name for the method {@code method}, by escaping
     * reserved words or by returning the name specified by the mapping or the
     * annotation.
     * 
     * @return a valid C++ method name.
     */
    public String getCppName(Method method) {
        ClassMapping mapping = mappings.get(method.getDeclaringClass());

        if (mapping != null) {
            String name = method.getName() + "(" + Datatype.generateJNISignature(method.getParameterTypes()) + ")";
            Wrappe wrappedMethod = mapping.getMethods().findWrappe(name);
            if (wrappedMethod != null && !Utils.isNullOrEmpty(wrappedMethod.getCppName())) {
                return wrappedMethod.getCppName();
            }
        }
        Java4CppWrappe annot = method.getAnnotation(Java4CppWrappe.class);
        if (annot != null && !Utils.isNullOrEmpty(annot.value())) {
            return annot.value();
        }
        return mappings.escapeName(method.getName());
    }
}