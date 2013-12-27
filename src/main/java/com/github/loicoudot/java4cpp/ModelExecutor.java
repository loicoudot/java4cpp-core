package com.github.loicoudot.java4cpp;

class ModelExecutor implements Runnable {
    private final Context context;
    private final Class<?> clazz;

    public ModelExecutor(Context context) throws InterruptedException {
        this.context = context;
        clazz = context.getClassesToDo().take();
    }

    @Override
    public void run() {
        context.getFileManager().enter("create model for " + clazz.getName());
        context.getClassModel(clazz);
        context.getFileManager().leave();
    }
}