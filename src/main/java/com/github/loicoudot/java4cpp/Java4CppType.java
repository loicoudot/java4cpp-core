package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public class Java4CppType {

    private Class<?> rawClass;
    private List<Java4CppType> parameterizedTypes = newArrayList();

    private static List<Java4CppType> typeCache = newArrayList();

    public static Java4CppType fromType(Type type) {
        Java4CppType j4cType = new Java4CppType(type);
        int index = typeCache.indexOf(j4cType);
        if (index == -1) {
            typeCache.add(j4cType);
            return j4cType;
        }
        return typeCache.get(index);
    }

    private Java4CppType(Type type) {
        rawClass = getRawClass(type);

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type argumentType : parameterizedType.getActualTypeArguments()) {
                parameterizedTypes.add(new Java4CppType(argumentType));
            }
        }
    }

    public Class<?> getRawClass() {
        return rawClass;
    }

    public List<Java4CppType> getParameterizedTypes() {
        return parameterizedTypes;
    }

    /**
     * Gets the raw class of a java <code>type</code>. Returns:
     * <ul>
     * <li>Inner class for arrays</li>
     * <li>Non parameterized class for parameterized type</li>
     * <li>Upper bounds class for type variable</li>
     * <ul>
     * 
     * @param type
     *            java type
     * @return Corresponding java class
     */
    @SuppressWarnings("rawtypes")
    private static Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof TypeVariable) {
            return getRawClass(((TypeVariable) type).getBounds()[0]);
        }
        if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            return getRawClass(((GenericArrayType) type).getGenericComponentType());
        }
        if (type instanceof WildcardType) {
            return getRawClass(((WildcardType) type).getUpperBounds()[0]);
        }
        throw new RuntimeException("Can't get raw class from " + type);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((parameterizedTypes == null) ? 0 : parameterizedTypes.hashCode());
        result = prime * result + ((rawClass == null) ? 0 : rawClass.hashCode());
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
        Java4CppType other = (Java4CppType) obj;
        if (parameterizedTypes == null) {
            if (other.parameterizedTypes != null) {
                return false;
            }
        } else if (!parameterizedTypes.equals(other.parameterizedTypes)) {
            return false;
        }
        if (rawClass == null) {
            if (other.rawClass != null) {
                return false;
            }
        } else if (!rawClass.equals(other.rawClass)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rawClass.getName());
        if (!parameterizedTypes.isEmpty()) {
            String separator = "<";
            for (Java4CppType type : parameterizedTypes) {
                sb.append(separator);
                sb.append(type.toString());
                separator = ", ";
            }
            sb.append('>');
        }
        return sb.toString();
    }
}
