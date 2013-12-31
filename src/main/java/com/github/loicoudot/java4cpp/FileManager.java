package com.github.loicoudot.java4cpp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXB;

import com.github.loicoudot.java4cpp.configuration.Symbols;

/**
 * Manager for all interractions between java4cpp and the file system.
 * <p>
 * There are two log files:
 * <ul>
 * <li>{@code java4cpp.log} inside the target directory along the C++ proxies,
 * which contains the dependency tree of class.</li>
 * <li>maven plugin log, for logging informations and errors to the console.</li>
 * </ul>
 * Other file are the C++ proxies, and {@code java4cpp.hash} also inside the
 * target directory, which contains the MD5 hash value of the files.
 * 
 * @author Loic Oudot
 * 
 */
final class FileManager {
    private static final String JAVA4CPP_HASH = "java4cpp.hash";
    private static final String JAVA4CPP_LOG = "java4cpp.log";
    private static final int BUFFER_SIZE = 1024;
    private final Context context;
    private final ThreadLocal<String> indent = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "";
        };
    };
    private FileWriter java4cppLog;
    private File java4cppHash;
    private final Symbols imports = new Symbols();
    private final Symbols export = new Symbols();
    private List<File> oldFiles = new ArrayList<File>();
    private final Properties oldHashes = new Properties();
    private final Properties newHashes = new Properties();
    private int generated;
    private int skipped;
    private int deleted;
    private int imported;

    /**
     * A {@code FilenameFilter} to filter files other than {@code java4cpp.log}
     * and {@code java4cpp.hash}
     * 
     * @author Loic Oudot
     * 
     */
    final class SourceFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return !(name.equalsIgnoreCase(FileManager.JAVA4CPP_LOG) || name.equalsIgnoreCase(FileManager.JAVA4CPP_HASH));
        }
    }

    public FileManager(Context context) {
        this.context = context;
    }

    /**
     * Called before starting generating proxies files. Create the
     * {@code java4cpp.log} log file, and manage the {@code clean} and
     * {@code useHash} settings.
     */
    public void start() {
        addSymbolsFromSettings();
        File rep = new File(context.getSettings().getTargetPath());
        rep.mkdirs();
        try {
            java4cppLog = new FileWriter(new File(getPath(JAVA4CPP_LOG)));
        } catch (IOException e) {
            System.err.println("Can't create log file: " + e.getMessage());
        }
        try {
            File[] existings = rep.listFiles(new SourceFilter());
            if (existings != null) {
                oldFiles = new ArrayList<File>(Arrays.asList(existings));
            }
            java4cppHash = new File(getPath(JAVA4CPP_HASH));
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(java4cppHash));
            oldHashes.load(in);
            in.close();
            java4cppHash.delete();
        } catch (IOException e) {
            logInfo("no java4cpp.hash file, regenerating all files");
        }
    }

    /**
     * Reads the imports files to construct the list of import symbols to use.
     */
    private void addSymbolsFromSettings() {
        if (!Utils.isNullOrEmpty(context.getSettings().getImportsFile())) {
            for (String name : context.getSettings().getImportsFile().split(";")) {
                try {
                    InputStream is = Utils.getFileOrResource(name);
                    Symbols symbol = JAXB.unmarshal(is, Symbols.class);
                    is.close();
                    imports.getSymbols().addAll(symbol.getSymbols());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read imports: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Called after all the proxies are generated. Delete all the remaining
     * files in the target directory.
     */
    public void stop() {
        if (!Utils.isNullOrEmpty(context.getSettings().getExportFile())) {
            JAXB.marshal(export, new File(context.getSettings().getExportFile()));
        }
        if (context.getSettings().isClean()) {
            for (File file : oldFiles) {
                logInfo("deleting " + file.getName());
                if (!file.delete()) {
                    logInfo("failed");
                }
                ++deleted;
            }
        }
        try {
            final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(java4cppHash));
            newHashes.store(out, "Generated by java4cpp");
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate java4cpp.hash " + e.getMessage());
        }
        logInfo(String.format("generated: %d, imported: %d, skipped: %d, deleted: %d", generated, imported, skipped, deleted));
    }

    public void enter(String message) {
        synchronized (java4cppLog) {
            try {
                java4cppLog.append('[').append(Thread.currentThread().getName()).append("] ");
                java4cppLog.append(indent.get()).append(message).append('\n');
                java4cppLog.flush();
            } catch (IOException e) {
            }
        }
        indent.set(indent.get() + "  ");
    }

    public void leave() {
        indent.set(indent.get().substring(2));
    }

    /**
     * Write {@code message} inside the {@code java4cpp.log} file.
     * 
     * @param message
     *            the message to log
     */
    public void logInfo(String message) {
        synchronized (java4cppLog) {
            try {
                java4cppLog.append('[').append(Thread.currentThread().getName()).append("] ");
                java4cppLog.append(indent.get()).append(message).append('\n');
                java4cppLog.flush();
            } catch (IOException e) {
            }
        }
    }

    public void writeSourceFile(String fileName, StringWriter sw) {
        saveFile(new String(sw.getBuffer()), new File(getPath(fileName)));
    }

    public void copyFile(String fileName) throws IOException {
        String content = readFile(Utils.getFileOrResource(fileName));
        saveFile(content, new File(getPath(fileName)));
    }

    /**
     * Write the file {@code fileName} in the target directory with
     * {@code fileContent}. If {@code useHash} is true, then the file is save if
     * it's doesn't exist or if the content has changed.
     */
    private synchronized void saveFile(String fileContent, File fileName) {
        try {
            if (imports.getSymbols().contains(fileName.getName())) {
                logInfo("   imported " + fileName);
                ++imported;
            } else {
                export.getSymbols().add(fileName.getName());
                MessageDigest algo = MessageDigest.getInstance("MD5");
                algo.update(fileContent.getBytes());
                String md5 = bytesToHexString(algo.digest());
                newHashes.put(fileName.getName(), md5);

                if (!oldFiles.contains(fileName) || !md5.equals(oldHashes.getProperty(fileName.getName()))) {
                    fileName.setWritable(true);
                    BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(fileName));
                    writer.write(fileContent.getBytes());
                    fileName.setWritable(false);
                    writer.close();
                    ++generated;
                    logInfo("   generated " + fileName);
                } else {
                    ++skipped;
                    logInfo("   skipped " + fileName);
                }
                oldFiles.remove(fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file " + e.getMessage());
        }
    }

    private String getPath(String fileName) {
        return String.format("%s%s%s", context.getSettings().getTargetPath(), File.separator, fileName);
    }

    private String readFile(InputStream input) throws IOException {
        Appendable sb = new StringBuilder(BUFFER_SIZE);
        Reader reader = new InputStreamReader(input);

        char[] chars = new char[BUFFER_SIZE];
        int numRead;
        while ((numRead = reader.read(chars)) > -1) {
            sb.append(String.valueOf(chars, 0, numRead));
        }
        reader.close();
        return sb.toString();
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02X", b);
        }
        formatter.close();

        return sb.toString();
    }
}
