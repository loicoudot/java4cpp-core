package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import com.github.loicoudot.java4cpp.configuration.Clazz;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

/**
 * Helper class regrouping informations from the mapping files and annotated
 * classes. Precedence orders are :
 * <ol>
 * <li>mapping file</li>
 * <li>annotation</li>
 * <li>default value</li>
 * </ol>
 * 
 * @author Loic Oudot
 * 
 */
final class MappingsHelper {

    private final Class<?> clazz;
    private final Context context;
    private final Java4Cpp annotation;
    private final Clazz mapping;

    public MappingsHelper(Class<?> clazz, Context context) {
        this.clazz = clazz;
        this.context = context;
        annotation = clazz.getAnnotation(Java4Cpp.class);
        mapping = context.getClazz(clazz);
    }

    public boolean exportSuperClass() {
        return mapping != null ? mapping.isSuperclass() : annotation != null && annotation.superclass();
    }

    public boolean isInterfaceWrapped(Class<?> interfac) {
        if (mapping != null) {
            if (mapping.isInterfaceAll()) {
                return !mapping.getInterfaces().getNoWrappes().contains(interfac.getName());
            }
            return mapping.getInterfaces().findWrappe(interfac.getName()) != null;
        }
        if (annotation != null) {
            if (annotation.interfaces()) {
                return !Arrays.asList(annotation.noWrappeInterfaces()).contains(interfac);
            }
            return Arrays.asList(annotation.wrappeInterfaces()).contains(interfac);
        }
        return false;
    }

    public boolean isInnerClassWrapped(Class<?> innerClass) {
        if (mapping != null) {
            String name = innerClass.getName().substring(innerClass.getName().indexOf('$') + 1);
            if (mapping.isExportAll()) {
                return !mapping.getInnerClasses().getNoWrappes().contains(name);
            }
            return mapping.getInnerClasses().findWrappe(name) != null;
        }
        if (annotation == null || annotation.all()) {
            return !innerClass.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return innerClass.isAnnotationPresent(Java4CppWrappe.class);
    }

    public boolean isFieldWrapped(Field field) {
        if (mapping != null) {
            if (mapping.isExportFields()) {
                return !mapping.getStaticFields().getNoWrappes().contains(field.getName());
            }
            return mapping.getStaticFields().findWrappe(field.getName()) != null;
        }
        if (annotation != null && annotation.staticFields()) {
            return !field.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return field.isAnnotationPresent(Java4CppWrappe.class);
    }

    public boolean isConstructorWrapped(Constructor<?> constructor) {
        if (mapping != null) {
            String name = generateJavaSignature(constructor.getParameterTypes());
            if (mapping.isExportAll()) {
                return !mapping.getConstructors().getNoWrappes().contains(name);
            }
            return mapping.getConstructors().findWrappe(name) != null;
        }
        if (annotation == null || annotation.all()) {
            return !constructor.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return constructor.isAnnotationPresent(Java4CppWrappe.class);
    }

    public boolean isMethodWrapped(Method method) {
        if (mapping != null) {
            String name = method.getName() + "(" + generateJavaSignature(method.getParameterTypes()) + ")";
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

    /**
     * Return a valid C++ name for the class, by escaping reserved words or by
     * returning the name specified by the mapping or the annotation.
     * 
     * @return a valid C++ class name.
     */
    public String getCppName() {
        if (mapping != null) {
            if (!Utils.isNullOrEmpty(mapping.getCppName())) {
                return mapping.getCppName();
            }
        }
        if (annotation != null && !Utils.isNullOrEmpty(annotation.name())) {
            return annotation.name();
        }
        return context.escapeName(clazz.getSimpleName());
    }

    /**
     * Return a valid C++ name for the field {@code field}, by escaping reserved
     * words or by returning the name specified by the mapping or the
     * annotation.
     * 
     * @return a valid C++ field name.
     */
    public String getCppName(Field field) {
        if (mapping != null) {
            Wrappe wrappedField = mapping.getStaticFields().findWrappe(field.getName());
            if (wrappedField != null && !Utils.isNullOrEmpty(wrappedField.getCppName())) {
                return wrappedField.getCppName();
            }
        }
        Java4CppWrappe annot = field.getAnnotation(Java4CppWrappe.class);
        if (annot != null && !Utils.isNullOrEmpty(annot.value())) {
            return annot.value();
        }
        return context.escapeName(field.getName());
    }

    /**
     * Return a valid C++ name for the method {@code method}, by escaping
     * reserved words or by returning the name specified by the mapping or the
     * annotation.
     * 
     * @return a valid C++ method name.
     */
    public String getCppName(Method method) {
        if (mapping != null) {
            String name = method.getName() + "(" + generateJavaSignature(method.getParameterTypes()) + ")";
            Wrappe wrappedMethod = mapping.getMethods().findWrappe(name);
            if (wrappedMethod != null && !Utils.isNullOrEmpty(wrappedMethod.getCppName())) {
                return wrappedMethod.getCppName();
            }
        }
        Java4CppWrappe annot = method.getAnnotation(Java4CppWrappe.class);
        if (annot != null && !Utils.isNullOrEmpty(annot.value())) {
            return annot.value();
        }
        return context.escapeName(method.getName());
    }

    /**
     * Return the final full qualified C++ name of the class. Apply the
     * namespace/package mapping, the class name mapping and escape all part by
     * the reserved words list. Works also for inner class.
     * 
     * @return the final full qualified C++ name.
     */
    public List<String> getNamespaces() {
        List<String> namespace;
        if (clazz.getEnclosingClass() == null) {
            namespace = context.getNamespaceForClass(clazz);
        } else {
            Class<?> enclosing = clazz;
            Deque<Class<?>> stack = new ArrayDeque<Class<?>>();
            while (enclosing.getEnclosingClass() != null) {
                stack.add(enclosing);
                enclosing = enclosing.getEnclosingClass();
            }
            namespace = context.getMappings(enclosing).getNamespaces();
            while (!stack.isEmpty()) {
                namespace.add(context.getMappings(stack.pollLast()).getCppName());
            }
        }
        List<String> escapedNamespace = newArrayList();
        for (String name : namespace) {
            escapedNamespace.add(context.escapeName(name));
        }
        escapedNamespace.set(escapedNamespace.size() - 1, getCppName());
        return escapedNamespace;
    }

    /**
     * Construct a JNI signature string from a series of parameters.
     * 
     * @param params
     *            a list of paramters
     * @return a {@code String} containing the corresponding JNI signature.
     */
    private String generateJavaSignature(Class<?>[] params) {
        StringBuilder ret = new StringBuilder();

        for (Class<?> param : params) {
            ret.append(Datatype.getJavaSignature(param));
        }

        return ret.toString();
    }
}
