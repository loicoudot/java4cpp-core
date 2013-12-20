package com.github.loicoudot.java4cpp.model;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;
import static com.github.loicoudot.java4cpp.Utils.newHashSet;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * Model that encapsulate a Java class. The model has two parts :
 * <ul>
 * <li>Type definition: contains the types mappings informations between Java and C++. If the class is a parametrized type,
 * <code>parameters</code> contains the list of parameterized type in a recursive manner. If the class is a generic type,
 * <code>parameters<code> contains only a <code>java.lang.Object</code> class model. for non generic class,
 * <code>parameters</code> is null.</li>
 * <li>Class content: The instrospection results for the inner classes, enums, static fields, constructors and methods.</li>
 * </ul>
 * 
 * @author Loic Oudot
 * 
 */
public final class ClassModel {

    private final ClassType type;
    private List<ClassModel> parameters;
    private final ClassContent content;

    public ClassModel(Type type) {
        this.type = new ClassType(type);
        this.content = new ClassContent();
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

    public Set<String> getIncludes() {
        Set<String> result = newHashSet();
        result.addAll(getType().getIncludes());
        result.addAll(getContent().getIncludes());
        return result;
    }

    public Set<ClassModel> getDependencies() {
        Set<ClassModel> result = newHashSet();
        result.addAll(getType().getDependencies());
        result.addAll(getContent().getDependencies());
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.getType().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClassModel other = (ClassModel) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.getType().equals(other.type.getType())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("class(%s)", type.getType());
    }
}
