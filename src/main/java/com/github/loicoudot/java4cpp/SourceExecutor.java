package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.util.Map;

public class SourceExecutor implements Runnable {
    private final Context context;
    private final Class<?> clazz;

    public SourceExecutor(Context context, Class<?> clazz) throws InterruptedException {
        this.context = context;
        this.clazz = clazz;
    }

    @Override
    public void run() {
        context.getFileManager().enter("generate c++ proxy for " + clazz.getName());

        context.getClassModel(clazz);
        final Map<String, Object> dataModel = newHashMap();
        dataModel.put("cppFormatter", new SourceFormatter());
        dataModel.put("class", context.getClassModel(clazz));
        context.getTemplateManager().processSourceTemplates(dataModel);

        context.getFileManager().leave();
    }
}
