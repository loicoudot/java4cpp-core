package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.loicoudot.java4cpp.model.ClassModel;

public class Core {
    private static final int TIMEOUT = 20;
    private Context context;

    public static void main(String[] args) {
        new Core().execute(new Context(new Settings(args)));
    }

    /**
     * Execute all java4cpp job defined by the {@code context}
     */
    public void execute(Context context) {
        this.context = context;

        context.start();
        Date startTime = new Date();
        context.getFileManager()
                .logInfo(String.format("java4cpp version %s, starting at %s", Context.class.getPackage().getImplementationVersion(), startTime));

        analyzeModels();
        resolveTypeTemplates();
        generateSources();
        finalization();

        context.getFileManager().logInfo(String.format("elapsed time: %ds", (new Date().getTime() - startTime.getTime()) / 1));
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
        Collections.sort(context.getClassesAlreadyDone(), new Comparator<Class<?>>() {

            @Override
            public int compare(Class<?> o1, Class<?> o2) {
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
        for (Class<?> clazz : context.getClassesAlreadyDone()) {
            context.executeTypeTemplate(clazz);
        }
    }

    /**
     * Generate proxies source code files
     */
    private void generateSources() {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(context.getSettings().getNbThread());

            for (Class<?> clazz : context.getClassesAlreadyDone()) {
                if (!clazz.isPrimitive() && !clazz.isArray() && clazz.getEnclosingClass() == null) {
                    pool.execute(new SourceExecutor(context, clazz));
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
        List<ClassModel> dependencies = newArrayList();
        for (Class<?> clazz : context.getClassesAlreadyDone()) {
            dependencies.add(context.getClassModel(clazz));
        }
        dataModel.put("classes", dependencies);

        context.getTemplateManager().processGlobalTemplates(dataModel);

        context.getTemplateManager().copyFiles();
    }
}
