package com.github.loicoudot.java4cpp;

import java.util.List;

import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * Data-model builder for a {@code Class}
 * 
 * @author Loic Oudot
 * 
 */
final class ClassAnalyzer extends Analyzer {

    public ClassAnalyzer(Context context) {
        super(context);
    }

    /**
     * Fill {@code classModel} data-model bean, with the content of
     * {@code clazz}.
     * 
     * @param classModel
     *            the data-model to fill
     */
    @Override
    public void fill(ClassModel classModel) {

        Class<?> clazz = classModel.getClazz();

        /**
         * FreeMarker function availlable inside templates to add a direct
         * dependency for the class.
         */
        class AddOutterDependency implements TemplateMethodModelEx {

            ClassModel model;

            public AddOutterDependency(ClassModel model) {
                this.model = model;
            }

            @Override
            @SuppressWarnings("rawtypes")
            public Object exec(List arguments) throws TemplateModelException {
                if (arguments.size() != 1) {
                    throw new TemplateModelException("AddOutterDependency need one parameter (a class name or a ClassModel instance).");
                }
                Object dependency = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
                if (dependency instanceof String) {
                    model.getOutterDependencies().add(context.getClassModel((String) dependency));
                } else if (dependency instanceof ClassModel) {
                    ClassModel classModel = (ClassModel) dependency;
                    if (!classModel.getClazz().isPrimitive() && !classModel.getClazz().isArray()) {
                        model.getOutterDependencies().add((ClassModel) dependency);
                    }
                }
                return "";
            }
        }

        /**
         * FreeMarker function availlable inside templates to add an incldue
         * file for the class.
         */
        class AddOutterInclude implements TemplateMethodModelEx {

            ClassModel model;

            public AddOutterInclude(ClassModel model) {
                this.model = model;
            }

            @Override
            @SuppressWarnings("rawtypes")
            public Object exec(List arguments) throws TemplateModelException {
                if (arguments.size() != 1) {
                    throw new TemplateModelException("AddOutterInclude need one parameter.");
                }
                Object dependency = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
                if (dependency instanceof String) {
                    model.getOutterIncludes().add((String) dependency);
                }
                return "";
            }
        }

        StringBuilder fullName = new StringBuilder();
        String shortName = "";
        String sep = "";
        for (String namespace : context.getMappingsManager().getNamespace(clazz)) {
            fullName.append(sep).append(namespace);
            sep = "::";
            shortName = namespace;
        }
        classModel.setCppFullName(fullName.toString());
        classModel.setCppShortName(shortName);
        classModel.setOwner(classModel.isIsInnerClass() ? context.getClassModel(clazz.getDeclaringClass()) : classModel);

        if (clazz.isArray()) {
            classModel.setInnerType(context.getClassModel(clazz.getComponentType()));
        }
        classModel.setJavaSignature(Datatype.getJavaSignature(clazz));
        classModel.setJniSignature(Datatype.getJNISignature(clazz));
        classModel.setJniMethodName(Datatype.getJNIMethodName(clazz));
        classModel.setAddInclude(new AddOutterInclude(classModel));
        classModel.setAddDependency(new AddOutterDependency(classModel));
        TypeTemplates typeTemplates = context.getTemplateManager().getTypeTemplates(clazz);
        classModel.setCppType(typeTemplates.getCppType(classModel));
        classModel.setCppReturnType(typeTemplates.getCppReturnType(classModel));
        typeTemplates.executeDependencies(classModel);
        classModel.setFunctions(typeTemplates.getFunctions(classModel));
    }
}
