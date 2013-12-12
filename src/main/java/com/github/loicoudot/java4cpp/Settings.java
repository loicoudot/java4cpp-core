package com.github.loicoudot.java4cpp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class for managing the settings of java4cpp. The settings are fetched in this
 * order :
 * <ol>
 * <li>Internal default values</li>
 * <li>System properties</li>
 * <li>.property file passed on the first argument in the command line</li>
 * </ol>
 * 
 * @author Loic Oudot
 * 
 */
public final class Settings {

    /**
     * Define the path where the resulting proxies files are generated.
     */
    private static final String TARGET_PATH = "java4cpp.targetPath";
    /**
     * A comma separated list of jar files to process
     */
    private static final String JAR_FILES = "java4cpp.jarFiles";
    /**
     * A comma separated list of xml files containing the mappings between the
     * Java clases and the C++ proxies
     */
    private static final String MAPPINGS_FILE = "java4cpp.mappingsFile";
    private static final String TEMPLATES_FILE = "java4cpp.templatesFile";
    /**
     * If true, remove the unnecessary files in the target folder
     */
    private static final String CLEAN = "java4cpp.clean";
    /**
     * If true, use the java4ccp.hash files to generate only the modified
     * proxies files. If false, generate all the proxies files.
     */
    private static final String USE_HASH = "java4cpp.useHash";
    /**
     * Size of the thread pool for generating the proxies files.
     */
    private static final String NB_THREAD = "java4cpp.nbThread";
    private String targetPath;
    private String jarFiles;
    private String mappingsFile;
    private String templatesFile;
    private boolean clean;
    private boolean useHash;
    private int nbThread;

    /**
     * Initialize the settings with the internal default values then with the
     * system properties values.
     */
    public Settings() {
        initFromProperties(System.getProperties());
    }

    /**
     * Initialize the settings with the internal default values then with the
     * system properties values and finally with the properties file pointed by
     * {@code propertiesFile}.
     * 
     * @param propertiesFile
     *            path to a properties file.
     */
    public Settings(String propertiesFile) {
        initFromPropertiesFile(propertiesFile);
    }

    /**
     * Helper constructor to manage the command line arguments. Initialize the
     * settings with the internal default values then with the system properties
     * values and finally with the properties file pointed by {@code args[0]} if
     * present.
     * 
     * @param args
     *            the command line arguments.
     */
    public Settings(String[] args) {
        if (args.length > 0) {
            initFromPropertiesFile(args[0]);
        } else {
            initFromProperties(System.getProperties());
        }
    }

    private void initFromProperties(Properties properties) {
        setTargetPath(properties.getProperty(TARGET_PATH, "."));
        setJarFiles(properties.getProperty(JAR_FILES, ""));
        setMappingsFile(properties.getProperty(MAPPINGS_FILE, ""));
        setTemplatesFile(properties.getProperty(TEMPLATES_FILE, ""));
        setClean(Boolean.valueOf(properties.getProperty(CLEAN, "false")));
        setUseHash(Boolean.valueOf(properties.getProperty(USE_HASH, "true")));
        setNbThread(Integer.valueOf(properties.getProperty(NB_THREAD, "1")));
    }

    private void initFromPropertiesFile(String propertiesFile) {
        Properties properties = (Properties) System.getProperties().clone();
        try {
            final FileInputStream inStream = new FileInputStream(propertiesFile);
            properties.load(inStream);
            inStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read properties " + e.getMessage());
        }

        initFromProperties(properties);
    }

    /**
     * @return the targetPath
     */
    public String getTargetPath() {
        return targetPath;
    }

    /**
     * @param targetPath
     *            the targetPath to set
     */
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    /**
     * @return the jarFiles
     */
    public String getJarFiles() {
        return jarFiles;
    }

    /**
     * @param jarFiles
     *            the jarFiles to set
     */
    public void setJarFiles(String jarFiles) {
        this.jarFiles = jarFiles;
    }

    /**
     * @return the mappingsFile
     */
    public String getMappingsFile() {
        return mappingsFile;
    }

    /**
     * @param mappingsFile
     *            the mappingsFile to set
     */
    public void setMappingsFile(String mappingsFile) {
        this.mappingsFile = mappingsFile;
    }

    /**
     * @return the templatesFile
     */
    public String getTemplatesFile() {
        return templatesFile;
    }

    /**
     * @param templatesFile
     *            the templatesFile to set
     */
    public void setTemplatesFile(String templatesFile) {
        this.templatesFile = templatesFile;
    }

    /**
     * @return the clean
     */
    public boolean isClean() {
        return clean;
    }

    /**
     * @param clean
     *            the clean to set
     */
    public void setClean(boolean clean) {
        this.clean = clean;
    }

    /**
     * @return the useHash
     */
    public boolean isUseHash() {
        return useHash;
    }

    /**
     * @param useHash
     *            the useHash to set
     */
    public void setUseHash(boolean useHash) {
        this.useHash = useHash;
    }

    /**
     * @return the nbThread
     */
    public int getNbThread() {
        return nbThread;
    }

    /**
     * @param nbThread
     *            the nbThread to set
     */
    public void setNbThread(int nbThread) {
        this.nbThread = nbThread;
    }
}
