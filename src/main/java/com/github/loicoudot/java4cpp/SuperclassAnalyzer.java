package com.github.loicoudot.java4cpp;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.model.ClassModel;

public class SuperclassAnalyzer extends Analyzer {

    public SuperclassAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        final Class<?> clazz = classModel.getType().getClazz();
        if (clazz.getSuperclass() != null && exportSuperClass(clazz)) {
            classModel.getContent().setSuperclass(context.getClassModel(clazz.getSuperclass()));
        }
    }

    boolean exportSuperClass(Class<?> clazz) {
        ClassMapping mapping = mappings.get(clazz);
        Java4Cpp annotation = clazz.getAnnotation(Java4Cpp.class);
        return mapping != null ? mapping.isSuperclass() : annotation != null && annotation.superclass();
    }
}
