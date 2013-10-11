package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.Arrays;
import java.util.List;

import com.github.loicoudot.java4cpp.configuration.ClassMapping;
import com.github.loicoudot.java4cpp.model.ClassModel;

public class InterfacesAnalyzer extends Analyzer {

    public InterfacesAnalyzer(Context context) {
        super(context);
    }

    @Override
    public void fill(ClassModel classModel) {
        for (Class<?> interfac : getInterfaces(classModel.getClazz())) {
            classModel.addInterface(context.getClassModel(interfac));
        }
    }

    private List<Class<?>> getInterfaces(Class<?> clazz) {
        List<Class<?>> interfaces = newArrayList();
        for (Class<?> interfac : clazz.getInterfaces()) {
            if (interfac != Cloneable.class && isInterfaceWrapped(clazz, interfac)) {
                interfaces.add(interfac);
            }
        }
        return interfaces;
    }

    public boolean isInterfaceWrapped(Class<?> clazz, Class<?> interfac) {
        ClassMapping mapping = mappings.get(clazz);
        Java4Cpp annotation = clazz.getAnnotation(Java4Cpp.class);

        if (mapping != null) {
            if (mapping.isInterfaceAll()) {
                return !mapping.getInterfaces().getNoWrappes().contains(interfac.getName());
            }
            return mapping.getInterfaces().findWrappe(interfac.getName()) != null;
        }
        if (annotation != null) {
            if (annotation.interfaces()) {
                return !Arrays.asList(annotation.noWrappeInterfaces()).contains(interfac);
            }
            return Arrays.asList(annotation.wrappeInterfaces()).contains(interfac);
        }
        return false;
    }
}
