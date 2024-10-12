package org.example.threadPoolComponents;

import lombok.Getter;
import org.example.collections.Matrices;
import org.example.model.Matrix;
import org.example.model.Task;
import org.example.model.TaskType;
import org.example.shared.TaskQueue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ForkJoinPool;

@Getter
public class MatrixExtractor {

    private ForkJoinPool forkJoinPool = new ForkJoinPool();

    public void extractMatrixFromFile(File file) {
        if (file.length() == 0) {
            System.out.println("File is empty.");
            return;
        }
        Matrix matrix = new Matrix(file.getPath(), file.getName(), 0, 0, null);
        Matrices.getInstance().getMatrices().put(matrix.getFilePath(), matrix);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String firstLine = bufferedReader.readLine();
            String parts[] = firstLine.split(",");
            String matrixName = parts[0].split("=")[1];
            int rows = Integer.parseInt(parts[1].split("=")[1]);
            int columns = Integer.parseInt(parts[2].split("=")[1]);
            matrix.setName(matrixName);
            matrix.setNumberOfRows(rows);
            matrix.setNumberOfColumns(columns);
            matrix.setMatrix(new int[rows][columns]);
            Matrices.getInstance().updateMatrix(matrix);

            Matrix matrix1 = (Matrix) forkJoinPool.submit(new MatrixExtractWorker(Matrices.getInstance().getMatrices().get(matrix.getFilePath()),
                    0, file.length())).get();

            Matrices.getInstance().addMatrixByName(matrix1);

//            System.out.println("Matrica sa putanjom: " + matrix1.getFilePath() + " Broj redova: " + Matrices.getInstance().getMatrices().get(matrix.getFilePath()).getMatrix()[0].length);
//            System.out.println("Matrica sa putanjom: " + matrix1.getFilePath() + " Neko polje 0 0: " + Matrices.getInstance().getMatrices().get(matrix.getFilePath()).getMatrix()[0][0]);
//            System.out.println("Matrica sa putanjom: " + matrix1.getFilePath() + " Neko polje 2 2: " + Matrices.getInstance().getMatrices().get(matrix.getFilePath()).getMatrix()[2][2]);

            if (matrix1.getNumberOfColumns() != matrix1.getNumberOfRows()) {
                System.out.println("Matrix cannot be squared. " + matrix1.getName());
                return;
            }
            TaskQueue.getInstance().getTaskQueue().put(new Task(TaskType.MULTIPLY, new File(""), matrix1, matrix1, null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
