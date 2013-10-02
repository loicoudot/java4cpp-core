package com.github.loicoudot.java4cpp;

import java.net.URL;

import freemarker.cache.URLTemplateLoader;

class ThreadTemplateLoader extends URLTemplateLoader {
    @Override
    protected URL getURL(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }
}