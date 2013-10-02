package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXB;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import com.github.loicoudot.java4cpp.configuration.Clazz;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Namespace;
import com.github.loicoudot.java4cpp.model.ClassModel;

public final class Context {

    private final Settings settings;
    private Log log = new SystemStreamLog();
    private final Mappings mappings = new Mappings();
    private final TemplateManager templateManager;
    private final FileManager fileManager;
    private final BlockingQueue<Class<?>> classesToDo = new ArrayBlockingQueue<Class<?>>(1024);
    private final List<Class<?>> classesAlreadyDone = newArrayList();
    private final Map<Class<?>, Clazz> classesCache = newHashMap();
    private final Map<Class<?>, String> namespaceCache = newHashMap();
    private final Map<Class<?>, MappingsHelper> mappingsCache = newHashMap();
    private final Map<Class<?>, ClassModel> classModelCache = newHashMap();

    public Context(Settings settings) {
        this.settings = settings;
        fileManager = new FileManager(this);
        templateManager = new TemplateManager(this);
    }

    public Settings getSettings() {
        return settings;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void addMappings(Mappings other) {
        mappings.getKeywords().addAll(other.getKeywords());
        mappings.getClasses().addAll(other.getClasses());
        mappings.getNamespaces().addAll(other.getNamespaces());
    }

    /**
     * Called before begining the processing of classes. Initialize all internal
     * parts with all the configurations.
     */
    public void start() {
        getFileManager().start();
        addMappingsFromSettings();
        addClassToDoFromJars();
        addClassToDoFromMappings();
        getTemplateManager().start();
    }

    /**
     * Called after all the classes are processed. Finalize the job by cleaning
     * up the target directory.
     */
    public void stop() {
        getFileManager().stop();
    }

    private void addMappingsFromSettings() {
        if (!Utils.isNullOrEmpty(settings.getMappingsFile())) {
            for (String fileName : settings.getMappingsFile().split(";")) {
                try {
                    InputStream is = null;
                    if (new File(fileName).isFile()) {
                        is = new FileInputStream(fileName);
                    } else {
                        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                    }
                    if (is == null) {
                        throw new RuntimeException("Failed to read mappings from settings " + fileName + " (file not found)");
                    }
                    Mappings mapping = JAXB.unmarshal(is, Mappings.class);
                    is.close();
                    addMappings(mapping);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read mappings from settings " + e.getMessage());
                }
            }
        }
    }

    private void addClassToDoFromJars() {
        if (!Utils.isNullOrEmpty(settings.getJarFiles())) {
            try {
                String[] files = settings.getJarFiles().split(";");
                for (String file : files) {
                    getFileManager().logInfo("searching classes to wrappe in " + file);
                    URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(file).toURI().toURL() }, Thread.currentThread()
                            .getContextClassLoader());
                    JarFile jf = new JarFile(file);
                    Enumeration<JarEntry> entries = jf.entries();
                    while (entries.hasMoreElements()) {
                        String clName = entries.nextElement().getName();
                        if (clName.endsWith(".class")) {
                            Class<?> clazz = classLoader.loadClass(clName.split("\\.")[0].replace('/', '.'));
                            if (clazz.isAnnotationPresent(Java4Cpp.class)) {
                                addClassToDo(clazz);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to load jar " + e.getMessage());
            }
        }
    }

    private void addClassToDoFromMappings() {
        getFileManager().logInfo("adding classes to wrappe from mappings files");
        for (Clazz clazz : getMappings().getClasses()) {
            addClassToDo(clazz.getClazz());
            classesCache.put(clazz.getClazz(), clazz);
        }
    }

    public void addClassToDo(Class<?> clazz) {
        synchronized (classesToDo) {
            if (clazz.getEnclosingClass() == null && !classesAlreadyDone.contains(clazz) && !classesToDo.contains(clazz)) {
                classesToDo.add(clazz);
                classesAlreadyDone.add(clazz);
                getFileManager().logInfo("   add dependency " + clazz.getName());
            }
        }
    }

    public boolean workToDo() {
        return !classesToDo.isEmpty();
    }

    public String escapeName(String name) {
        if (getMappings().getKeywords().contains(name)) {
            return escapeName(name + '_');
        }
        return name;
    }

    public String getNamespaceForClass(Class<?> clazz) {
        synchronized (namespaceCache) {
            if (!namespaceCache.containsKey(clazz)) {
                int bestScore = 0;
                String bestNamespace = clazz.getName().replaceAll("\\.", "::");
                for (Namespace namespace : getMappings().getNamespaces()) {
                    if (namespace.getJavaPackage().length() > bestScore && clazz.getName().matches(namespace.getJavaPackage())) {
                        bestScore = namespace.getJavaPackage().length();
                        bestNamespace = Utils.isNullOrEmpty(namespace.getNamespace()) ? clazz.getSimpleName() : String.format("%s::%s",
                                namespace.getNamespace(), clazz.getSimpleName());
                    }
                }
                namespaceCache.put(clazz, bestNamespace);
            }
            return namespaceCache.get(clazz);
        }
    }

    public Clazz getClazz(Class<?> cl) {
        return classesCache.get(cl);
    }

    public MappingsHelper getMappings(Class<?> clazz) {
        synchronized (mappingsCache) {
            if (!mappingsCache.containsKey(clazz)) {
                mappingsCache.put(clazz, new MappingsHelper(clazz, this));
            }
            return mappingsCache.get(clazz);
        }
    }

    public ClassModel getClassModel(Class<?> clazz) {
        synchronized (classModelCache) {
            if (!classModelCache.containsKey(clazz)) {
                classModelCache.put(clazz, new ClassModel(clazz));
                new ClassAnalyzer(clazz, this).fillModel(classModelCache.get(clazz));
            }
            return classModelCache.get(clazz);
        }
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Mappings getMappings() {
        return mappings;
    }

    public BlockingQueue<Class<?>> getClassesToDo() {
        return classesToDo;
    }

    public List<Class<?>> getClassesAlreadyDone() {
        return classesAlreadyDone;
    }
}
