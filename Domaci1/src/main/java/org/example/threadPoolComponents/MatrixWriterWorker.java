package org.example.threadPoolComponents;

import lombok.AllArgsConstructor;
import org.example.collections.Matrices;
import org.example.model.Matrix;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor
public class MatrixWriterWorker implements Runnable {

    private static final Lock lock = new ReentrantLock();
    private Matrix matrix;
    private String fileName;

    @Override
    public void run() {
        lock.lock();
        File file = new File("results/" + fileName);
        try {
            if (file.createNewFile()) {
                System.out.println("Fajl je kreiran: " + file.getName());
            } else {
                System.out.println("Fajl već postoji.");
            }
        } catch (IOException e) {
            System.out.println("Došlo je do greške.");
            e.printStackTrace();
        }

        if (matrix.getFilePath() == "") {
            matrix.setFilePath("results/" + fileName);
            Matrices.getInstance().updateMultipliedMatrix(matrix);
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results/" + fileName, true)))) {
            out.println("matrix_name=" + matrix.getName() + ", rows=" + matrix.getNumberOfRows() + ", cols=" + matrix.getNumberOfColumns());
            for (int i = 0; i < matrix.getNumberOfRows(); i++) {
                for (int j = 0; j < matrix.getNumberOfColumns(); j++) {
                    out.println(i + "," + j + " = " + matrix.getMatrix()[i][j]);
                }
            }
            System.out.println("Matrica je sačuvana u fajl.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
