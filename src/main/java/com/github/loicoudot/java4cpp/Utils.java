package com.github.loicoudot.java4cpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public final class Utils {

    /**
     * Creates a <i>mutable</i>, empty {@code ArrayList} instance.
     * 
     * @return a new, empty {@code ArrayList}
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    /**
     * Creates a <i>mutable</i>, empty {@code HashMap} instance.
     * 
     * @return a new, empty {@code HashMap}
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * Creates a <i>mutable</i>, empty {@code HashSet} instance.
     * 
     * @return a new, empty {@code HashSet}
     */
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    /**
     * Returns {@code true} if the given string is null or is the empty string.
     * 
     * @param string
     *            a string reference to check
     * @return {@code true} if the string is null or is the empty string
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * Looks for a file in the file system path named {@code name}, if none
     * exist looks for a resource named {@code name} inside the current class
     * path.
     * 
     * @param name
     *            a file or a resource name to find
     * @return the corresponding {@code InputStream}
     * @throws IOException
     */
    public static InputStream getFileOrResource(String name) throws IOException {
        InputStream is = null;
        if (new File(name).isFile()) {
            is = new FileInputStream(name);
        } else {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        }
        if (is == null) {
            throw new IOException("Failed to locate " + name);
        }
        return is;
    }
}
