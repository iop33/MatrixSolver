package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Matrix {
    private String filePath;
    private String name;
    private int numberOfRows;
    private int numberOfColumns;
    private int[][] matrix;
}
