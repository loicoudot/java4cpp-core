package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.io.File;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.loicoudot.java4cpp.model.ClassModel;
import com.github.loicoudot.java4cpp.model.ClassType;

/**
 * {@code Context} class contains all the environement of an execution of
 * java4cpp.
 * 
 * @author Loic Oudot
 * 
 */
public final class Context {

    private final Settings settings;
    private final FileManager fileManager;
    private final MappingsManager mappingsManager;
    private final TemplateManager templateManager;
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private final BlockingQueue<Class<?>> classesToDo = new ArrayBlockingQueue<Class<?>>(1024);
    private final List<Class<?>> classesAlreadyDone = newArrayList();
    private final Map<Type, ClassModel> classModelCache = newHashMap();
    private final Analyzer[] analyzers;
    private final Analyzer typeAnalyzer;

    public Context(Settings settings) {
        this.settings = settings;
        fileManager = new FileManager(this);
        mappingsManager = new MappingsManager(this);
        templateManager = new TemplateManager(this);
        typeAnalyzer = new TypeAnalyzer(this);
        analyzers = new Analyzer[] { typeAnalyzer, new SuperclassAnalyzer(this), new InterfacesAnalyzer(this), new InnerClassAnalyzer(this),
                new FieldsAnalyzer(this), new EnumAnalyzer(this), new ConstructorsAnalyzer(this), new MethodsAnalyzer(this) };
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
            if (!classesAlreadyDone.contains(clazz) && !classesToDo.contains(clazz)) {
                classesToDo.add(clazz);
                classesAlreadyDone.add(clazz);
                getFileManager().logInfo("add dependency " + clazz.getName());
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

    /**
     * Gets the raw class of a java <code>type</code>. Returns:
     * <ul>
     * <li>Inner class for arrays</li>
     * <li>Non parameterized class for parameterized type</li>
     * <li>Upper bounds class for type variable</li>
     * <ul>
     * 
     * @param type
     *            java type
     * @return Corresponding java class
     */
    @SuppressWarnings("rawtypes")
    public static Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof TypeVariable) {
            return getRawClass(((TypeVariable) type).getBounds()[0]);
        }
        if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            return getRawClass(((GenericArrayType) type).getGenericComponentType());
        }
        if (type instanceof WildcardType) {
            return getRawClass(((WildcardType) type).getUpperBounds()[0]);
        }
        throw new RuntimeException("Can't get raw class from " + type);
    }

    public ClassModel analyzeClassModel(Type type) {
        try {
            getFileManager().enter("analyzing " + type);
            ClassModel classModel = reserveClassModelEntry(type);
            if (getTemplateManager().getTypeTemplates(getRawClass(type)).isNeedAnalyzing()) {
                for (Analyzer analyzer : analyzers) {
                    analyzer.fill(classModel);
                }
            } else {
                typeAnalyzer.fill(classModel);
            }
            return classModel;
        } finally {
            getFileManager().leave();
        }
    }

    public ClassModel executeTypeTemplate(Class<?> clazz) {
        try {
            getFileManager().enter("templating " + clazz);
            ClassModel classModel = reserveClassModelEntry(clazz);
            ClassType typeModel = classModel.getType();

            TypeTemplates typeTemplates = getTemplateManager().getTypeTemplates(clazz);
            typeModel.setCppType(typeTemplates.getCppType(classModel));
            typeModel.setCppReturnType(typeTemplates.getCppReturnType(classModel));
            typeTemplates.executeDependencies(classModel);
            typeModel.setFunctions(typeTemplates.getFunctions(classModel));
            return classModel;
        } finally {
            getFileManager().leave();
        }
    }

    public ClassModel getClassModel(Type type) {
        ClassModel classModel = reserveClassModelEntry(type);

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type argumentType : parameterizedType.getActualTypeArguments()) {
                classModel.addParameter(getClassModel(argumentType));
            }
        }
        return classModel;
    }

    public ClassModel getClassModel(String name) {
        try {
            return getClassModel(classLoader.loadClass(name));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class " + e.getMessage());
        }
    }

    public List<ClassModel> getClassesModels(Type[] types) {
        List<ClassModel> result = newArrayList();
        for (Type type : types) {
            result.add(getClassModel(type));
        }
        return result;
    }

    private ClassModel reserveClassModelEntry(Type type) {
        if (!classModelCache.containsKey(type)) {
            synchronized (classModelCache) {
                if (!classModelCache.containsKey(type)) {
                    classModelCache.put(type, new ClassModel(type));
                    addClassToDo(getRawClass(type));
                }
            }
        }
        return classModelCache.get(type);
    }
}
