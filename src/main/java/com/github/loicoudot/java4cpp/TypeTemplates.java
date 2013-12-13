package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.Template;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * Ths class use freemarker templates and <code>ClassModel</code> models to
 * generate the C++ source code for type mapings. <br>
 * Gives the C++ type of a corresponding java class, the dependencies with other
 * classes and C++ source code to convert back and forth between java classes
 * and C++ objects.
 * 
 * @author Loic Oudot
 */
final class TypeTemplates {
    private boolean needAnalyzing;
    private Template cppType;
    private Template cppReturnType;
    private Template dependencies;
    private Map<String, Template> functions = newHashMap();

    /**
     * When defining datatypes templates, it's possible to add any numbers of
     * user's functions to make bridge between source code template generation
     * and datatype definition.
     */
    public class TemplateFunction implements TemplateMethodModelEx {

        private final Template template;
        private ClassModel classModel;
        private final ThreadLocal<Map<String, Object>> model = new ThreadLocal<Map<String, Object>>();

        public TemplateFunction(Template template, ClassModel classModel) {
            this.template = template;
            this.classModel = classModel;
        }

        public TemplateFunction(Template template) {
            this.template = template;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Object exec(List arguments) throws TemplateModelException {
            if (template != null) {
                model.set(new HashMap<String, Object>());
                model.get().put("class", classModel);
                for (int i = 0; i < arguments.size(); ++i) {
                    model.get().put("arg" + (i + 1), arguments.get(i).toString());
                }
                return processTemplate(template, model.get());
            }
            return "";
        }
    }

    /**
     * Process a freemarker templates <code>template</code> with the model
     * <code>classModel</code> and return the resulting strings. At this stage
     * some informations of the class are updated in the <code>classModel</code>
     * and these informations can be used inside the freemarker
     * <code>template</code> to deduce the C++ source code. <br>
     * Templates exemple :<br>
     * <code>"std::vector<${innerType.cppReturnType} >"</code><br>
     * <code>"${addInclude("&lt;vector>")}"</code>
     * 
     * @param template
     *            a freemarker template for generating parts of C++ source code
     * @param model
     *            a semi-filled <code>ClassModel</code>
     * @return the freemarker template processing results
     */
    private String processTemplate(Template template, Object model) {
        if (template != null) {
            StringWriter sw = new StringWriter();
            try {
                template.process(model, sw);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process template " + e.getMessage());
            }
            return sw.toString();
        }
        return "";
    }

    /**
     * Return true if the java class need to be fully analyzed in the
     * ClassModel. For exemple, if <code>java.lang.String</code> is mapped to
     * <code>std::string</code>, then it is not necessary to analyse
     * constructor, methods etc.
     * 
     * @return false to disable class analysis
     */
    public boolean isNeedAnalyzing() {
        return needAnalyzing;
    }

    public void setNeedAnalyzing(boolean needAnalyzing) {
        this.needAnalyzing = needAnalyzing;
    }

    /**
     * Process the <code>cppType</code> template with the model
     * <code>classModel</class> to obtain the C++ equivalent type.
     * 
     * @param classModel
     *            to model to use when processing the templates
     * @return the template processing results
     */
    public String getCppType(ClassModel classModel) {
        return processTemplate(cppType, newHashMap("class", classModel));
    }

    /**
     * Sets the template that define the C++ equivalent type.
     * 
     * @param cppType
     *            a freemarker templates
     */
    public void setCppType(Template cppType) {
        this.cppType = cppType;
    }

    /**
     * Process the <code>cppReturnType</code> template with the model
     * <code>classModel</class> to obtain the C++ equivalent type to use 
     * as return type of functions and fields.
     * 
     * @param classModel
     *            to model to use when processing the templates
     * @return the template processing results
     */
    public String getCppReturnType(ClassModel classModel) {
        String type = processTemplate(cppReturnType, newHashMap("class", classModel));
        if (!type.isEmpty()) {
            return type;
        }
        return getCppType(classModel);
    }

    /**
     * Sets the template that define the C++ equivalent return type of methods
     * and fields.
     * 
     * @param cppReturnType
     *            a freemarker templates
     */
    public void setCppReturnType(Template cppReturnType) {
        this.cppReturnType = cppReturnType;
    }

    /**
     * Process the <code>dependencies</code> template with the model
     * <code>classModel</class> to add inside necessary includes and dependencies.
     * 
     * @param classModel
     *            to model to use when processing the templates
     */
    public void executeDependencies(ClassModel classModel) {
        if (dependencies != null) {
            processTemplate(dependencies, newHashMap("class", classModel));
        }
    }

    /**
     * Sets the template that define the C++ dependencies needed for this type.
     * 
     * @param dependencies
     *            a freemarker templates
     */
    public void setDependencies(Template dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Return a freemarker model containing the user defined functions.
     * 
     * @param classModel
     *            refenrece a model that will be used when evaluating these
     *            funstions
     * @return model for user defined functions
     */
    public HashMap<String, Object> getFunctions(ClassModel classModel) {
        HashMap<String, Object> result = newHashMap();
        for (String name : functions.keySet()) {
            result.put(name, new TemplateFunction(functions.get(name), classModel));
        }
        return result;
    }

    /**
     * Sets the templates that define the user defined functions. It's a map of
     * function names and associated freemarker templates
     * 
     * @param functions
     *            a map of funtions name and freemarker templates
     */
    public void setFunctions(Map<String, Template> functions) {
        this.functions = functions;
    }
}
