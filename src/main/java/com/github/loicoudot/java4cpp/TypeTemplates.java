package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.loicoudot.java4cpp.model.ClassModel;

import freemarker.template.Template;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public final class TypeTemplates {
    private final Logger log = LoggerFactory.getLogger(TypeTemplates.class);
    private Template cppType;
    private Template cppReturnType;
    private Map<String, Template> functions = newHashMap();

    public class TemplateFunction implements TemplateMethodModelEx {

        private final Template template;
        private ClassModel model;

        public TemplateFunction(Template template, ClassModel model) {
            this.template = template;
            this.model = model;
        }

        public TemplateFunction(Template template) {
            this.template = template;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Object exec(List arguments) throws TemplateModelException {
            if (template != null) {
                for (int i = 0; i < arguments.size(); ++i) {
                    model.getFunctions().put("arg" + (i + 1), arguments.get(i).toString());
                }
                return processTemplate(template, model);
            }
            return "";
        }
    }

    private String processTemplate(Template template, ClassModel classModel) {
        if (template != null) {
            StringWriter sw = new StringWriter();
            try {
                template.process(classModel, sw);
            } catch (Exception e) {
                log.error("error processing template:", e);
            }
            return sw.toString();
        }
        return "";
    }

    public String getCppType(ClassModel classModel) {
        return processTemplate(cppType, classModel);
    }

    public void setCppType(Template cppType) {
        this.cppType = cppType;
    }

    public String getCppReturnType(ClassModel classModel) {
        String type = processTemplate(cppReturnType, classModel);
        if (!type.isEmpty()) {
            return type;
        }
        return getCppType(classModel);
    }

    public void setCppReturnType(Template cppReturnType) {
        this.cppReturnType = cppReturnType;
    }

    public HashMap<String, Object> getFunctions(ClassModel classModel) {
        HashMap<String, Object> result = newHashMap();
        for (String name : functions.keySet()) {
            result.put(name, new TemplateFunction(functions.get(name), classModel));
        }
        return result;
    }

    public void setFunctions(Map<String, Template> functions) {
        this.functions = functions;
    }
}
