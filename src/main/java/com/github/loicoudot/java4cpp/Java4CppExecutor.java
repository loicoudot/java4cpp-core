package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Java4CppExecutor implements Runnable {
    private final Logger log = LoggerFactory.getLogger(Java4CppExecutor.class);
    private final Context context;
    private Class<?> clazz;

    public Java4CppExecutor(Context context) {
        this.context = context;
        try {
            clazz = context.getClassesToDo().take();
        } catch (InterruptedException e) {
            log.error("java4cpp executor:", e);
        }
    }

    public void run() {
        log.info("generating c++ wrapper for {}", clazz.getName());

        final Map<String, Object> dataModel = newHashMap();
        dataModel.put("cppFormatter", new SourceFormatter());
        dataModel.put("class", context.getClassModel(clazz));

        context.getTemplateManager().processSourceTemplates(dataModel);
    }
}