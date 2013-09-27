package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.loicoudot.java4cpp.configuration.Clazz;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Namespace;
import com.github.loicoudot.java4cpp.model.ClassModel;

public final class Context {
    private final Logger log = LoggerFactory.getLogger(Context.class);
    private static final String DEFAULT_MAPPINGS_XML = "DefaultMappings.xml";

    private final Settings settings;
    private Mappings mappings;
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
        readDefaultMappings();
    }

    public Settings getSettings() {
        return settings;
    }

    private void readDefaultMappings() {
        try {
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_MAPPINGS_XML);
            mappings = JAXB.unmarshal(input, Mappings.class);
            input.close();
        } catch (Exception e) {
            log.error("Error reading default mappings: ", e);
        }
    }

    /**
     * Called before begining the processing of classes. Initialize all internal
     * parts with all the configurations.
     */
    public void start() {
        log.info("java4cpp version {}, starting at {}", Context.class.getPackage().getImplementationVersion(), new Date());
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
            try {
                FileInputStream inStream = new FileInputStream(settings.getMappingsFile());
                Mappings mappings = JAXB.unmarshal(inStream, Mappings.class);
                inStream.close();
                addMappings(mappings);
            } catch (IOException e) {
                log.error("java4cpp mappings file error", e);
            }
        }
    }

    public void addMappings(Mappings config) {
        if (config.isReplaceDefaultMappings()) {
            mappings.getClasses().clear();
            mappings.getKeywords().clear();
            mappings.getNamespaces().clear();
        }
        mappings.getKeywords().addAll(config.getKeywords());
        mappings.getClasses().addAll(config.getClasses());
        mappings.getNamespaces().addAll(config.getNamespaces());
    }

    private void addClassToDoFromJars() {
        if (!Utils.isNullOrEmpty(settings.getJarFiles())) {
            try {
                String[] files = settings.getJarFiles().split(";");
                for (String file : files) {
                    log.info("searching classes to wrappe in {}", file);
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
            } catch (ClassNotFoundException e) {
                System.out.println("java4cpp error: class not found '" + e.getMessage() + "'.\n");
            } catch (IOException e) {
                System.out.println("java4cpp error: unable to load file '" + settings.getJarFiles() + "'.");
            } catch (Exception e) {
                System.out.println("java4cpp error: exception : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void addClassToDoFromMappings() {
        log.info("adding classes to wrappe from mappings files");
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
                log.info("   add dependency {}", clazz.getName());
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
