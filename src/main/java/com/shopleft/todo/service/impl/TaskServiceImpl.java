package com.shopleft.todo.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskCreated createTask(NewTask task) {
        Optional<User> userOptional = userRepository.findById(task.getUserId());
        if (userOptional.isEmpty()) {
            return new TaskCreated();
        }

        Task newTask = new Task(task.getTask());
        newTask.setUser(userOptional.get());
        Task savedTask = taskRepository.save(newTask);
        return new TaskCreated(savedTask.getId(), savedTask.getTask());
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public DeletedTask deleteTask(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            System.out.println("\n\n\nWE DID NOT FIND THE TASK\n\n\n");
            DeletedTask taskFailed = new DeletedTask();
            taskFailed.setDeleted(false);
            return taskFailed;
        }
        else {
            System.out.println("\n\n\nWE FOUND THE TASK\n\n\n");
            taskRepository.deleteTaskById(taskId);
            Task gottenTask = taskOptional.get();
            return new DeletedTask(
                gottenTask.getId(),
                gottenTask.getTask(),
                gottenTask.getCreatedAt(),
                true
            );
        }
    }

    public UpdatedTask updateTask(Long taskId, String taskDescription) {
        Optional<Task> gottenTask = taskRepository.findById(taskId);
        UpdatedTask result = new UpdatedTask();
        if (gottenTask.isPresent()) {
            Task taskToUpdate = gottenTask.get();
            taskToUpdate.setTask(taskDescription);
            taskRepository.save(taskToUpdate);

            result.setId(taskToUpdate.getId());
            result.setTask(taskDescription);
            result.setIsUpdated(true);
            result.setCreatedAt(taskToUpdate.getCreatedAt());
        }
        else {
            result.setIsUpdated(false);
        }
        return result;
    }

    public List<Task> findByTaskContains(Long userId, String description) {
        return taskRepository.findByTaskContains(userId,description);
    }

    public List<Task> findByTaskAfter(Long userId, LocalDate minDate) {
        return taskRepository.findByTaskAfter(userId, minDate);
    }

    public List<Task> findByTaskBefore(Long userId, LocalDate maxDate) {
        return taskRepository.findByTaskBefore(userId, maxDate);
    }

    public List<Task> findByTaskBetween(Long userId, LocalDate minDate, LocalDate maxDate) {
        return taskRepository.findByTaskBetween(userId, minDate, maxDate);
    }
}
