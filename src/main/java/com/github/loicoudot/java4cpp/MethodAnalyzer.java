package com.github.loicoudot.java4cpp;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.github.loicoudot.java4cpp.model.MethodModel;

/**
 * Data-model builder for a {@code Method}
 * 
 * @author Loic Oudot
 * 
 */
final class MethodAnalyzer extends Analyzer {
    private final Method method;

    public MethodAnalyzer(Method method, Context context) {
        super(context);
        this.method = method;
    }

    public MethodModel getModel() {
        MethodModel methodModel = new MethodModel(method.getName());
        methodModel.setCppName(context.getMappingsManager().getMappings(method.getDeclaringClass()).getCppName(method));
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
}
