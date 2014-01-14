package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * Reprensent types inside java4cpp.
 * 
 * Difference between <code>Java4cppType</code> and <code>Type</code>:
 * <ul>
 * <li>pure class, no interfaces</li>
 * <li>Wildcard are replaced by <code>Object</code> (<code>List&lt;?></code>
 * will be replaced by <code>List&lt;Object></code>)
 * <li>Generic are replaced by upper bounds type (
 * <code>List&lt;E extends String></code> will be replaced by
 * <code>List&lt;String></code>)
 * </ul>
 * 
 * Each different <code>Java4cppType</code> is a singleton, so it can be used as
 * a key.
 * 
 * @author Loic Oudot
 * 
 */
public class Java4CppType {

    private final Class<?> rawClass;
    private final List<Java4CppType> parameterizedTypes = newArrayList();

    private static List<Java4CppType> typeCache = newArrayList();

    /**
     * Returns the corresponding <code>Java4CppType</code> of a
     * <code>Type</code>.
     * 
     * Even if <code>List&lt;E></code> and <code>List&lt;?></code> are two
     * different instance of <code>Type</code>, this method returns the same
     * instance of <code>Java4CppType</code> representing
     * <code>List&lt;Object></code>.
     * 
     * @param type
     *            a Java <code>Type</code>
     * @return the corresponding <code>Java4CppType</code> singleton
     */
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

    /**
     * The base raw class of a type i.e. <code>List</code> for
     * <code>List&lt;?></code>
     * 
     * @return base raw class
     */
    public Class<?> getRawClass() {
        return rawClass;
    }

    /**
     * For parameterized type, contains an ordered list of
     * <code>Java4CppType</code> type compositing the type i.e. for a
     * <code>Map&ltInteger, String></code> returns a list with
     * <code>{ Integer, String }</code>.
     * 
     * @return list of type arguments
     */
    public List<Java4CppType> getParameterizedTypes() {
        return parameterizedTypes;
    }

    /**
     * Gets the raw class of a java <code>type</code>. Returns:
     * <ul>
     * <li>Inner class for arrays</li>
     * <li>Non parameterized class for parameterized type</li>
     * <li>Upper bounds class for type variable</li>
     * </ul>
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
