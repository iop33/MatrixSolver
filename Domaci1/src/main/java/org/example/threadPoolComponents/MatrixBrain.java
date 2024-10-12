package org.example.threadPoolComponents;

import org.example.collections.Matrices;
import org.example.model.Matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixBrain {

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private MatrixMultiplier matrixMultiplier = new MatrixMultiplier();

    public MatrixBrain() {
    }

    public void getInfo(String command) {
        if (command.equals("-all")) {
            Matrices.getInstance().getMatricesByNames().forEach((k, v) -> {
                System.out.println("Matrix name: " + v.getName() + " path: "
                        + v.getFilePath() + " rows: " + v.getNumberOfRows()
                        + " columns: " + v.getNumberOfColumns());
            });
            return;
        }
        Matrix matrix = Matrices.getInstance().getMatricesByNames().get(command);
        if (matrix == null) {
            System.out.println("Matrix not found.");
            return;
        }
        System.out.println("Matrix name: " + matrix.getName() + " path: "
                + matrix.getFilePath() + " rows: " + matrix.getNumberOfRows()
                + " columns: " + matrix.getNumberOfColumns());
    }

    public void multiplyMatrices(Matrix firstMatrix, Matrix secondMatrix, String newMatrixName) {
        Matrix newMatrix = matrixMultiplier.multiply(firstMatrix, secondMatrix, newMatrixName);
        if (newMatrix == null) {
            System.out.println("Matrix multiplication failed.");
            return;
        }
        registerNewMatrix(newMatrix);
        System.out.println("New matrix: " + newMatrix.getName());
    }

    public void registerNewMatrix(Matrix matrix) {
        Matrices.getInstance().addMultipliedMatrix(matrix);
        Matrices.getInstance().addMatrixByName(matrix);
    }

    public void saveMatrixToFile(String matrixName, String fileName) {
        // Save matrix to file
        if (Matrices.getInstance().getMatricesByNames().get(matrixName) == null) {
            System.out.println("Matrix not found.");
            return;
        }
        executor.execute(new MatrixWriterWorker(Matrices.getInstance().getMatricesByNames().get(matrixName), fileName));
    }

    public void clearMatrix(String name) {
        if (name.endsWith(".rix")) {
            Matrices.getInstance().removeMatrixByPath(name);
        } else {
            Matrices.getInstance().removeMatrixByName(name);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
