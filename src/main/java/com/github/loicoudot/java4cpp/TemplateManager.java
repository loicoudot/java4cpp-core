package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;

import freemarker.cache.ClassTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public final class TemplateManager {
    private final Logger log = LoggerFactory.getLogger(TemplateManager.class);
    private final Context context;
    private final Templates templates = new Templates();
    private final Configuration configuration = new Configuration();
    private final List<Template> sourceTemplates = newArrayList();
    private final List<Template> globalTemplates = newArrayList();
    private final Map<Class<?>, TypeTemplates> typeCache = newHashMap();

    public TemplateManager(Context context) {
        this.context = context;
        configuration.setTemplateLoader(new ClassTemplateLoader(TemplateManager.class, ""));
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        configuration.setDefaultEncoding("ISO-8859-1");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setIncompatibleImprovements(new Version(2, 3, 20));
        configuration.setLocalizedLookup(false);
    }

    public void addTemplates(Templates other) {
        templates.getSourceTemplates().addAll(other.getSourceTemplates());
        templates.getGlobalTemplates().addAll(other.getGlobalTemplates());
        templates.getCopyFiles().addAll(other.getCopyFiles());
        if (other.getDatatypes().getFallback() != null) {
            templates.getDatatypes().setFallback(other.getDatatypes().getFallback());
        }
        if (other.getDatatypes().getArray() != null) {
            templates.getDatatypes().setArray(other.getDatatypes().getArray());
        }
        if (other.getDatatypes().getEnumeration() != null) {
            templates.getDatatypes().setEnumeration(other.getDatatypes().getEnumeration());
        }
        templates.getDatatypes().getTemplates().addAll(other.getDatatypes().getTemplates());
    }

    public void start() {
        try {
            addTemplatesFromSettings();
            for (String templateName : templates.getSourceTemplates()) {
                sourceTemplates.add(configuration.getTemplate(templateName));
            }
            for (String templateName : templates.getGlobalTemplates()) {
                globalTemplates.add(configuration.getTemplate(templateName));
            }
        } catch (IOException e) {
            log.error("Error reading templates: ", e);
        }
    }

    private void addTemplatesFromSettings() {
        if (!Utils.isNullOrEmpty(context.getSettings().getTemplatesFile())) {
            for (String fileName : context.getSettings().getTemplatesFile().split(";")) {
                try {
                    FileInputStream inStream = new FileInputStream(fileName);
                    Templates templates = JAXB.unmarshal(inStream, Templates.class);
                    inStream.close();
                    addTemplates(templates);
                } catch (IOException e) {
                    log.error("java4cpp templates file error", e);
                }
            }
        }
    }

    public void copyFiles() {
        try {
            for (String file : templates.getCopyFiles()) {
                context.getFileManager().copyFile(file);
            }
        } catch (IOException e) {
            log.error("Error copying files: ", e);
        }
    }

    public void processSourceTemplates(Map<String, Object> dataModel) {
        processTemplates(dataModel, sourceTemplates);
    }

    public void processGlobalTemplates(Map<String, Object> dataModel) {
        processTemplates(dataModel, globalTemplates);
    }

    private void processTemplates(Map<String, Object> dataModel, List<Template> templates) {
        for (Template template : templates) {
            try {
                StringWriter sw = new StringWriter();
                Environment env = template.createProcessingEnvironment(dataModel, sw);
                env.process();
                String fileName = env.getVariable("fileName").toString();
                if (!fileName.isEmpty()) {
                    context.getFileManager().writeSourceFile(fileName, sw);
                }
                sw.close();
            } catch (TemplateException e) {
                log.error("Error processing template: ", e);
            } catch (IOException e) {
                log.error("Error processing template: ", e);
            }
        }
    }

    private TypeTemplate getTypeTemplate(Class<?> clazz) {
        if (clazz.isArray()) {
            return templates.getDatatypes().getArray();
        }
        if (clazz.isEnum()) {
            return templates.getDatatypes().getEnumeration();
        }
        for (TypeTemplate template : templates.getDatatypes().getTemplates()) {
            if (template.getClazz().equals(clazz)) {
                return template;
            }
        }
        return templates.getDatatypes().getFallback();
    }

    private Template parseTemplate(String template) {
        try {
            if (!Utils.isNullOrEmpty(template)) {
                return new Template("", template, configuration);
            }
        } catch (IOException e) {
            log.error("Error processing template: ", e);
        }
        return null;
    }

    public TypeTemplates getTypeTemplates(Class<?> clazz) {
        if (typeCache.containsKey(clazz)) {
            return typeCache.get(clazz);
        }
        TypeTemplates result = new TypeTemplates();
        TypeTemplate type = getTypeTemplate(clazz);
        if (type == null) {
            throw new RuntimeException("No defined template for type " + clazz.getName());
        }
        result.setCppType(parseTemplate(type.getCppType()));
        result.setCppReturnType(parseTemplate(type.getCppReturnType()));
        result.setJava2cpp(parseTemplate(type.getJava2cpp()));
        result.setCpp2java(parseTemplate(type.getCpp2java()));
        result.setCpp2javaClean(parseTemplate(type.getCpp2javaClean()));
        result.setDependencies(parseTemplate(type.getDependencies()));
        typeCache.put(clazz, result);
        return result;
    }
}