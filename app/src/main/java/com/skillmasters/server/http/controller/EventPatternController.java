package com.skillmasters.server.http.controller;

import java.util.Date;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import org.springframework.validation.Validator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.misc.OffsetPageRequest;
import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.repository.EventPatternRepository;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.service.PermissionService;
import com.skillmasters.server.service.EventPatternService;
import com.skillmasters.server.service.EventService;
import com.skillmasters.server.model.*;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Patterns", description="events' patterns")
public class EventPatternController
{
  @Autowired
  EventPatternService service;

  @Autowired
  PermissionService permissionService;

  @Autowired
  EventService eventService;

  @Autowired
  EventPatternRepository repository;

  @Autowired
  EventRepository eventRepository;

  @Autowired
  Validator validator;

  @PersistenceContext
  EntityManager entityManager;

  @ApiOperation(value = "Get a list of patterns for given event", response = EventPatternResponse.class)
  @GetMapping("/patterns")
  public EventPatternResponse retrieve(
    @AuthenticationPrincipal User user,
    @ApiParam(value = "Array of pattern's id")
    @RequestParam(value="id", defaultValue="") List<Long> id,
    @ApiParam(value = "Array of connected events's id")
    @RequestParam(value="events", defaultValue="") List<Long> events,
    @Deprecated
    @ApiParam(value = "Id of linked event")
    @RequestParam(value="event_id", required=false) Long eventId,
    @ApiParam(value = "Start of requesting range")
    @RequestParam(value="from", required=false) Long from,
    @ApiParam(value = "End of requesting range")
    @RequestParam(value="to", required=false) Long to,
    @ApiParam(value = "Timestamp after that pattern was created")
    @RequestParam(value="created_from", required=false) Long createdFrom,
    @ApiParam(value = "Timestamp before that pattern was created")
    @RequestParam(value="created_to", required=false) Long createdTo,
    @ApiParam(value = "Timestamp after that pattern was updated")
    @RequestParam(value="updated_from", required=false) Long updatedFrom,
    @ApiParam(value = "Timestamp before that pattern was updated")
    @RequestParam(value="updated_to", required=false) Long updatedTo,
    @ApiParam(value = "Pagination offset")
    @RequestParam(value="offset", defaultValue="0") long offset,
    @ApiParam(value = "Count of patterns to retrieve")
    @RequestParam(value="count", defaultValue="100") int count
  ) {
    // for backward compatibility
    if (eventId != null) {
      Event entity = eventService.getById(eventId);
      if (entity == null) {
        return new EventPatternResponse().error(404, "Event not found");
      }
      return new EventPatternResponse().success( entity.getPatterns() );
    }

    JPAQuery query = generateGetQuery(user, id, events, from, to, createdFrom, createdTo, updatedFrom, updatedTo);
    return new EventPatternResponse().success( service.getByQuery(query, new OffsetPageRequest(offset, count)) );
  }

  @ApiOperation(value = "Get pattern by id", response = EventPatternResponse.class)
  @GetMapping("/patterns/{id}")
  public EventPatternResponse retrieveById(@PathVariable Long id)
  {
    EventPattern entity = service.getById(id);
    if (entity == null) {
      return new EventPatternResponse().error(404, "EventPattern not found");
    }
    return new EventPatternResponse().success(entity);
  }

  @ApiOperation(value = "Create pattern", response = EventPatternResponse.class)
  @PostMapping("/patterns")
  public EventPatternResponse create(
    @RequestParam(value="event_id", required=true) Long eventId,
    @RequestBody @Validated EventPattern pattern,
    BindingResult binding
  ) {
    if (binding.hasErrors()) {
      return new EventPatternResponse().error(400, binding.getAllErrors().get(0).getDefaultMessage());
    }

    if (pattern.getEndedAt().getTime() == Long.MAX_VALUE && pattern.getRrule() == null) {
      pattern.setEndedAt(new Date(pattern.getStartedAt().getTime() + pattern.getDuration()));
    } else if (pattern.getDuration() <= 0) {
      pattern.setDuration(pattern.getEndedAt().getTime() - pattern.getStartedAt().getTime());
    }

    Event entity = eventService.getById(eventId);
    if (entity == null) {
      return new EventPatternResponse().error(404, "Event not found");
    }

    for (EventPatternExrule ex : pattern.getExrules()) {
      ex.setPattern(pattern);
    }

    pattern.setEvent(entity);
    return new EventPatternResponse().success( service.save(pattern) );
  }

