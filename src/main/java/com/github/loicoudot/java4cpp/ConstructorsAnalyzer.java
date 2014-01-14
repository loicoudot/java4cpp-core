package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Constructor;
import java.util.List;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.model.ClassModel;
import com.github.loicoudot.java4cpp.model.ConstructorModel;

/**
 * Data-model builder for class constructors
 * 
 * @author Loic Oudot
 * 
 */
final class ConstructorsAnalyzer extends Analyzer {

    public ConstructorsAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        for (Constructor<?> constructor : getConstructors(classModel.getType().getClazz())) {
            classModel.getContent().addConstructor(getModel(constructor));
        }
    }

    private List<Constructor<?>> getConstructors(Class<?> clazz) {
        List<Constructor<?>> list = newArrayList();
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (isConstructorWrapped(constructor)) {
                list.add(constructor);
            }
        }
        return list;
    }

    boolean isConstructorWrapped(Constructor<?> constructor) {
        ClassMapping mapping = mappings.get(constructor.getDeclaringClass());
        Java4Cpp annotation = constructor.getDeclaringClass().getAnnotation(Java4Cpp.class);

        if (mapping != null) {
            String name = Datatype.generateJNISignature(constructor.getParameterTypes());
            if (mapping.isExportAll()) {
                return !mapping.getConstructors().getNoWrappes().contains(name);
            }
            return mapping.getConstructors().findWrappe(name) != null;
        }
        if (annotation == null || annotation.all()) {
            return !constructor.isAnnotationPresent(Java4CppNoWrappe.class);
        }
        return constructor.isAnnotationPresent(Java4CppWrappe.class);
    }

    private ConstructorModel getModel(Constructor<?> constructor) {
        context.getFileManager().enter("constructor: " + constructor);
        ConstructorModel constructorModel = new ConstructorModel();
        constructorModel.getParameters().addAll(context.getClassesModels(constructor.getParameterTypes()));
        context.getFileManager().leave();
        return constructorModel;
    }
}
