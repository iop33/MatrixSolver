package org.example.collections;

import org.example.model.Matrix;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Matrices {
    private static Matrices instance = null;
    private static ConcurrentHashMap<String, Matrix> matrices = null;
    private static ConcurrentHashMap<String, Matrix> matricesByNames = null;
    private static CopyOnWriteArrayList<Matrix> multipliedMatrices = null;

    private Matrices() {
    }

    public static synchronized Matrices getInstance() {
        if (instance == null) {
            instance = new Matrices();
            matrices = new ConcurrentHashMap<>();
            matricesByNames = new ConcurrentHashMap<>();
            multipliedMatrices = new CopyOnWriteArrayList<>();
        }
        return instance;
    }

    public ConcurrentMap<String, Matrix> getMatrices() {
        return matrices;
    }

    public ConcurrentMap<String, Matrix> getMatricesByNames() {
        return matricesByNames;
    }

    public void addMatrix(Matrix matrix) {
        matrices.put(matrix.getFilePath(), matrix);
    }

    public void updateMatrix(Matrix matrix) {
        matrices.put(matrix.getFilePath(), matrix);
    }

    public void updateMultipliedMatrix(Matrix matrix) {
        multipliedMatrices.stream().filter(m -> m.getName().equals(matrix.getName())).findFirst().ifPresent(m -> {
            multipliedMatrices.remove(m);
            multipliedMatrices.add(matrix);
        });
    }

    public void addMatrixByName(Matrix matrix) {
        matricesByNames.put(matrix.getName(), matrix);
    }

    public void addMultipliedMatrix(Matrix matrix) {
        multipliedMatrices.add(matrix);
    }

    public void removeMatrixByPath(String path) {
        Matrix matrix = matrices.get(path);
        if (matrix != null) {
            matricesByNames.remove(matrix.getName());
            matrices.remove(path);
            File file = new File(path);
            file.delete();
            System.out.println("Matrix removed.");
        } else {
            Matrix multipliedMatrix = multipliedMatrices.stream().filter(m -> m.getFilePath().equals(path)).findFirst().orElse(null);
            if (multipliedMatrix != null) {
                matricesByNames.remove(multipliedMatrix.getName());
                multipliedMatrices.remove(multipliedMatrix);
                File file = new File(path);
                file.delete();
                System.out.println("Matrix removed.");
            } else {
                System.out.println("Matrix on given path not found.");
            }
        }
    }

    public void removeMatrixByName(String name) {
        Matrix matrix = matricesByNames.get(name);
        if (matrix != null) {
            matrices.remove(matrix.getFilePath());
            matricesByNames.remove(name);
            multipliedMatrices.remove(matrix);
            File file = new File(matrix.getFilePath());
            file.delete();
            System.out.println("Matrix removed.");
        } else {
            System.out.println("Matrix not found.");
        }
    }
}
