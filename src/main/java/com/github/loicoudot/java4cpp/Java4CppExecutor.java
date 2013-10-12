package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.util.Map;

class Java4CppExecutor implements Runnable {
    private final Context context;
    private final Class<?> clazz;

    public Java4CppExecutor(Context context) throws InterruptedException {
        this.context = context;
        clazz = context.getClassesToDo().take();
    }

    @Override
    public void run() {
        context.getFileManager().logInfo("generating c++ wrapper for " + clazz.getName());

        final Map<String, Object> dataModel = newHashMap();
        dataModel.put("cppFormatter", new SourceFormatter());
        dataModel.put("class", context.getClassModel(clazz));

        context.getTemplateManager().processSourceTemplates(dataModel);
    }
}