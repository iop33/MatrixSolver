package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
@AllArgsConstructor
public class Task implements ITask {

    private TaskType taskType;

    private File file;

    private Matrix firstMatrix = null;

    private Matrix secondMatrix = null;

    private String newMatrixName = null;

    @Override
    public TaskType getType() {
        return taskType;
    }
}
