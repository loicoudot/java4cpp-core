package com.github.loicoudot.java4cpp.model;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Model that encapsulate a Java class. The model has two parts :
 * <ul>
 * <li>Type definition: contains the types mappings informations between Java
 * and C++. If the class is a parametrized type, <code>parameters</code>
 * contains the list of parameterized type in a recursive manner. If the class
 * is a generic type,
 * <code>parameters<code> contains only a <code>java.lang.Object</code> class
 * model. for non generic class, <code>parameters</code> is null.</li>
 * <li>Class content: The instrospection results for the inner classes, enums,
 * static fields, constructors and methods.</li>
 * </ul>
 * 
 * @author Loic Oudot
 * 
 */
public final class ClassModel {

    private final ClassType type;
    private List<ClassModel> parameters;
    private ClassContent content;

    public ClassModel(Type type) {
        this.type = new ClassType(type);
    }

    public ClassType getType() {
        return type;
    }

    public List<ClassModel> getParameters() {
        return parameters;
    }

    public void addParameter(ClassModel parameter) {
        if (parameters == null) {
            parameters = newArrayList();
        }
        parameters.add(parameter);
    }

    public ClassContent getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("type(%s)", type.getType());
    }
}
