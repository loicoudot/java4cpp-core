package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newHashMap;
import static com.github.loicoudot.java4cpp.Utils.newHashSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.loicoudot.java4cpp.model.ClassModel;

public class Core {
    private static final int MILLISEC = 1000;
    private static final int TIMEOUT = 20;
    private Context context;

    public static void main(String[] args) {
        new Core().execute(new Context(new Settings(args)));
    }

    /**
     * Execute all java4cpp job defined by the {@code context}
     */
    public void execute(Context aContext) {
        this.context = aContext;

        context.start();
        Date startTime = new Date();
        context.getFileManager().logInfo(String.format("java4cpp version %s, starting at %s", Context.class.getPackage().getImplementationVersion(), startTime));

        analyzeModels();
        resolveTypeTemplates();
        generateSources();
        finalization();

        context.getFileManager().logInfo(String.format("elapsed time: %ds", (new Date().getTime() - startTime.getTime()) / MILLISEC));
        context.stop();
    }

    /**
     * Fill ClassModel type and content parts by java introspection of classes
     */
    private void analyzeModels() {
        try {
            do {
                ExecutorService pool = Executors.newFixedThreadPool(context.getSettings().getNbThread());

                while (context.workToDo()) {
                    pool.execute(new ModelExecutor(context));
                }

                pool.shutdown();
                while (!pool.isTerminated()) {
                    pool.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
                }

            } while (context.workToDo());
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted " + e.getMessage());
        }
    }

    /**
     * Execute type freemarker templates of ClassModel
     */
    private void resolveTypeTemplates() {
        Collections.sort(context.getClassesAlreadyDone(), new Comparator<Java4CppType>() {

            @Override
            public int compare(Java4CppType t1, Java4CppType t2) {
                Class<?> o1 = t1.getRawClass();
                Class<?> o2 = t2.getRawClass();
                if (o1.isArray() && o2.isArray()) {
                    int idx1 = o1.getName().lastIndexOf('[');
                    int idx2 = o2.getName().lastIndexOf('[');
                    if (idx1 == idx2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return idx1 < idx2 ? -1 : 1;
                } else if (o1.isArray() && !o2.isArray()) {
                    return 1;
                } else if (!o1.isArray() && o2.isArray()) {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Java4CppType type : context.getClassesAlreadyDone()) {
            context.executeTypeTemplate(type);
        }
    }

    /**
     * Generate proxies source code files
     */
    private void generateSources() {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(context.getSettings().getNbThread());

            for (Java4CppType type : context.getClassesAlreadyDone()) {
                Class<?> clazz = type.getRawClass();
                if (isValid(clazz)) {
                    pool.execute(new SourceExecutor(context, type));
                }
            }

            pool.shutdown();
            while (!pool.isTerminated()) {
                pool.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted " + e.getMessage());
        }
    }

    /**
     * Execute global templates, and copy runtime files
     */
    private void finalization() {
        Map<String, Object> dataModel = newHashMap();
        dataModel.put("cppFormatter", new SourceFormatter());
        Set<ClassModel> dependencies = newHashSet();
        for (Java4CppType type : context.getClassesAlreadyDone()) {
            Class<?> clazz = type.getRawClass();
            if (isValid(clazz) && context.getTemplateManager().getTypeTemplates(clazz).isNeedAnalyzing()) {
                dependencies.add(context.getClassModel(clazz));
            }
        }
        dataModel.put("classes", dependencies);

        context.getTemplateManager().processGlobalTemplates(dataModel);

        context.getTemplateManager().copyFiles();
    }

    private boolean isValid(Class<?> clazz) {
        return !clazz.isPrimitive() && !clazz.isArray() && clazz.getEnclosingClass() == null;
    }

}
