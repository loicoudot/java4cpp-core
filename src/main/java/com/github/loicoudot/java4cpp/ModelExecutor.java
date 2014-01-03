package com.github.loicoudot.java4cpp;


class ModelExecutor implements Runnable {
    private final Context context;
    private final Java4CppType type;

    public ModelExecutor(Context context) throws InterruptedException {
        this.context = context;
        type = context.getClassesToDo().take();
    }

    @Override
    public void run() {
        context.analyzeClassModel(type);
    }
}