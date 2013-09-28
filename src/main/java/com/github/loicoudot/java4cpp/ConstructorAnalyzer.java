package com.github.loicoudot.java4cpp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import com.github.loicoudot.java4cpp.model.ConstructorModel;

final class ConstructorAnalyzer extends Analyzer {
    private final Constructor<?> constructor;

    public ConstructorAnalyzer(Constructor<?> constructor, Context context) {
        super(context);
        this.constructor = constructor;
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
}
