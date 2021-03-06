package com.skillmasters.server.http.controller;

import java.util.Date;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import org.springframework.validation.Validator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import com.skillmasters.server.misc.OffsetPageRequest;
import com.skillmasters.server.http.response.TaskResponse;
import com.skillmasters.server.service.TaskService;
import com.skillmasters.server.service.EventService;
import com.skillmasters.server.service.PermissionService;
import com.skillmasters.server.model.*;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Tasks", description="events' tasks")
public class TaskController
{
  @Autowired
  TaskService taskService;

  @Autowired
  EventService eventService;

  @Autowired
  PermissionService permissionService;

  @Autowired
  Validator validator;

  @PersistenceContext
  EntityManager entityManager;

  @ApiOperation(value = "Get a list of available tasks", response = TaskResponse.class)
  @GetMapping("/tasks")
  public TaskResponse retrieve(
    @AuthenticationPrincipal User user,
    @ApiParam(value = "Array of tasks's id")
    @RequestParam(value="id", defaultValue="") List<Long> id,
    @ApiParam(value = "Id of linked event")
    @RequestParam(value="event_id", required=false) Long eventId,
    @ApiParam(value = "Id of parent task")
    @RequestParam(value="parent_id", required=false) Long parentId,
    @ApiParam(value = "Task's status")
    @RequestParam(value="status", required=false) String status,
    @ApiParam(value = "Task's deadline, timestamp")
    @RequestParam(value="deadline", required=false) Long deadlineTo,
    @ApiParam(value = "Timestamp after that task was created")
    @RequestParam(value="created_from", required=false) Long createdFrom,
    @ApiParam(value = "Timestamp before that task was created")
    @RequestParam(value="created_to", required=false) Long createdTo,
    @ApiParam(value = "Timestamp after that task was updated")
    @RequestParam(value="updated_from", required=false) Long updatedFrom,
    @ApiParam(value = "Timestamp before that task was updated")
    @RequestParam(value="updated_to", required=false) Long updatedTo,
    @ApiParam(value = "Pagination offset")
    @RequestParam(value="offset", defaultValue="0") long offset,
    @ApiParam(value = "Count of tasks to retrieve")
    @RequestParam(value="count", defaultValue="100") int count
  ) {
    JPAQuery query = generateGetQuery(user, id, eventId, parentId, status, deadlineTo,
                                      createdFrom, createdTo, updatedFrom, updatedTo);
    return new TaskResponse().success( taskService.getByQuery(query, new OffsetPageRequest(offset, count)) );
  }

  @ApiOperation(value = "Get task by id", response = TaskResponse.class)
  @GetMapping("/tasks/{id}")
  public TaskResponse retrieveById(@PathVariable Long id)
  {
    Task entity = taskService.getById(id);
    if (entity == null) {
      return new TaskResponse().error(404, "Task not found");
    }
    return new TaskResponse().success(entity);
  }

  @ApiOperation(value = "Create task", response = TaskResponse.class)
  @PostMapping("/tasks")
  public TaskResponse create(
    @RequestParam(value="event_id", required=true) Long eventId,
    @RequestBody @Validated Task task,
    BindingResult binding
  ) {
    if (binding.hasErrors()) {
      return new TaskResponse().error(400, binding.getAllErrors().get(0).getDefaultMessage());
    }

    Event entity = eventService.getById(eventId);
    if (entity == null) {
      return new TaskResponse().error(404, "Event not found");
    }
    task.setEvent(entity);
    eventService.save(entity);
    taskService.save(task);
    return new TaskResponse().success(task);
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
  public TaskResponse update(
    @PathVariable Long id,
    @RequestBody Map<String, Object> updates,
    BindingResult binding
  ) {
    Task entity = taskService.getById(id);
    if (entity == null) {
      return new TaskResponse().error(404, "Task not found");
    }
    taskService.update(entity, updates);
    validator.validate(entity, binding);
    if (binding.hasErrors()) {
      return new TaskResponse().error(400, binding.getAllErrors().get(0).getDefaultMessage());
    }
    return new TaskResponse().success( taskService.save(entity) );
  }

  @ApiOperation(value = "Delete task")
  @DeleteMapping("/tasks/{id}")
  public TaskResponse delete(@PathVariable Long id)
  {
    Task entity = taskService.getById(id);
    if (entity == null) {
      return new TaskResponse().error(404, "Task not found");
    }
    taskService.delete(entity);
    return new TaskResponse().success();
  }

  private JPAQuery generateGetQuery(
    User user,
    List<Long> id,
    Long eventId,
    Long parentId,
    String status,
    Long deadlineTo,
    Long createdFrom,
    Long createdTo,
    Long updatedFrom,
    Long updatedTo
  ) {
    QTask qTask = QTask.task;
    QPermission qPermission = QPermission.permission;
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qTask);

    String userId = user.getId();
    BooleanExpression where = qTask.event.ownerId.eq(userId)
        .or(qTask.id.stringValue().eq(qPermission.entityId))
        .or(qTask.event.ownerId.eq(qPermission.entityId))
        .or(qTask.event.id.stringValue().eq(qPermission.entityId).and(qPermission.name.eq("READ_EVENT")));

    BooleanExpression hasPermission = permissionService.getHasPermissionQuery(userId, "READ_TASK")
        .or(permissionService.getHasPermissionQuery(userId, "READ_EVENT"));
    query.leftJoin(qPermission).on(hasPermission);
    query.groupBy(qTask.id);

    if (id.size() > 0) {
      where = qTask.id.in(id).and(where);
    }

    if (eventId != null) {
      where = qTask.event.id.eq(eventId).and(where);
    }

    if (parentId != null) {
      where = qTask.parentId.eq(parentId).and(where);
    }

    if (status != null) {
      where = qTask.status.eq(status).and(where);
    }

    if (deadlineTo != null) {
      where = qTask.deadlineAt.goe(new Date(deadlineTo)).and(where);
    }

    if (createdFrom != null) {
      where = qTask.createdAt.goe(new Date(createdFrom)).and(where);
    }

    if (createdTo != null) {
      where = qTask.createdAt.loe(new Date(createdTo)).and(where);
    }

    if (updatedFrom != null) {
      where = qTask.updatedAt.goe(new Date(updatedFrom)).and(where);
      query.orderBy(qTask.updatedAt.asc());
    }

    if (updatedTo != null) {
      where = qTask.updatedAt.loe(new Date(updatedTo)).and(where);
    }

    query.where(where);
    return query;
  }
}
