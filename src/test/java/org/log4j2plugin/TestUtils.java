package org.log4j2plugin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestUtils {
    protected static boolean DO_CLEAN_LOG_FOLDER = true;

    public static Properties getPropertiesFromClasspath(String propFileName) throws IOException {
        InputStream inputStream = SystemUtils.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream == null) {
            throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
        }

        Properties props = new Properties();
        props.load(inputStream);
        return props;
    }

    protected static File getLoggingFolder() throws Exception {
        Properties properties = getPropertiesFromClasspath("system.properties");
        String logPath = properties.getProperty("log.path");
        return new File(logPath);
    }

    protected static void cleanLogFolder() throws Exception {
        if (!DO_CLEAN_LOG_FOLDER) {
            return;
        }

        // remove all test logs
        File logFolder = getLoggingFolder();
        if (logFolder.listFiles() == null) {
            return;
        }

        for (File file : logFolder.listFiles()) {
            try {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else if (file.isFile()) {
                    file.delete();
                }
            } catch (IOException e) {
                System.err.println("Exception during log clean-up:" + e.getMessage());
            }
        }
    }
}
