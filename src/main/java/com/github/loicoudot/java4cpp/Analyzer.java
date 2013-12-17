package com.github.loicoudot.java4cpp;

import com.github.loicoudot.java4cpp.model.ClassModel;

abstract class Analyzer {

    protected final Context context;
    protected final MappingsManager mappings;

    public Analyzer(Context context) {
        this.context = context;
        mappings = context.getMappingsManager();
    }

    public abstract void fill(ClassModel classModel);

}