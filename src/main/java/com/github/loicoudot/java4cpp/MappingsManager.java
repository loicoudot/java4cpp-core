package com.github.loicoudot.java4cpp;

import static com.github.loicoudot.java4cpp.Utils.newArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import javax.xml.bind.JAXB;

import com.github.loicoudot.java4cpp.configuration.Clazz;
import com.github.loicoudot.java4cpp.configuration.Mappings;
import com.github.loicoudot.java4cpp.configuration.Namespace;

public class MappingsManager {

    private final Context context;
    private final Mappings mappings = new Mappings();

    public MappingsManager(Context context) {
        this.context = context;
    }

    public void start() {
        addMappingsFromSettings();
        addClassToDoFromMappings();
    }

    private void addMappingsFromSettings() {
        if (!Utils.isNullOrEmpty(context.getSettings().getMappingsFile())) {
            for (String name : context.getSettings().getMappingsFile().split(";")) {
                try {
                    InputStream is = Utils.getFileOrResource(name);
                    Mappings mapping = JAXB.unmarshal(is, Mappings.class);
                    is.close();
                    addMappings(mapping);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read mappings from settings " + e.getMessage());
                }
            }
        }
    }

    private void addClassToDoFromMappings() {
        context.getFileManager().logInfo("adding classes to wrappe from mappings files");
        for (Clazz clazz : mappings.getClasses()) {
            context.addClassToDo(clazz.getClazz());
        }
    }

    /**
     * Add a mappings configuration bean to the actual context.
     * 
     * @param other
     *            the mappings bean to add
     */
    public void addMappings(Mappings other) {
        mappings.getKeywords().addAll(other.getKeywords());
        mappings.getClasses().addAll(other.getClasses());
        mappings.getNamespaces().addAll(other.getNamespaces());
    }

    public String escapeName(String name) {
        if (mappings.getKeywords().contains(name)) {
            return escapeName(name + '_');
        }
        return name;
    }

    /**
     * Transform the full qualified name of {@code clazz} by applying the rules
     * on namespace/package mappings. The mappings on the class name is not
     * applied here. Does not work on inner class.
     * 
     * @param clazz
     *            the class to get namespace from.
     * @return the associate namespace associate to {@code clazz}.
     */
    private List<String> getNamespaceMapping(Class<?> clazz) {
        int bestScore = 0;
        String bestNamespace = clazz.getName().replaceAll("\\.", "::");
        for (Namespace namespace : mappings.getNamespaces()) {
            if (namespace.getJavaPackage().length() > bestScore && clazz.getName().matches(namespace.getJavaPackage())) {
                bestScore = namespace.getJavaPackage().length();
                bestNamespace = Utils.isNullOrEmpty(namespace.getNamespace()) ? clazz.getSimpleName() : String.format("%s::%s", namespace.getNamespace(),
                        clazz.getSimpleName());
            }
        }
        return Arrays.asList(bestNamespace.split("::"));
    }

    /**
     * Return the final full qualified C++ name of the class. Apply the
     * namespace/package mapping, the class name mapping and escape all part by
     * the reserved words list. Works also for inner class.
     * 
     * @return the final full qualified C++ name.
     */
    public List<String> getNamespace(Class<?> clazz) {
        List<String> namespace;
        if (clazz.getEnclosingClass() == null) {
            namespace = getNamespaceMapping(clazz);
        } else {
            Class<?> enclosing = clazz;
            Deque<Class<?>> stack = new ArrayDeque<Class<?>>();
            while (enclosing.getEnclosingClass() != null) {
                stack.add(enclosing);
                enclosing = enclosing.getEnclosingClass();
            }
            namespace = getNamespace(enclosing);
            while (!stack.isEmpty()) {
                namespace.add(getMappings(stack.pollLast()).getCppName());
            }
        }
        List<String> escapedNamespace = newArrayList();
        for (String name : namespace) {
            escapedNamespace.add(escapeName(name));
        }
        escapedNamespace.set(escapedNamespace.size() - 1, getMappings(clazz).getCppName());
        return escapedNamespace;
    }

    private Clazz getClazz(Class<?> clazz) {
        for (Clazz other : mappings.getClasses()) {
            if (other.getClazz().equals(clazz))
                return other;
        }
        return null;
    }

    public MappingsHelper getMappings(Class<?> clazz) {
        return new MappingsHelper(clazz, getClazz(clazz), this);
    }
}
