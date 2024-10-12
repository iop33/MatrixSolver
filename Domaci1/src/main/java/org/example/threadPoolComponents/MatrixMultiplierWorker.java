package org.example.threadPoolComponents;

import org.example.properties.AppProperties;

import java.util.concurrent.RecursiveTask;

public class MatrixMultiplierWorker extends RecursiveTask {
    private int[][] A;
    private int[][] B;
    private int startRow;
    private int endRow;
    private static final int THRESHOLD = AppProperties.getInstance().getMaximumRowsSize();

    public MatrixMultiplierWorker(int[][] A, int[][] B, int startRow, int endRow) {
        this.A = A;
        this.B = B;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    protected int[][] compute() {
        int numRows = endRow - startRow;

        if (numRows <= THRESHOLD) {
            // Dovoljno mali zadatak za direktan rad
            return multiplySegment(A, B, startRow, endRow);
        } else {
            // Zadatak je prevelik, delimo na manje segmente
            int midRow = startRow + (numRows / 2);
            MatrixMultiplierWorker task1 = new MatrixMultiplierWorker(A, B, startRow, midRow);
            MatrixMultiplierWorker task2 = new MatrixMultiplierWorker(A, B, midRow, endRow);

            task1.fork();
            int[][] result2 = task2.compute();
            int[][] result1 = (int[][]) task1.join();

            // Spajanje rezultata
            return mergeResults(result1, result2, midRow - startRow);
        }
    }

    private int[][] multiplySegment(int[][] A, int[][] B, int startRow, int endRow) {
        int numColsB = B[0].length;
        int[][] result = new int[endRow - startRow][numColsB];

        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < numColsB; j++) {
                for (int k = 0; k < B.length; k++) {
                    result[i - startRow][j] += A[i][k] * B[k][j];
                }
            }
        }

        return result;
    }

    private int[][] mergeResults(int[][] result1, int[][] result2, int splitRow) {
        int totalRows = result1.length + result2.length;
        int[][] result = new int[totalRows][];

        System.arraycopy(result1, 0, result, 0, result1.length);
        System.arraycopy(result2, 0, result, result1.length, result2.length);

        return result;
    }
}
