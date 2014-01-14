package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Modifier;
import java.util.List;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.model.ClassModel;

public class InnerClassAnalyzer extends Analyzer {

    public InnerClassAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        for (Class<?> nestedClass : getNestedClasses(classModel.getType().getClazz())) {
            classModel.getContent().addNestedClass(context.getClassModel(nestedClass));
        }
    }

    private List<Class<?>> getNestedClasses(Class<?> clazz) {
        List<Class<?>> list = newArrayList();
        for (Class<?> nested : clazz.getDeclaredClasses()) {
            if (Modifier.isPublic(nested.getModifiers()) && isInnerClassWrapped(clazz, nested)) {
                list.add(nested);
            }
        }
        return list;
    }

    boolean isInnerClassWrapped(Class<?> clazz, Class<?> innerClass) {
        ClassMapping mapping = mappings.get(clazz);
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
}
