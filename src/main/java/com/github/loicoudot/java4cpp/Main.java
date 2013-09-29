package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.loicoudot.java4cpp.model.ClassModel;

public class Main {
    private final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        new Main().start(args);
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
        log.info("java4cpp version {}, starting at {}", Context.class.getPackage().getImplementationVersion(), new Date());
        context.start();

        do {
            ExecutorService pool = Executors.newFixedThreadPool(context.getSettings().getNbThread());

            while (context.workToDo()) {
                pool.execute(new Java4CppExecutor(context));
            }

            pool.shutdown();
            while (!pool.isTerminated()) {
                try {
                    pool.awaitTermination(20, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    log.error("execute: ", e);
                }
            }

        } while (context.workToDo());

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
