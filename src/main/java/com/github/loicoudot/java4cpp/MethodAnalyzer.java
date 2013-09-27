package com.github.loicoudot.java4cpp;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.github.loicoudot.java4cpp.model.MethodModel;

public final class MethodAnalyzer {
    private final Method method;
    private final Context context;

    public MethodAnalyzer(Method method, Context context) {
        this.context = context;
        this.method = method;
    }

    public MethodModel getModel() {
        MethodModel methodModel = new MethodModel(method.getName());
        methodModel.setCppName(context.getMappings(method.getDeclaringClass()).getCppName(method));
        methodModel.setStatic(Modifier.isStatic(method.getModifiers()));

        methodModel.setReturnType(context.getClassModel(method.getReturnType()));
        updateGenericDependency(method.getGenericReturnType());

        for (Class<?> param : method.getParameterTypes()) {
            methodModel.getParameters().add(context.getClassModel(param));
        }
        for (Type type : method.getGenericParameterTypes()) {
            updateGenericDependency(type);
        }
        return methodModel;
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
