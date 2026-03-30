package com.shopleft.todo.service.interfaces;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.shopleft.todo.dto.DeletedTask;
import com.shopleft.todo.dto.NewTask;
import com.shopleft.todo.dto.TaskCreated;
import com.shopleft.todo.dto.UpdatedTask;
import com.shopleft.todo.model.Task;

public interface TaskService {
    TaskCreated createTask(NewTask task);
    Page<Task> getTasksByUserId(Long userId,int page, int size);
    DeletedTask deleteTask(Long taskId);
    UpdatedTask updateTask(Long taskId, String taskDescription);
    Page<Task> findByTaskContains(Long userId,String description, int page, int size);
    Page<Task> findByTaskAfter(Long userId, LocalDate minDate, int page, int size);
    Page<Task> findByTaskBefore(Long userId, LocalDate maxDate, int page, int size);
    Page<Task> findByTaskBetween(Long userId, LocalDate minDate, LocalDate maxDate, int page, int size);
}


