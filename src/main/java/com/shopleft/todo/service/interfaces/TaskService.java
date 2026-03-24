package com.shopleft.todo.service.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.shopleft.todo.dto.DeletedTask;
import com.shopleft.todo.dto.NewTask;
import com.shopleft.todo.dto.TaskCreated;
import com.shopleft.todo.dto.UpdatedTask;
import com.shopleft.todo.model.Task;

public interface TaskService {
    TaskCreated createTask(NewTask task);
    List<Task> getTasksByUserId(Long userId);
    DeletedTask deleteTask(Long taskId);
    UpdatedTask updateTask(Long taskId, String taskDescription);
    List<Task> findByTaskContains(Long userId,String description);
    List<Task> findByTaskAfter(Long userId, LocalDate minDate);
    List<Task> findByTaskBefore(Long userId, LocalDate maxDate);
    List<Task> findByTaskBetween(Long userId, LocalDate minDate, LocalDate maxDate);

}

