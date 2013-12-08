package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.configuration.Wrappe;
import com.github.loicoudot.java4cpp.model.ClassModel;
import com.github.loicoudot.java4cpp.model.FieldModel;

/**
 * Data-model builder for class fields
 * 
 * @author Loic Oudot
 * 
 */
final class FieldsAnalyzer extends Analyzer {

    public FieldsAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        for (Field field : getStaticFields(classModel.getClazz())) {
            classModel.addField(getModel(field));
        }
    }

    List<Field> getStaticFields(Class<?> clazz) {
        ArrayList<Field> list = newArrayList();
        for (Field field : clazz.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && isFieldWrapped(field)) {
                list.add(field);
            }
        }
        return list;
    }

    boolean isFieldWrapped(Field field) {
        ClassMapping mapping = mappings.get(field.getDeclaringClass());
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

    FieldModel getModel(Field field) {
        FieldModel fieldModel = new FieldModel(field.getName());
        fieldModel.setCppName(getCppName(field));
        fieldModel.setType(getParameterized(field.getGenericType()));
        return fieldModel;
    }

    /**
     * Return a valid C++ name for the field {@code field}, by escaping reserved
     * words or by returning the name specified by the mapping or the
     * annotation.
     * 
     * @return a valid C++ field name.
     */
    String getCppName(Field field) {
        ClassMapping mapping = mappings.get(field.getDeclaringClass());

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
        return mappings.escapeName(field.getName());
    }
}
