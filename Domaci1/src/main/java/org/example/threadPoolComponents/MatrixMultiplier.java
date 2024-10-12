package org.example.threadPoolComponents;

import org.example.collections.Matrices;
import org.example.model.Matrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class MatrixMultiplier {
    private ForkJoinPool forkJoinPool = new ForkJoinPool();

    public Matrix multiply(Matrix firstMatrix, Matrix secondMatrix, String newMatrixName) {
        try {
            System.out.println("Mnozim matrice " + firstMatrix.getName() + " i " + secondMatrix.getName() + ".");
            MatrixMultiplierWorker matrixMultiplierWorker = new MatrixMultiplierWorker(
                    firstMatrix.getMatrix(),
                    secondMatrix.getMatrix(),
                    0,
                    firstMatrix.getMatrix().length);

            int[][] result = (int[][]) forkJoinPool.submit(matrixMultiplierWorker).get();
            System.out.println("Matrica pomnozena: " + firstMatrix.getName());
            System.out.println("Rezultat mnozenja matrica, broj redova: " + result.length + " broj kolona: " + result[0].length);
            System.out.println("Rezultat mnozenja matrica polje 0 0: " + result[0][0]);

            String matrixName = (newMatrixName != null) ? newMatrixName : firstMatrix.getName() + secondMatrix.getName();

            if (Matrices.getInstance().getMatricesByNames().get(matrixName) != null) {
                throw new Exception("Matrica sa imenom " + matrixName + " već postoji.");
            }
            Matrix resultMatrix = new Matrix("", matrixName, result.length, result[0].length, result);
            return resultMatrix;
        } catch (Exception e) {
            System.out.println("Došlo je do greške prilikom mnozenja.");
            return null;
        }
    }
}
