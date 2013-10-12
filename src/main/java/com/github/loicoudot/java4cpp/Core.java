package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.loicoudot.java4cpp.model.ClassModel;

public class Core {
    private static final int TIMEOUT = 20;

    public static void main(String[] args) {
        new Core().start(args);
    }

    private void start(String[] args) {
        execute(new Context(new Settings(args)));
    }

    /**
     * Execute all java4cpp job defined by the {@code context}
     * 
     * @param context
     */
    public void execute(Context context) {

        context.start();
        context.getFileManager().logInfo(
                String.format("java4cpp version %s, starting at %s", Context.class.getPackage().getImplementationVersion(), new Date()));

        try {
            do {
                ExecutorService pool = Executors.newFixedThreadPool(context.getSettings().getNbThread());

                while (context.workToDo()) {
                    pool.execute(new Java4CppExecutor(context));
                }

                pool.shutdown();
                while (!pool.isTerminated()) {
                    pool.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
                }

            } while (context.workToDo());
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted " + e.getMessage());
        }
        finalize(context);

        context.stop();
    }

    private void finalize(Context context) {
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
