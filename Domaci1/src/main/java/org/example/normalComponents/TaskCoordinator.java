package org.example.normalComponents;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Task;
import org.example.model.TaskType;
import org.example.shared.TaskQueue;
import org.example.threadPoolComponents.MatrixBrain;
import org.example.threadPoolComponents.MatrixExtractor;

import java.util.concurrent.atomic.AtomicBoolean;

// Cita iz TaskQueue
@Getter
@Setter
public class TaskCoordinator implements Runnable {

    private AtomicBoolean active = new AtomicBoolean(true);
    private MatrixExtractor matrixExtractor = new MatrixExtractor();
    private MatrixBrain matrixBrain = new MatrixBrain();

    @Override
    public void run() {
        try {
            while (active.get()) {
//                System.out.println("Usao u task coordinator.");
                Task task = TaskQueue.getInstance().getTaskQueue().take();
                if (task.getTaskType().equals(TaskType.STOP)) {
                    System.out.println("TaskCoordinator je preuzeo STOP zadatak.");
                    break;
                }
//                System.out.println("TaskCoordinator je preuzeo zadatak.");
                processTask(task);
            }
            matrixExtractor.getForkJoinPool().shutdown();
        } catch (InterruptedException e) {
            System.out.println("TaskCoordinator je prekinut.");
        }
    }

    private void processTask(Task task) {
        if (task.getTaskType().equals(TaskType.CREATE)) {
            matrixExtractor.extractMatrixFromFile(task.getFile());
        } else if (task.getTaskType().equals(TaskType.MULTIPLY)) {
            matrixBrain.multiplyMatrices(task.getFirstMatrix(), task.getSecondMatrix(), task.getNewMatrixName());
        }
    }
}
