package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import com.github.loicoudot.java4cpp.model.ClassModel;

/**
 * {@code Context} class contains all the environement of an execution of
 * java4cpp.
 * 
 * @author Loic Oudot
 * 
 */
public final class Context {

    private Log log = new SystemStreamLog();
    private final Settings settings;
    private final FileManager fileManager;
    private final MappingsManager mappingsManager;
    private final TemplateManager templateManager;
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private final BlockingQueue<Class<?>> classesToDo = new ArrayBlockingQueue<Class<?>>(1024);
    private final List<Class<?>> classesAlreadyDone = newArrayList();
    private final Map<Class<?>, ClassModel> classModelCache = newHashMap();

    public Context(Settings settings) {
        this.settings = settings;
        fileManager = new FileManager(this);
        mappingsManager = new MappingsManager(this);
        templateManager = new TemplateManager(this);
    }

    public Log getLog() {
        return log;
    }

    /**
     * Sets the logger from maven plugins execution.
     * 
     * @param log
     *            a maven plugin logger
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * Called before begining the processing of classes. Initialize all internal
     * parts with all the configurations.
     */
    public void start() {
        getFileManager().start();
        getMappingsManager().start();
        addClassToDoFromJars();
        getTemplateManager().start();
    }

    /**
     * Called after all the classes are processed. Finalize the job by cleaning
     * up the target directory.
     */
    public void stop() {
        getFileManager().stop();
    }

    /**
     * Add annotated classes with {@code Java4Cpp} annotation to the current
     * list of class to be processed by introspecting jar files. The context
     * {@code ClassLoader} is augmented with each jar {@code ClassLoader}'s.
     */
    private void addClassToDoFromJars() {
        if (!Utils.isNullOrEmpty(settings.getJarFiles())) {
            try {
                String[] files = settings.getJarFiles().split(";");
                for (String file : files) {
                    getFileManager().logInfo("searching classes to wrappe in " + file);
                    classLoader = new URLClassLoader(new URL[] { new File(file).toURI().toURL() }, classLoader);
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

    public void addClassToDo(Class<?> clazz) {
        synchronized (classesToDo) {
            if (clazz.getEnclosingClass() == null && !classesAlreadyDone.contains(clazz) && !classesToDo.contains(clazz)) {
                classesToDo.add(clazz);
                classesAlreadyDone.add(clazz);
                getFileManager().logInfo("   add dependency " + clazz.getName());
            }
        }
    }

    public BlockingQueue<Class<?>> getClassesToDo() {
        return classesToDo;
    }

    public boolean workToDo() {
        return !classesToDo.isEmpty();
    }

    public List<Class<?>> getClassesAlreadyDone() {
        return classesAlreadyDone;
    }

    public Settings getSettings() {
        return settings;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public MappingsManager getMappingsManager() {
        return mappingsManager;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
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

    public ClassModel getClassModel(String name) {
        try {
            return getClassModel(classLoader.loadClass(name));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class " + e.getMessage());
        }
    }
}
