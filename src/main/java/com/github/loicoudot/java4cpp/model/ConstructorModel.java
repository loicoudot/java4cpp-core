package com.github.loicoudot.java4cpp.model;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.util.List;

public final class ConstructorModel {
    private List<ClassModel> parameters = newArrayList();

    public List<ClassModel> getParameters() {
        return parameters;
    }
}
