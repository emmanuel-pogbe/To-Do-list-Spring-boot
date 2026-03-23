package com.shopleft.todo.service.impl;

import org.springframework.stereotype.Service;

import com.shopleft.todo.dto.NewTask;
import com.shopleft.todo.dto.TaskCreated;
import com.shopleft.todo.model.Task;
import com.shopleft.todo.repository.TaskRepository;
import com.shopleft.todo.service.interfaces.TaskService;

@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskCreated createTask(Task task) {
        taskRepository.save(task);
        return new TaskCreated(task.getId(),task.getTask());
    }
}
