package org.example.properties;

import java.io.FileInputStream;
import java.util.Properties;

public class AppProperties {
    private static AppProperties instance = null;
    private static Properties properties = null;
    private static String sysExplorerSleepTime = null;
    private static String startDir = null;
    private static String maximumFileChunkSize = null;
    private static String maximumRowsSize = null;

    private AppProperties() {
    }

    public static synchronized AppProperties getInstance() {
        if (instance == null) {
            instance = new AppProperties();
            properties = new Properties();
            try {
                properties.load(new FileInputStream("src/main/resources/app.properties"));
            } catch (Exception e) {
                System.out.println("Error while loading properties file.");
                e.printStackTrace();
            }

            sysExplorerSleepTime = properties.getProperty("sys_explorer_sleep_time");
            startDir = properties.getProperty("start_dir");
            maximumFileChunkSize = properties.getProperty("maximum_file_chunk_size");
            maximumRowsSize = properties.getProperty("maximum_rows_size");
        }
        return instance;
    }

    public int getExplorerSleepTime() {
        return Integer.parseInt(sysExplorerSleepTime);
    }

    public String getStartDir() {
        return startDir;
    }

    public int getMaximumFileChunkSize() {
        return Integer.parseInt(maximumFileChunkSize);
    }

    public int getMaximumRowsSize() {
        return Integer.parseInt(maximumRowsSize);
    }
}
