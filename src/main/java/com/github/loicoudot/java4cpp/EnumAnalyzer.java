package com.github.loicoudot.java4cpp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.github.loicoudot.java4cpp.model.ClassModel;

public class EnumAnalyzer extends Analyzer {

    public EnumAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        for (String key : getEnumKeys(classModel.getType().getClazz())) {
            classModel.getContent().addEnumKey(key);
        }
    }

    private List<String> getEnumKeys(Class<?> clazz) {
        List<String> enumKeys = new ArrayList<String>();
        for (Field field : clazz.getFields()) {
            if (field.isEnumConstant()) {
                enumKeys.add(context.getMappingsManager().escapeName(field.getName()));
            }
        }
        return enumKeys;
    }
}
