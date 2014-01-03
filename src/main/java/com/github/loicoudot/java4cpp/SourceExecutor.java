package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newHashMap;

import java.util.Map;

public class SourceExecutor implements Runnable {
    private final Context context;
    private final Java4CppType type;

    public SourceExecutor(Context context, Java4CppType type) throws InterruptedException {
        this.context = context;
        this.type = type;
    }

    @Override
    public void run() {
        context.getFileManager().enter("generate c++ proxy for " + type);

        context.getClassModel(type);
        final Map<String, Object> dataModel = newHashMap();
        dataModel.put("cppFormatter", new SourceFormatter());
        dataModel.put("class", context.getClassModel(type));
        context.getTemplateManager().processSourceTemplates(dataModel);

        context.getFileManager().leave();
    }
}
