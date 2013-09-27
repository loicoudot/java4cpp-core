package com.github.loicoudot.java4cpp;

public class Joiner {
    /**
     * Returns a joiner which automatically places {@code separator} between
     * consecutive elements.
     */
    public static Joiner on(String separator) {
        return new Joiner(separator);
    }

    private final String separator;

    private Joiner(String separator) {
        this.separator = separator;
    }

    /**
     * Returns a string containing the string representation of each of
     * {@code parts}, using the previously configured separator between each.
     */
    public final String join(Object[] parts) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Object object : parts) {
            sb.append(sep).append(object.toString());
            sep = separator;
        }
        return sb.toString();
    }
}
