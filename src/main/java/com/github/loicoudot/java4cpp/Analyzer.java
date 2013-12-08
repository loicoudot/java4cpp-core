package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.github.loicoudot.java4cpp.model.ClassModel;

abstract class Analyzer {

    protected final Context context;
    protected final MappingsManager mappings;

    public Analyzer(Context context) {
        this.context = context;
        mappings = context.getMappingsManager();
    }

    public abstract void fill(ClassModel classModel);

    protected ClassModel getParameterized(Type type) {
        if (type instanceof ParameterizedType) {
            return context.getClassModel((ParameterizedType) type);
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (!clazz.isArray()) {
                context.addClassToDo(clazz);
            }
            return context.getClassModel(clazz);
        }
        return context.getClassModel(Object.class);
    }

    protected List<ClassModel> getParameterized(Type[] types) {
        List<ClassModel> result = newArrayList();
        for (Type type : types) {
            result.add(getParameterized(type));
        }
        return result;
    }

}