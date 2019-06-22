package com.skillmasters.server.http.controller;

import java.util.Date;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.dsl.BooleanExpression;

import com.skillmasters.server.misc.OffsetPageRequest;
import com.skillmasters.server.http.response.TaskResponse;
import com.skillmasters.server.service.TaskService;
import com.skillmasters.server.service.EventService;
import com.skillmasters.server.model.User;
import com.skillmasters.server.model.Task;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.QTask;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Tasks", description="events' tasks")
public class TaskController
{
  @Autowired
  TaskService service;

  @Autowired
  EventService eventService;

  @ApiOperation(value = "Get a list of available tasks", response = TaskResponse.class)
  @GetMapping("/tasks")
  public TaskResponse retrieve(
    @AuthenticationPrincipal User user,
    @RequestParam(value="id", defaultValue="") List<Long> id,
    @RequestParam(value="event_id", required=false) Long eventId,
    @RequestParam(value="created_from", required=false) Long createdFrom,
    @RequestParam(value="created_to", required=false) Long createdTo,
    @RequestParam(value="updated_from", required=false) Long updatedFrom,
    @RequestParam(value="updated_to", required=false) Long updatedTo,
    @RequestParam(value="count", defaultValue="100") int count,
    @RequestParam(value="offset", defaultValue="0") long offset
  ) {
    QTask qTask = QTask.task;
    BooleanExpression query = qTask.event.ownerId.eq(user.getId());

    if (id.size() > 0) {
      query = qTask.id.in(id).and(query);
    }

    if (eventId != null) {
      query = qTask.event.id.eq(eventId).and(query);
    }

    if (createdFrom != null) {
      query = qTask.createdAt.goe(new Date(createdFrom)).and(query);
    }

    if (createdTo != null) {
      query = qTask.createdAt.loe(new Date(createdTo)).and(query);
    }

    if (updatedFrom != null) {
      query = qTask.updatedAt.goe(new Date(updatedFrom)).and(query);
    }

    if (updatedTo != null) {
      query = qTask.updatedAt.loe(new Date(updatedTo)).and(query);
    }

    return new TaskResponse().success( service.getByQuery(query, new OffsetPageRequest(offset, count)) );
  }

  @ApiOperation(value = "Get task by id", response = TaskResponse.class)
  @GetMapping("/tasks/{id}")
  public TaskResponse retrieveById(@PathVariable Long id)
  {
    Task entity = service.getById(id);
    if (entity == null) {
      return new TaskResponse().error(404, "Task not found");
    }
    return new TaskResponse().success(entity);
  }

  @ApiOperation(value = "Create task", response = TaskResponse.class)
  @PostMapping("/tasks")
  public TaskResponse create(
    @RequestParam(value="event_id", required=true) Long eventId,
    @RequestBody Task task
  ) {
    Event entity = eventService.getById(eventId);
    if (entity == null) {
      return new TaskResponse().error(404, "Event not found");
    }
    task.setEvent(entity);
    return new TaskResponse().success( service.save(task) );
  }

  @ApiImplicitParams(
    @ApiImplicitParam(
      name = "updates",
      value = "Object with updated values for Task",
      required = true,
      dataType = "Task"
    )
  )
  @ApiOperation(value = "Update task", response = TaskResponse.class)
  @PatchMapping("/tasks/{id}")
  public TaskResponse update(@PathVariable Long id, @RequestBody Map<String, Object> updates)
  {
    Task entity = service.getById(id);
    if (entity == null) {
      return new TaskResponse().error(404, "Task not found");
    }
    return new TaskResponse().success( service.update(entity, updates) );
  }

  @ApiOperation(value = "Delete task")
  @DeleteMapping("/tasks/{id}")
  public TaskResponse delete(@PathVariable Long id)
  {
    Task entity = service.getById(id);
    if (entity == null) {
      return new TaskResponse().error(404, "Task not found");
    }
    service.delete(entity);
    return new TaskResponse().success();
  }
}
