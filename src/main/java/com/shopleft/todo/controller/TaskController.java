package com.shopleft.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopleft.todo.dto.DeletedTask;
import com.shopleft.todo.dto.NewTask;
import com.shopleft.todo.dto.TaskCreated;
import com.shopleft.todo.dto.UpdatedTask;
import com.shopleft.todo.model.Task;
import com.shopleft.todo.service.interfaces.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(path = "/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Long userId, @RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return taskService.getTasksByUserId(userId);
        }
        return taskService.findByTaskContains(userId, search.trim());
    }
    
    @PostMapping(path = "/create")
    public TaskCreated createTask(@RequestBody NewTask task) {
        return taskService.createTask(task);
    }


    @DeleteMapping(path = "/delete/{id}")
    public DeletedTask deleleTask(@PathVariable("id") Long taskId) {
        return taskService.deleteTask(taskId);
    } 

    @PatchMapping(path = "/update/{id}")
    public UpdatedTask updatedTask(@PathVariable("id") Long taskId, @RequestBody Map<String,String> update) {
        return taskService.updateTask(taskId, update.get("description"));
    }
}
