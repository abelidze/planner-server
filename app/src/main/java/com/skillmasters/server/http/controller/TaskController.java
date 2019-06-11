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
import com.skillmasters.server.repository.TaskRepository;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.User;
import com.skillmasters.server.model.Task;
import com.skillmasters.server.model.QTask;
import com.skillmasters.server.model.QEvent;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Tasks", description="events' tasks")
public class TaskController
{
  @Autowired
  TaskRepository repository;

  @Autowired
  EventRepository eventRepository;

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
    BooleanExpression query = null;

    if (id.size() > 0) {
      query = qTask.id.in(id).and(query);
    }

    if (eventId != null) {
      query = qTask.event.id.eq(eventId).and(qTask.event.ownerId.eq(user.getId())).and(query);
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

    return new TaskResponse().success( repository.findAll(query, new OffsetPageRequest(offset, count)) );
  }

  @ApiOperation(value = "Create task", response = TaskResponse.class)
  @PostMapping("/tasks")
  public TaskResponse create(
    @AuthenticationPrincipal User user,
    @RequestParam(value="event_id", required=true) Long eventId,
    @RequestBody Task task
  ) {
    QEvent qEvent = QEvent.event;
    if (!eventRepository.exists( qEvent.id.eq(eventId).and(qEvent.ownerId.eq(user.getId())) )) {
      return new TaskResponse().error(404, "Event not found or you don't have access to it");
    }
    task.setEvent( eventRepository.getOne(eventId) );
    return new TaskResponse().success( Arrays.asList(repository.save(task)) );
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
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    @RequestBody Map<String, Object> updates
  ) {
    QTask qTask = QTask.task;
    if (!repository.exists( qTask.id.eq(id).and(qTask.event.ownerId.eq(user.getId())) )) {
      return new TaskResponse().error(404, "Task not found or you don't have access to it");
    }

    Task entity = repository.findById(id).get();
    updates.forEach((k, v) -> {
      String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, k);
      Field field = ReflectionUtils.findField(Task.class, fieldName);
      if (field != null) {
        ReflectionUtils.makeAccessible(field);
        final Class<?> type = field.getType();
        if (type.equals(Long.class)) {
          ReflectionUtils.setField(field, entity, ((Number) v).longValue());
        } else if (type.equals(Date.class)) {
          ReflectionUtils.setField(field, entity, new Date( ((Number) v).longValue() ));
        } else {
          ReflectionUtils.setField(field, entity, v);
        }
      }
    });
    return new TaskResponse().success(Arrays.asList( repository.save(entity) ));
  }

  @ApiOperation(value = "Delete task")
  @DeleteMapping("/tasks/{id}")
  public TaskResponse delete(@AuthenticationPrincipal User user, @PathVariable Long id)
  {
    QTask qTask = QTask.task;
    if (!repository.exists( qTask.id.eq(id).and(qTask.event.ownerId.eq(user.getId())) )) {
      return new TaskResponse().error(404, "Task not found or you don't have access to it");
    }
    repository.deleteById(id);
    return new TaskResponse().ok("ok");
  }
}
