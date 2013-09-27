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

public final class MappingsHelper {

    private final Class<?> clazz;
    private final Context context;
    private final Java4Cpp annotation;
    private final Clazz settings;

    public MappingsHelper(Class<?> clazz, Context context) {
        this.clazz = clazz;
        this.context = context;
        annotation = clazz.getAnnotation(Java4Cpp.class);
        settings = context.getClazz(clazz);
    }

    public boolean exportSuperClass() {
        return settings != null ? settings.isSuperclass() : annotation != null && annotation.superclass();
    }

    public boolean isInterfaceWrapped(Class<?> interfac) {
        if (settings != null) {
            if (settings.isInterfaceAll()) {
                return !settings.getInterfaces().getNoWrappes().contains(interfac.getName());
            }
            return settings.getInterfaces().findWrappe(interfac.getName()) != null;
        }
        if (annotation != null) {
            if (annotation.interfaces()) {
                return !Arrays.asList(annotation.noWrappeInterfaces()).contains(interfac);
            }
            return Arrays.asList(annotation.wrappeInterfaces()).contains(interfac);
        }
        return false;
    }

    public boolean isInnerClassWrapped(Class<?> clazz) {
        if (settings != null) {
            // TODO: what about inner inner class ?
            // TODO: Java4Cpp aussi pour les inners
            String name = clazz.getName().substring(clazz.getName().indexOf('$') + 1);
            if (settings.isExportAll()) {
                return !settings.getInnerClasses().getNoWrappes().contains(name);
            }
            return settings.getInnerClasses().findWrappe(name) != null;
        }
        if (annotation == null || annotation.all()) {
            return !clazz.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return clazz.isAnnotationPresent(Java4CppWrappe.class);
    }

    public boolean isFieldWrapped(Field field) {
        if (settings != null) {
            if (settings.isExportFields()) {
                return !settings.getStaticFields().getNoWrappes().contains(field.getName());
            }
            return settings.getStaticFields().findWrappe(field.getName()) != null;
        }
        if (annotation != null && annotation.staticFields()) {
            return !field.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return field.isAnnotationPresent(Java4CppWrappe.class);
    }

    public boolean isConstructorWrapped(Constructor<?> constructor) {
        if (settings != null) {
            String name = generateJavaSignature(constructor.getParameterTypes());
            if (settings.isExportAll()) {
                return !settings.getConstructors().getNoWrappes().contains(name);
            }
            return settings.getConstructors().findWrappe(name) != null;
        }
        if (annotation == null || annotation.all()) {
            return !constructor.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return constructor.isAnnotationPresent(Java4CppWrappe.class);
    }

    public boolean isMethodWrapped(Method method) {
        if (settings != null) {
            String name = method.getName() + "(" + generateJavaSignature(method.getParameterTypes()) + ")";
            if (settings.isExportAll()) {
                return !settings.getMethods().getNoWrappes().contains(name);
            }
            return settings.getMethods().findWrappe(name) != null;
        }
        if (annotation == null || annotation.all()) {
            return !method.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return method.isAnnotationPresent(Java4CppWrappe.class);
    }

    public String getCppName() {
        if (settings != null) {
            if (!Utils.isNullOrEmpty(settings.getCppName())) {
                return settings.getCppName();
            }
        }
        if (annotation != null && !Utils.isNullOrEmpty(annotation.name())) {
            return annotation.name();
        }
        return context.escapeName(clazz.getSimpleName());
    }

    public String getCppName(Field field) {
        if (settings != null) {
            Wrappe wrappedField = settings.getStaticFields().findWrappe(field.getName());
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

    public String getCppName(Method method) {
        if (settings != null) {
            String name = method.getName() + "(" + generateJavaSignature(method.getParameterTypes()) + ")";
            Wrappe wrappedMethod = settings.getMethods().findWrappe(name);
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

    public String[] getNamespaces() {
        String namespace;
        if (clazz.getEnclosingClass() == null) {
            namespace = context.getNamespaceForClass(clazz);
        } else {
            // TODO: simplify
            Class<?> conteneur = clazz;
            Deque<Class<?>> stack = new ArrayDeque<Class<?>>();
            while (conteneur.getEnclosingClass() != null) {
                stack.add(conteneur);
                conteneur = conteneur.getEnclosingClass();
            }
            namespace = Joiner.on("::").join(context.getMappings(conteneur).getNamespaces());
            while (!stack.isEmpty()) {
                namespace += "::" + context.getMappings(stack.pollLast()).getCppName();
            }
        }
        List<String> escapedNamespace = newArrayList();
        for (String name : namespace.split("::")) {
            escapedNamespace.add(context.escapeName(name));
        }
        escapedNamespace.set(escapedNamespace.size() - 1, getCppName());
        return escapedNamespace.toArray(new String[escapedNamespace.size()]);
    }

    private String generateJavaSignature(Class<?>[] params) {
        StringBuilder ret = new StringBuilder();

        for (Class<?> param : params) {
            ret.append(Datatype.getJavaSignature(param));
        }

        return ret.toString();
    }
}
