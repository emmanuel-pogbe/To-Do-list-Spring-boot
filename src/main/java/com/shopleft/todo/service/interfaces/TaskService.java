package com.shopleft.todo.service.interfaces;

import java.util.List;

import com.shopleft.todo.dto.NewTask;
import com.shopleft.todo.dto.TaskCreated;
import com.shopleft.todo.model.Task;

public interface TaskService {
    TaskCreated createTask(NewTask task);

    List<Task> getTasksByUserId(Long userId);
}
