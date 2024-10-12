package org.example.threadPoolComponents;

import org.example.collections.Matrices;
import org.example.model.Matrix;
import org.example.properties.AppProperties;

import java.io.RandomAccessFile;
import java.util.concurrent.RecursiveTask;

public class MatrixExtractWorker extends RecursiveTask {

    private Matrix matrix;
    private String filePath;
    private long start;
    private long end;

    public MatrixExtractWorker(Matrix matrix, long start, long end) {
        this.matrix = matrix;
        this.filePath = matrix.getFilePath();
        this.start = start;
        this.end = end;
    }

    @Override
    protected Object compute() {
        long length = end - start;
        if (length <= AppProperties.getInstance().getMaximumFileChunkSize()) {
            processChunk(matrix, start, end); // Obrada dela datoteke
        } else {
            long mid = start + length / 2;
            MatrixExtractWorker leftTask = new MatrixExtractWorker(matrix, start, mid);
            MatrixExtractWorker rightTask = new MatrixExtractWorker(matrix, mid, end);

            leftTask.fork(); // Pokreće levi zadatak asinhrono
            rightTask.compute(); // Izvršava desni zadatak sinhrono
            leftTask.join();
        }
        return matrix;
    }

    private void processChunk(Matrix matrix, long start, long end) {

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(matrix.getFilePath(), "r")) {
            randomAccessFile.seek(start);

            if (start != 0) {
                randomAccessFile.readLine();
            }

            String line;
            while ((line = randomAccessFile.readLine()) != null && randomAccessFile.getFilePointer() <= end) {
                if (line.trim().isEmpty() || randomAccessFile.getFilePointer() > end || line.contains("matrix_name")) continue;

                if (Matrices.getInstance().getMatrices().get(matrix.getFilePath()) != null) {
                    String[] parts = line.split(" = ");
                    int value = Integer.parseInt(parts[1].trim());
                    String[] indices = parts[0].split(",");
                    int row = Integer.parseInt(indices[0].trim());
                    int col = Integer.parseInt(indices[1].trim());
                    if (row >= matrix.getNumberOfRows() || col >= matrix.getNumberOfColumns()) {
                        System.out.println("Invalid matrix dimensions.");
                        return;
                    }
                    matrix.getMatrix()[row][col] = value;
                    Matrices.getInstance().updateMatrix(matrix);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
