package com.github.loicoudot.java4cpp;

import java.lang.reflect.Field;

import com.github.loicoudot.java4cpp.model.FieldModel;

public final class FieldAnalyzer extends Analyzer {
    private final Field field;

    public FieldAnalyzer(Field field, Context context) {
        super(context);
        this.field = field;
    }

    public FieldModel getModel() {
        FieldModel fieldModel = new FieldModel(field.getName());
        fieldModel.setType(context.getClassModel(field.getType()));
        fieldModel.setCppName(context.getMappings(field.getDeclaringClass()).getCppName(field));
        updateGenericDependency(field.getGenericType());
        return fieldModel;
    }
}
