package com.skillmasters.server.http.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.skillmasters.server.http.response.TaskResponse;
import com.skillmasters.server.repository.TaskRepository;
import com.skillmasters.server.model.Task;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Tasks", description="planer's tasks")
public class TaskController
{
  @Autowired
  TaskRepository repository;

  @ApiOperation(value = "Get a list of available tasks", response = TaskResponse.class)
  @GetMapping("/tasks")
  public TaskResponse retrieve(@RequestParam(value="ids", defaultValue="") List<Long> ids)
  {
    return new TaskResponse().success( repository.findAll() );
  }

  @ApiOperation(value = "Create task", response = TaskResponse.class)
  @PostMapping("/tasks")
  public TaskResponse create(@RequestBody Task task)
  {
    return new TaskResponse().success( Arrays.asList(repository.save(task)) );
  }

  @ApiOperation(value = "Update task", response = TaskResponse.class)
  @PutMapping("/tasks/{id}")
  public TaskResponse update(@PathVariable Long id, @RequestBody Task task)
  {
    // task.setId(id);
    return new TaskResponse().success( Arrays.asList(repository.save(task)) );
  }

  @ApiOperation(value = "Delete task")
  @DeleteMapping("/tasks/{id}")
  public void delete(@PathVariable Long id)
  {
    repository.deleteById(id);
  }
}
