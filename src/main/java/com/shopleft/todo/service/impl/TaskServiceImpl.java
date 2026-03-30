package com.shopleft.todo.service.impl;

import java.time.LocalDate;
import java.util.Optional;

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
        Optional<User> userOptional = userRepository.findById(task.getUserId());
        if (userOptional.isEmpty()) {
            return new TaskCreated();
        }

        Task newTask = new Task(task.getTask());
        newTask.setUser(userOptional.get());
        Task savedTask = taskRepository.save(newTask);
        return new TaskCreated(savedTask.getId(), savedTask.getTask());
    }

    public Page<Task> getTasksByUserId(Long userId,int page, int size) {
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId,PageRequestCreator.createPageRequest(page,size));
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

    public Page<Task> findByTaskContains(Long userId, String description, int page, int size) {
        return taskRepository.findByTaskContains(userId,description, PageRequestCreator.createPageRequest(page, size));
    }

    public Page<Task> findByTaskAfter(Long userId, LocalDate minDate, int page, int size) {
        return taskRepository.findByTaskAfter(userId, minDate, PageRequestCreator.createPageRequest(page, size));
    }

    public Page<Task> findByTaskBefore(Long userId, LocalDate maxDate, int page, int size) {
        return taskRepository.findByTaskBefore(userId, maxDate, PageRequestCreator.createPageRequest(page, size));
    }

    public Page<Task> findByTaskBetween(Long userId, LocalDate minDate, LocalDate maxDate, int page, int size) {
        return taskRepository.findByTaskBetween(userId, minDate, maxDate, PageRequestCreator.createPageRequest(page, size));
    }
}
