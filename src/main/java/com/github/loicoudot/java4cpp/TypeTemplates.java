package com.github.loicoudot.java4cpp;

import java.io.StringWriter;
import java.util.List;

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
    private Template java2cpp;
    private Template cpp2java;
    private Template cpp2javaClean;
    private Template dependencies;

    public class Java2cpp implements TemplateMethodModelEx {

        ClassModel model;

        public Java2cpp(ClassModel model) {
            this.model = model;
        }

        @SuppressWarnings("rawtypes")
        public Object exec(List arguments) throws TemplateModelException {
            if (java2cpp != null) {
                model.setJavaVar(arguments.get(0).toString());
                model.setCppVar(arguments.get(1).toString());
                processTemplate(java2cpp, model);
            }
            return "";
        }
    }

    public class Cpp2java implements TemplateMethodModelEx {

        ClassModel model;

        public Cpp2java(ClassModel model) {
            this.model = model;
        }

        @SuppressWarnings("rawtypes")
        public Object exec(List arguments) throws TemplateModelException {
            if (cpp2java != null) {
                model.setJavaVar(arguments.get(0).toString());
                model.setCppVar(arguments.get(1).toString());
                processTemplate(cpp2java, model);
            }
            return "";
        }
    }

    public class Cpp2javaClean implements TemplateMethodModelEx {

        ClassModel model;

        public Cpp2javaClean(ClassModel model) {
            this.model = model;
        }

        @SuppressWarnings("rawtypes")
        public Object exec(List arguments) throws TemplateModelException {
            if (cpp2javaClean != null) {
                model.setJavaVar(arguments.get(0).toString());
                model.setCppVar(arguments.get(1).toString());
                processTemplate(cpp2javaClean, model);
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
        return processTemplate(getCppType(), classModel);
    }

    public String getCppReturnType(ClassModel classModel) {
        String type = processTemplate(getCppType(), classModel);
        if (!type.isEmpty()) {
            return type;
        }
        return getCppType(classModel);
    }

    public Java2cpp getJava2cpp(ClassModel classModel) {
        return new Java2cpp(classModel);
    }

    public Cpp2java getCpp2Java(ClassModel classModel) {
        return new Cpp2java(classModel);
    }

    public Cpp2javaClean getCpp2JavaClean(ClassModel classModel) {
        return new Cpp2javaClean(classModel);
    }

    public void processDependencies(ClassModel classModel) {
        processTemplate(dependencies, classModel);
    }

    /**
     * @return the cppType
     */
    public Template getCppType() {
        return cppType;
    }

    /**
     * @param cppType
     *            the cppType to set
     */
    public void setCppType(Template cppType) {
        this.cppType = cppType;
    }

    /**
     * @return the cppReturnType
     */
    public Template getCppReturnType() {
        return cppReturnType;
    }

    /**
     * @param cppReturnType
     *            the cppReturnType to set
     */
    public void setCppReturnType(Template cppReturnType) {
        this.cppReturnType = cppReturnType;
    }

    /**
     * @return the java2cpp
     */
    public Template getJava2cpp() {
        return java2cpp;
    }

    /**
     * @param java2cpp
     *            the java2cpp to set
     */
    public void setJava2cpp(Template java2cpp) {
        this.java2cpp = java2cpp;
    }

    /**
     * @return the cpp2java
     */
    public Template getCpp2java() {
        return cpp2java;
    }

    /**
     * @param cpp2java
     *            the cpp2java to set
     */
    public void setCpp2java(Template cpp2java) {
        this.cpp2java = cpp2java;
    }

    /**
     * @return the cpp2javaClean
     */
    public Template getCpp2javaClean() {
        return cpp2javaClean;
    }

    /**
     * @param cpp2javaClean
     *            the cpp2javaClean to set
     */
    public void setCpp2javaClean(Template cpp2javaClean) {
        this.cpp2javaClean = cpp2javaClean;
    }

    /**
     * @return the dependencies
     */
    public Template getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies
     *            the dependencies to set
     */
    public void setDependencies(Template dependencies) {
        this.dependencies = dependencies;
    }
}
