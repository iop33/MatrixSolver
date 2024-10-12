package org.example.normalComponents;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Task;
import org.example.model.TaskType;
import org.example.properties.AppProperties;
import org.example.shared.TaskQueue;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class SystemExplorer implements Runnable {

    private CopyOnWriteArrayList<String> directories = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, Long> processedFiles = new ConcurrentHashMap<>();
    private AtomicBoolean active = new AtomicBoolean(true);
    private int explorerSleepTime = AppProperties.getInstance().getExplorerSleepTime();

    public void addDirectoryToExplorer(String directory) {
        directories.add(directory);
    }


    @Override
    public void run() {
        while (active.get()) {
            try {
                for (String directoryPath : directories) {
                    File directory = new File(directoryPath);
                    exploreDirectory(directory);
                }
                // Ponovo pretrazi direktorijume svakih 5 sekundi
                Thread.sleep(explorerSleepTime);
            } catch (InterruptedException e) {
                System.out.println("Ovde puca u sleep-u");
//                e.printStackTrace();
            }
        }
    }

    private void exploreDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    extractFiles(file);
                }
            }
        } else {
            System.out.println("Direktorijum ne postoji: " + directory.getAbsolutePath());
            directories.remove(directory.getPath());
        }
    }

    private void extractFiles(File file) {
        if (file.isDirectory() && !directories.contains(file.getPath())) {
            directories.add(file.getPath());
        } else if (file.getName().endsWith(".rix")
                && (!processedFiles.containsKey(file.getPath()) || file.lastModified() > processedFiles.get(file.getPath()))) {
            processedFiles.put(file.getPath(), file.lastModified());
            processFile(file);
        }
    }

    private void processFile(File file) {
        System.out.println("Pronađen .rix fajl: " + file.getAbsolutePath());
        Task task = new Task(TaskType.CREATE, file, null, null, null);
        // Dodavanje taska u task queue
        try {
            TaskQueue.getInstance().getTaskQueue().put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
//        System.out.println("Velicina " + TaskQueue.getInstance().getTaskQueue().size());
    }
}
