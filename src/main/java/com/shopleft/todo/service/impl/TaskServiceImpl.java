package com.shopleft.todo.service.impl;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.shopleft.todo.dto.DeletedTask;
import com.shopleft.todo.dto.NewTask;
import com.shopleft.todo.dto.TaskCreated;
import com.shopleft.todo.dto.UpdatedTask;
import com.shopleft.todo.model.Task;
import com.shopleft.todo.model.User;
import com.shopleft.todo.repository.TaskRepository;
import com.shopleft.todo.repository.UserRepository;
import com.shopleft.todo.service.interfaces.TaskService;
import com.shopleft.todo.utils.PageRequestCreator;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskCreated createTask(NewTask task) {
        User existingUser = userRepository
            .findById(task.getUserId())
            .orElseThrow(() -> new NoSuchElementException("User not found"));

        Task newTask = new Task(task.getTask());
        newTask.setUser(existingUser);
        Task savedTask = taskRepository.save(newTask);
        return new TaskCreated(savedTask.getId(), savedTask.getTask());
    }

    public Page<Task> getTasksByUserId(Long userId,int page, int size) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId,PageRequestCreator.createPageRequest(page,size));
    }

    public DeletedTask deleteTask(Long taskId) {
        Task gottenTask = taskRepository
            .findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task not found"));

        taskRepository.deleteTaskById(taskId);
        return new DeletedTask(
            gottenTask.getId(),
            gottenTask.getTask(),
            gottenTask.getCreatedAt(),
            true
        );
    }

    public UpdatedTask updateTask(Long taskId, String taskDescription) {
        Task taskToUpdate = taskRepository
            .findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task not found"));

        UpdatedTask result = new UpdatedTask();

        taskToUpdate.setTask(taskDescription);
        taskRepository.save(taskToUpdate);

        result.setId(taskToUpdate.getId());
        result.setTask(taskDescription);
        result.setIsUpdated(true);
        result.setCreatedAt(taskToUpdate.getCreatedAt());
        return result;
    }

    public Page<Task> findByTaskContains(Long userId, String description, int page, int size) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        return taskRepository.findByTaskContains(userId,description, PageRequestCreator.createPageRequest(page, size));
    }

    public Page<Task> findByTaskAfter(Long userId, LocalDate minDate, int page, int size) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        return taskRepository.findByTaskAfter(userId, minDate, PageRequestCreator.createPageRequest(page, size));
    }

    public Page<Task> findByTaskBefore(Long userId, LocalDate maxDate, int page, int size) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        return taskRepository.findByTaskBefore(userId, maxDate, PageRequestCreator.createPageRequest(page, size));
    }

    public Page<Task> findByTaskBetween(Long userId, LocalDate minDate, LocalDate maxDate, int page, int size) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
        return taskRepository.findByTaskBetween(userId, minDate, maxDate, PageRequestCreator.createPageRequest(page, size));
    }
}
