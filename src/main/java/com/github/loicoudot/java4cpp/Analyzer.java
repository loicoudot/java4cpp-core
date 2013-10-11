package com.github.loicoudot.java4cpp;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.github.loicoudot.java4cpp.model.ClassModel;

abstract class Analyzer {

    protected final Context context;
    protected final MappingsManager mappings;

    public Analyzer(Context context) {
        this.context = context;
        mappings = context.getMappingsManager();
    }

    public abstract void fill(ClassModel classModel);

    /**
     * If {@code type} is a parametrized type, add the parametrized type to the
     * list of class to process. For exemple, if {@code type} is
     * {@code List<String>}, then {@code String} is added to the system.
     * 
     * @param type
     *            a type
     */
    protected void updateGenericDependency(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parametrizedType = (ParameterizedType) type;
            Type[] typeArguments = parametrizedType.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                if (typeArgument instanceof Class) {
                    context.addClassToDo((Class<?>) typeArgument);
                }
            }
        }
    }

}