package com.github.loicoudot.java4cpp;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Analyzer {

    protected final Context context;

    public Analyzer(Context context) {
        this.context = context;
    }

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