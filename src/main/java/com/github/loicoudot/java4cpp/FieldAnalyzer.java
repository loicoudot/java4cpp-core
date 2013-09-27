package com.github.loicoudot.java4cpp;

import java.lang.reflect.Field;

import com.github.loicoudot.java4cpp.model.FieldModel;

public final class FieldAnalyzer {
    private final Field field;
    private final Context context;

    public FieldAnalyzer(Field field, Context context) {
        this.field = field;
        this.context = context;
    }

    public FieldModel getModel() {
        FieldModel fieldModel = new FieldModel(field.getName());
        fieldModel.setType(context.getClassModel(field.getType()));
        fieldModel.setCppName(context.getMappings(field.getDeclaringClass()).getCppName(field));
        // TODO: update generic
        return fieldModel;
    }
}
