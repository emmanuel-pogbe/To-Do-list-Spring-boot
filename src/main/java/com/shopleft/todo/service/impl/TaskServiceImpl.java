package com.shopleft.todo.service.impl;

import java.util.List;
import java.util.Optional;

import org.h2.tools.DeleteDbFiles;
import org.springframework.stereotype.Service;

import com.shopleft.todo.dto.DeletedTask;
import com.shopleft.todo.dto.NewTask;
import com.shopleft.todo.dto.TaskCreated;
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
        if (taskOptional.isEmpty()) {
            DeletedTask taskFailed = new DeletedTask();
            taskFailed.setDeleted(false);
            return taskFailed;
        }
        else {
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
}
