package com.github.loicoudot.java4cpp;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.model.ClassModel;

public class SuperclassAnalyzer extends Analyzer {

    public SuperclassAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        if (exportSuperClass(classModel.getType().getClazz())) {
            classModel.getContent().setSuperclass(context.getClassModel(classModel.getType().getClazz().getSuperclass()));
        }
    }

    boolean exportSuperClass(Class<?> clazz) {
        ClassMapping mapping = mappings.get(clazz);
        Java4Cpp annotation = clazz.getAnnotation(Java4Cpp.class);
        return mapping != null ? mapping.isSuperclass() : annotation != null && annotation.superclass();
    }
}
