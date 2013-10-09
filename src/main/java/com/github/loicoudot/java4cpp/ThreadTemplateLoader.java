package com.github.loicoudot.java4cpp;

import java.net.URL;

import freemarker.cache.URLTemplateLoader;

/**
 * A FreeMarker template loader that fetch templates in resource from class
 * loader of the current thread.
 * 
 * @author Loic Oudot
 * 
 */
class ThreadTemplateLoader extends URLTemplateLoader {
    /**
     * Search for the template named {@code name} in the resource file from the
     * class loader of the current thread.
     */
    @Override
    protected URL getURL(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }
}