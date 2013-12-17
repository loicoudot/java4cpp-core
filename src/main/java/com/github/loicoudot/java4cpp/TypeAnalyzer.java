package com.github.loicoudot.java4cpp;

import java.util.Collection;
import java.util.List;

import com.github.loicoudot.java4cpp.model.ClassModel;
import com.github.loicoudot.java4cpp.model.ClassType;

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
final class TypeAnalyzer extends Analyzer {

    public TypeAnalyzer(Context context) {
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

        /**
         * FreeMarker function availlable inside templates to add dependencies
         * for the class.
         */
        class AddDependencies implements TemplateMethodModelEx {

            ClassType model;

            public AddDependencies(ClassType model) {
                this.model = model;
            }

            @Override
            @SuppressWarnings("rawtypes")
            public Object exec(List arguments) throws TemplateModelException {
                if (arguments.size() < 1) {
                    throw new TemplateModelException("addDependencies need at least one parameter.");
                }
                for (Object argument : arguments) {
                    Object dependency = DeepUnwrap.unwrap((TemplateModel) argument);
                    if (dependency instanceof String) {
                        addDependency(context.getClassModel((String) dependency));
                    } else if (dependency instanceof ClassModel) {
                        addDependency((ClassModel) dependency);
                    } else if (dependency instanceof Collection) {
                        for (Object depend : (Collection) dependency) {
                            addDependency((ClassModel) depend);
                        }
                    } else if (dependency != null) {
                        throw new TemplateModelException(
                                "AddDependencies arguments must be a a class name, a ClassModel instance or a Collection of ClassModels.");
                    }
                }
                return "";
            }

            private void addDependency(ClassModel classModel) {
                model.getDependencies().add(classModel);
            }
        }

        /**
         * FreeMarker function availlable inside templates to add includes file
         * for the class.
         */
        class AddIncludes implements TemplateMethodModelEx {

            ClassType model;

            public AddIncludes(ClassType model) {
                this.model = model;
            }

            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public Object exec(List arguments) throws TemplateModelException {
                if (arguments.size() < 1) {
                    throw new TemplateModelException("addIncludes need at least one parameter.");
                }
                for (Object argument : arguments) {
                    Object include = DeepUnwrap.unwrap((TemplateModel) argument);
                    if (include instanceof String) {
                        model.getIncludes().add((String) include);
                    } else if (include instanceof Collection) {
                        model.getIncludes().addAll((Collection) include);
                    } else if (include != null) {
                        throw new TemplateModelException("addIncludes arguments must be a String or a Collection of Strings.");
                    }
                }
                return "";
            }
        }

        ClassType typeModel = classModel.getType();
        Class<?> clazz = typeModel.getClazz();

        StringBuilder fullName = new StringBuilder();
        String shortName = "";
        String sep = "";
        for (String namespace : context.getMappingsManager().getNamespace(clazz)) {
            fullName.append(sep).append(namespace);
            sep = "::";
            shortName = namespace;
        }
        typeModel.setCppFullName(fullName.toString());
        typeModel.setCppShortName(shortName);

        typeModel.setOwner(typeModel.isIsInnerClass() ? context.getClassModel(clazz.getDeclaringClass()) : classModel);
        if (clazz.isArray()) {
            typeModel.setInnerType(context.getClassModel(clazz.getComponentType()));
        }

        typeModel.setJavaSignature(Datatype.getJavaSignature(clazz));
        typeModel.setJniSignature(Datatype.getJNISignature(clazz));
        typeModel.setJniMethodName(Datatype.getJNIMethodName(clazz));
        typeModel.setAddIncludes(new AddIncludes(typeModel));
        typeModel.setAddDependencies(new AddDependencies(typeModel));
        TypeTemplates typeTemplates = context.getTemplateManager().getTypeTemplates(clazz);
        typeModel.setCppType(typeTemplates.getCppType(classModel));
        typeModel.setCppReturnType(typeTemplates.getCppReturnType(classModel));
        typeTemplates.executeDependencies(classModel);
        typeModel.setFunctions(typeTemplates.getFunctions(classModel));
    }
}
