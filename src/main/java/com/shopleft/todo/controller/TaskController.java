package com.shopleft.todo.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
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

    private final String defaultPageSize = "5";
    private final String defaultPageNumber = "0";
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(path = "/user/{userId}")
    public PagedModel<Task> getPagedTasksByUser(
        @PathVariable Long userId,
        @RequestParam(required = false) String search,
        @RequestParam(required = false, defaultValue = defaultPageNumber) int page,
        @RequestParam(required = false, defaultValue = defaultPageSize) int size
    ) {
        if (search == null || search.isBlank()) {
            Page<Task> result = taskService.getTasksByUserId(userId,page,size);
            return new PagedModel<>(result);
        }
        Page<Task> result = taskService.findByTaskContains(userId, search.trim(),page,size);
        return new PagedModel<>(result);
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

    @GetMapping(path = "/user/{userId}/after")
    public PagedModel<Task> getTasksAfter(
        @PathVariable Long userId, 
        @RequestParam String date,
        @RequestParam(required = false, defaultValue = defaultPageNumber) int page,
        @RequestParam(required = false, defaultValue = defaultPageSize) int size
    ) {
        LocalDate minDate = LocalDate.parse(date);
        Page<Task> result = taskService.findByTaskAfter(userId, minDate,page,size);
        return new PagedModel<>(result);
    }

    @GetMapping(path = "/user/{userId}/before")
    public PagedModel<Task> getTasksBefore(
        @PathVariable Long userId, 
        @RequestParam String date,
        @RequestParam(required = false, defaultValue = defaultPageNumber) int page,
        @RequestParam(required = false, defaultValue = defaultPageSize) int size
    ) {
        LocalDate maxDate = LocalDate.parse(date);
        Page<Task> result = taskService.findByTaskBefore(userId, maxDate,page,size);
        return new PagedModel<>(result);
    }

    @GetMapping(path = "/user/{userId}/between")
    public PagedModel<Task> getTasksBetween(
        @PathVariable Long userId, 
        @RequestParam String startDate, 
        @RequestParam String endDate,
        @RequestParam(required = false, defaultValue = defaultPageNumber) int page,
        @RequestParam(required = false, defaultValue = defaultPageSize) int size
    ) {
        LocalDate minDate = LocalDate.parse(startDate);
        LocalDate maxDate = LocalDate.parse(endDate);
        Page<Task> result = taskService.findByTaskBetween(userId, minDate, maxDate,page,size);
        return new PagedModel<>(result);
    }
}
