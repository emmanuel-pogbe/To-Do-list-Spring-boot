package com.shopleft.todo.service.interfaces;

import org.springframework.stereotype.Service;

import com.shopleft.todo.dto.TaskCreated;
import com.shopleft.todo.model.Task;

@Service
public interface TaskService {
    TaskCreated createTask(Task task);
}
