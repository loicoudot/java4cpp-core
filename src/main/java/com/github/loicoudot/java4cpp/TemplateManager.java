package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import com.github.loicoudot.java4cpp.configuration.Function;
import com.github.loicoudot.java4cpp.configuration.Templates;
import com.github.loicoudot.java4cpp.configuration.TypeTemplate;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public final class TemplateManager {
    private final Context context;
    private final Templates templates = new Templates();
    private final Configuration configuration = new Configuration();
    private final List<Template> sourceTemplates = newArrayList();
    private final List<Template> globalTemplates = newArrayList();
    private final Map<Class<?>, TypeTemplates> typeCache = newHashMap();

    public TemplateManager(Context context) {
        this.context = context;
        try {
            TemplateLoader[] loaders = { new FileTemplateLoader(), new ThreadTemplateLoader() };
            configuration.setTemplateLoader(new MultiTemplateLoader(loaders));
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            configuration.setDefaultEncoding("ISO-8859-1");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setIncompatibleImprovements(new Version(2, 3, 20));
            configuration.setLocalizedLookup(false);
        } catch (IOException e) {
            throw new RuntimeException("FreeMarker initialisation error " + e.getMessage());
        }
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
            throw new RuntimeException("Failed to read templates " + e.getMessage());
        }
    }

    private void addTemplatesFromSettings() {
        if (!Utils.isNullOrEmpty(context.getSettings().getTemplatesFile())) {
            for (String name : context.getSettings().getTemplatesFile().split(";")) {
                try {
                    InputStream is = Utils.getFileOrResource(name);
                    Templates template = JAXB.unmarshal(is, Templates.class);
                    is.close();
                    addTemplates(template);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read templates: " + e.getMessage());
                }
            }
        }
    }

    public void copyFiles() {
        try {
            for (String file : templates.getCopyFiles()) {
                context.getFileManager().copyFile(file);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy file " + e.getMessage());
        }
    }

    public void processSourceTemplates(Map<String, Object> dataModel) {
        processTemplates(dataModel, sourceTemplates);
    }

    public void processGlobalTemplates(Map<String, Object> dataModel) {
        processTemplates(dataModel, globalTemplates);
    }

    private void processTemplates(Map<String, Object> dataModel, List<Template> templateList) {
        for (Template template : templateList) {
            try {
                StringWriter sw = new StringWriter();
                Environment env = template.createProcessingEnvironment(dataModel, sw);
                env.process();
                String fileName = env.getVariable("fileName").toString();
                if (!fileName.isEmpty()) {
                    context.getFileManager().writeSourceFile(fileName, sw);
                }
                sw.close();
            } catch (Exception e) {
                throw new RuntimeException("Failed to process template " + e.getMessage());
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

    public Template createTemplate(String template) {
        try {
            if (!Utils.isNullOrEmpty(template)) {
                return new Template("", template, configuration);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process template " + e.getMessage());
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
        result.setCppType(createTemplate(type.getCppType()));
        result.setCppReturnType(createTemplate(type.getCppReturnType()));

        HashMap<String, Template> functions = newHashMap();
        for (Function function : type.getFunctions()) {
            functions.put(function.getName(), createTemplate(function.getTemplate()));
        }
        result.setFunctions(functions);

        typeCache.put(clazz, result);
        return result;
    }

}
