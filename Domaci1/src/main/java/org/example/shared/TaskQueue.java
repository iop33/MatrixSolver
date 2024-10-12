package org.example.shared;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Getter
@Setter
public class TaskQueue {

    private static TaskQueue instance = null;

    private BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
    private TaskQueue() {
    }

    public static synchronized TaskQueue getInstance() {
        if (instance == null) {
            instance = new TaskQueue();
        }
        return instance;
    }

}
