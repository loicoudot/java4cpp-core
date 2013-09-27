package com.github.loicoudot.java4cpp;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.github.loicoudot.java4cpp.model.ConstructorModel;

final class ConstructorAnalyzer {
    private final Constructor<?> constructor;
    private final Context context;

    public ConstructorAnalyzer(Constructor<?> constructor, Context context) {
        this.constructor = constructor;
        this.context = context;
    }

    public ConstructorModel getModel() {
        ConstructorModel constructorModel = new ConstructorModel();
        for (Class<?> param : constructor.getParameterTypes()) {
            constructorModel.getParameters().add(context.getClassModel(param));
        }
        for (Type type : constructor.getGenericParameterTypes()) {
            updateGenericDependency(type);
        }
        return constructorModel;
    }

    private void updateGenericDependency(Type type) {
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
