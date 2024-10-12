package org.example.normalComponents;

import org.example.collections.Matrices;
import org.example.model.Matrix;
import org.example.model.Task;
import org.example.model.TaskType;
import org.example.properties.AppProperties;
import org.example.shared.TaskQueue;
import org.example.threadPoolComponents.MatrixBrain;
import org.example.threadPoolComponents.MatrixExtractor;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        SystemExplorer systemExplorer = new SystemExplorer();
        TaskCoordinator taskCoordinator = new TaskCoordinator();
        MatrixExtractor matrixExtractor = new MatrixExtractor();
        MatrixBrain matrixBrain = new MatrixBrain();

        Thread systemExplorerThread = new Thread(systemExplorer);
        Thread taskCoordinatorThread = new Thread(taskCoordinator);

        startThreads(systemExplorerThread, taskCoordinatorThread);
        startDirectorySearch(systemExplorer);

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }

            String[] command = input.split(" ");

            switch (command[0]) {
                case "dir": {
                    if (command.length > 2) {
                        System.out.println("Invalid command.");
                        break;
                    }
                    systemExplorer.addDirectoryToExplorer(command[1]);
                    systemExplorer.setActive(new AtomicBoolean(true));
                    break;
                }
                case "info": {
                    if (command.length > 2 || command.length == 1) {
                        System.out.println("Invalid command.");
                        break;
                    }
                    matrixBrain.getInfo(command[1]);
                    break;
                }
                case "multiply": {
                    if (command.length > 4) {
                        System.out.println("Invalid command.");
                        break;
                    }
                    if (command.length > 2 && command[2].equals("-name")) {
                        String[] matrices = command[1].split(",");
                        try {
                            String firstMatrixName = matrices[0];
                            String secondMatrixName = matrices[1];
                            Matrix matrix1 = Matrices.getInstance().getMatricesByNames().get(firstMatrixName);
                            Matrix matrix2 = Matrices.getInstance().getMatricesByNames().get(secondMatrixName);
                            if (matrix1 == null || matrix2 == null) {
                                System.out.println("Matrix not found.");
                                break;
                            }
                            if (matrix1.getNumberOfColumns() != matrix2.getNumberOfRows()) {
                                System.out.println("Matrices cannot be multiplied. " + matrix1.getNumberOfColumns() + " != " + matrix2.getNumberOfRows());
                                break;
                            }
                            if (command[3] == null || command[3].equals("")) {
                                System.out.println("Invalid matrix name.");
                                break;
                            }
                            TaskQueue.getInstance().getTaskQueue().put(new Task(TaskType.MULTIPLY, new File(""), matrix1, matrix2, command[3]));
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid matrix names.");
                            break;
                        }
                    } else {
                        String[] matrices = command[1].split(",");
                        try {
                            String firstMatrixName = matrices[0];
                            String secondMatrixName = matrices[1];
                            Matrix matrix1 = Matrices.getInstance().getMatricesByNames().get(firstMatrixName);
                            Matrix matrix2 = Matrices.getInstance().getMatricesByNames().get(secondMatrixName);
                            if (matrix1 == null || matrix2 == null) {
                                System.out.println("Matrix not found.");
                                break;
                            }
                            if (matrix1.getNumberOfColumns() != matrix2.getNumberOfRows()) {
                                System.out.println("Matrices cannot be multiplied. " + matrix1.getNumberOfColumns() + " != " + matrix2.getNumberOfRows());
                                break;
                            }
                            TaskQueue.getInstance().getTaskQueue().put(new Task(TaskType.MULTIPLY, new File(""), matrix1, matrix2, null));
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid matrix names.");
                            break;
                        }
                    }
                }
                case "save": {
                    if (command.length > 5 || !command[1].equals("-name") || !command[3].equals("-file")) {
                        System.out.println("Invalid command.");
                        break;
                    }
                    matrixBrain.saveMatrixToFile(command[2], command[4]);
                    break;
                }
                case "clear": {
                    if (command.length > 2) {
                        System.out.println("Invalid command.");
                        break;
                    }
                    matrixBrain.clearMatrix(command[1]);
                    break;
                }
                case "stop": {
                    stopAllThreads(systemExplorer, taskCoordinator, matrixBrain, systemExplorerThread, taskCoordinatorThread);
                    System.exit(0);
                    break;
                }
                default: {
                    System.out.println("Invalid command.");
                }
            }
        }
    }

    private static void startDirectorySearch(SystemExplorer systemExplorer) {
        systemExplorer.addDirectoryToExplorer(AppProperties.getInstance().getStartDir());
    }

    private static void startThreads(Thread systemExplorerThread, Thread taskCoordinatorThread) {
        if (systemExplorerThread.isAlive() || taskCoordinatorThread.isAlive()) {
            return;
        }
        systemExplorerThread.start();
        taskCoordinatorThread.start();
    }

    private static void stopAllThreads(SystemExplorer systemExplorer,
                                       TaskCoordinator taskCoordinator,
                                       MatrixBrain matrixBrain,
                                       Thread systemExplorerThread,
                                       Thread taskCoordinatorThread) {

        systemExplorer.setActive(new AtomicBoolean(false));
        // Stopiranje taskCoordinatora
        taskCoordinator.setActive(new AtomicBoolean(false));
        try {
            TaskQueue.getInstance().getTaskQueue().put(new Task(TaskType.STOP, new File(""), null, null, null));
            systemExplorerThread.join();
            taskCoordinatorThread.join();
            matrixBrain.shutdown();
        } catch (InterruptedException e) {
            System.out.println("Error: Thread interrupted.");
//            e.printStackTrace();
        }
    }

}