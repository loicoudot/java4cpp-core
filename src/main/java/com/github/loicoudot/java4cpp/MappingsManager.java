package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Namespace;
import com.github.loicoudot.java4cpp.configuration.Wrappe;

public class MappingsManager {

    private final Context context;
    private final Mappings mappings = new Mappings();
    private final Map<Class<?>, ClassMapping> mappingCache = newHashMap();

    public MappingsManager(Context context) {
        this.context = context;
    }

    public void start() {
        addMappingsFromSettings();
        addClassToDoFromMappings();
        for (ClassMapping mapping : mappings.getClasses()) {
            mappingCache.put(mapping.getClazz(), mapping);
        }
    }

    private void addMappingsFromSettings() {
        if (!Utils.isNullOrEmpty(context.getSettings().getMappingsFile())) {
            for (String name : context.getSettings().getMappingsFile().split(";")) {
                try {
                    InputStream is = Utils.getFileOrResource(name);
                    Mappings mapping = JAXB.unmarshal(is, Mappings.class);
                    is.close();
                    addMappings(mapping);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read mappings from settings " + e.getMessage());
                }
            }
        }
    }

    private void addClassToDoFromMappings() {
        context.getFileManager().logInfo("adding classes to wrappe from mappings files");
        for (ClassMapping mapping : mappings.getClasses()) {
            context.addClassToDo(mapping.getClazz());
        }
    }

    /**
     * Add a mappings configuration bean to the actual context.
     * 
     * @param other
     *            the mappings bean to add
     */
    public void addMappings(Mappings other) {
        mappings.getKeywords().addAll(other.getKeywords());
        mappings.getClasses().addAll(other.getClasses());
        mappings.getNamespaces().addAll(other.getNamespaces());
    }

    public String escapeName(String name) {
        if (mappings.getKeywords().contains(name)) {
            return escapeName(name + '_');
        }
        return name;
    }

    /**
     * Transform the full qualified name of {@code clazz} by applying the rules
     * on namespace/package mappings. The mappings on the class name is not
     * applied here. Does not work on inner class.
     * 
     * @param clazz
     *            the class to get namespace from.
     * @return the associate namespace associate to {@code clazz}.
     */
    private List<String> getNamespaceMapping(Class<?> clazz) {
        int bestScore = 0;
        String bestNamespace = clazz.getName().replaceAll("\\.", "::");
        for (Namespace namespace : mappings.getNamespaces()) {
            if (namespace.getJavaPackage().length() > bestScore && clazz.getName().matches(namespace.getJavaPackage())) {
                bestScore = namespace.getJavaPackage().length();
                bestNamespace = Utils.isNullOrEmpty(namespace.getNamespace()) ? clazz.getSimpleName() : String.format("%s::%s", namespace.getNamespace(),
                        clazz.getSimpleName());
            }
        }
        return Arrays.asList(bestNamespace.split("::"));
    }

    /**
     * Return the final full qualified C++ name of the class. Apply the
     * namespace/package mapping, the class name mapping and escape all part by
     * the reserved words list. Works also for inner class.
     * 
     * @return the final full qualified C++ name.
     */
    public List<String> getNamespace(Class<?> clazz) {
        List<String> namespace;
        if (clazz.getEnclosingClass() == null) {
            namespace = getNamespaceMapping(clazz);
        } else {
            Class<?> enclosing = clazz;
            Deque<Class<?>> stack = new ArrayDeque<Class<?>>();
            while (enclosing.getEnclosingClass() != null) {
                stack.add(enclosing);
                enclosing = enclosing.getEnclosingClass();
            }
            namespace = getNamespace(enclosing);
            while (!stack.isEmpty()) {
                namespace.add(getCppName(stack.pollLast()));
            }
        }
        List<String> escapedNamespace = newArrayList();
        for (String name : namespace) {
            escapedNamespace.add(escapeName(name));
        }
        escapedNamespace.set(escapedNamespace.size() - 1, getCppName(clazz));
        return escapedNamespace;
    }

    public boolean exportSuperClass(Class<?> clazz) {
        ClassMapping mapping = mappingCache.get(clazz);
        Java4Cpp annotation = clazz.getAnnotation(Java4Cpp.class);
        return mapping != null ? mapping.isSuperclass() : annotation != null && annotation.superclass();
    }

    public boolean isInterfaceWrapped(Class<?> clazz, Class<?> interfac) {
        ClassMapping mapping = mappingCache.get(clazz);
        Java4Cpp annotation = clazz.getAnnotation(Java4Cpp.class);

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

    public boolean isInnerClassWrapped(Class<?> clazz, Class<?> innerClass) {
        ClassMapping mapping = mappingCache.get(clazz);
        Java4Cpp annotation = clazz.getAnnotation(Java4Cpp.class);

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
        ClassMapping mapping = mappingCache.get(field.getDeclaringClass());
        Java4Cpp annotation = field.getDeclaringClass().getAnnotation(Java4Cpp.class);

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
        ClassMapping mapping = mappingCache.get(constructor.getDeclaringClass());
        Java4Cpp annotation = constructor.getDeclaringClass().getAnnotation(Java4Cpp.class);

        if (mapping != null) {
            String name = Utils.generateJNISignature(constructor.getParameterTypes());
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
        ClassMapping mapping = mappingCache.get(method.getDeclaringClass());
        Java4Cpp annotation = method.getDeclaringClass().getAnnotation(Java4Cpp.class);

        if (mapping != null) {
            String name = method.getName() + "(" + Utils.generateJNISignature(method.getParameterTypes()) + ")";
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
    public String getCppName(Class<?> clazz) {
        ClassMapping mapping = mappingCache.get(clazz);
        Java4Cpp annotation = clazz.getAnnotation(Java4Cpp.class);

        if (mapping != null) {
            if (!Utils.isNullOrEmpty(mapping.getCppName())) {
                return mapping.getCppName();
            }
        }
        if (annotation != null && !Utils.isNullOrEmpty(annotation.name())) {
            return annotation.name();
        }
        return escapeName(clazz.getSimpleName());
    }

    /**
     * Return a valid C++ name for the field {@code field}, by escaping reserved
     * words or by returning the name specified by the mapping or the
     * annotation.
     * 
     * @return a valid C++ field name.
     */
    public String getCppName(Field field) {
        ClassMapping mapping = mappingCache.get(field.getDeclaringClass());

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
        return escapeName(field.getName());
    }

    /**
     * Return a valid C++ name for the method {@code method}, by escaping
     * reserved words or by returning the name specified by the mapping or the
     * annotation.
     * 
     * @return a valid C++ method name.
     */
    public String getCppName(Method method) {
        ClassMapping mapping = mappingCache.get(method.getDeclaringClass());

        if (mapping != null) {
            String name = method.getName() + "(" + Utils.generateJNISignature(method.getParameterTypes()) + ")";
            Wrappe wrappedMethod = mapping.getMethods().findWrappe(name);
            if (wrappedMethod != null && !Utils.isNullOrEmpty(wrappedMethod.getCppName())) {
                return wrappedMethod.getCppName();
            }
        }
        Java4CppWrappe annot = method.getAnnotation(Java4CppWrappe.class);
        if (annot != null && !Utils.isNullOrEmpty(annot.value())) {
            return annot.value();
        }
        return escapeName(method.getName());
    }
}