  @ApiImplicitParams(
    @ApiImplicitParam(
      name = "updates",
      value = "Object with updated values for EventPattern",
      required = true,
      dataType = "EventPattern"
    )
  )
  @ApiOperation(value = "Update pattern", response = EventPatternResponse.class)
  @PatchMapping("/patterns/{id}")
  public EventPatternResponse update(
    @PathVariable Long id,
    @RequestBody Map<String, Object> updates,
    BindingResult binding
  ) {
    EventPattern entity = service.getById(id);
    if (entity == null) {
      return new EventPatternResponse().error(404, "EventPattern not found");
    }
    service.update(entity, updates);
    validator.validate(entity, binding);
    if (binding.hasErrors()) {
      return new EventPatternResponse().error(400, binding.getAllErrors().get(0).getDefaultMessage());
    }
    return new EventPatternResponse().success( service.save(entity) );
  }

  @ApiOperation(value = "Delete pattern")
  @DeleteMapping("/patterns/{id}")
  public EventPatternResponse delete(@PathVariable Long id)
  {
    EventPattern entity = service.getById(id);
    if (entity == null) {
      return new EventPatternResponse().error(404, "EventPattern not found");
    }
    service.delete(entity);
    return new EventPatternResponse().success();
  }

  private JPAQuery generateGetQuery(
    User user,
    List<Long> id,
    List<Long> events,
    Long from,
    Long to,
    Long createdFrom,
    Long createdTo,
    Long updatedFrom,
    Long updatedTo
  ) {
    QEventPattern qPattern = QEventPattern.eventPattern;
    QPermission qPermission = QPermission.permission;
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qPattern);

    String userId = user.getId();
    BooleanExpression where = qPattern.event.ownerId.eq(userId)
        .or(qPattern.id.stringValue().eq(qPermission.entityId))
        .or(qPattern.event.ownerId.eq(qPermission.entityId))
        .or(qPattern.event.id.stringValue().eq(qPermission.entityId).and(qPermission.name.eq("READ_EVENT")));

    BooleanExpression hasPermission = permissionService.getHasPermissionQuery(userId, "READ_PATTERN")
        .or(permissionService.getHasPermissionQuery(userId, "READ_EVENT"));
    query.leftJoin(qPermission).on(hasPermission);

    if (id.size() > 0) {
      where = qPattern.id.in(id).and(where);
    }

    if (events.size() > 0) {
      where = qPattern.event.id.in(events).and(where);
    }

    if (createdFrom != null) {
      where = qPattern.createdAt.goe(new Date(createdFrom)).and(where);
    }

    if (createdTo != null) {
      where = qPattern.createdAt.loe(new Date(createdTo)).and(where);
    }

    if (updatedFrom != null) {
      where = qPattern.updatedAt.goe(new Date(updatedFrom)).and(where);
    }

    if (updatedTo != null) {
      where = qPattern.updatedAt.loe(new Date(updatedTo)).and(where);
    }

    if (from != null) {
      where = qPattern.endedAt.goe(new Date(from)).and(where);
    }

    if (to != null) {
      where = qPattern.startedAt.loe(new Date(to)).and(where);
    }

    query.where(where);
    return query;
  }
}

// public class PatternUpdateValidator implements Validator
// {
//   @Override
//   public boolean supports(Class<?> clazz)
//   {
//     return FormParams.class.isAssignableFrom(clazz);
//   }

//   @Override
//   public void validate(Object target, Errors errors)
//   {
//     FormParams params = (FormParams) target;

//     if (StringUtils.isBlank(params.getRuleId()) && StringUtils.isBlank(params.getRef())) {
//       errors.reject(null, "Необходимо указать или 'ID правила', и/или 'Объект алерта'!");
//     } else if (params.getDateFrom() == null || params.getDateTo() == null) {
//       errors.reject(null, "Необходимо указать период поиска!");
//     }
//   }
// }