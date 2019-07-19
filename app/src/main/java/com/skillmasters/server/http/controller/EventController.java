package com.skillmasters.server.http.controller;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.reflect.Field;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.validation.Validator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ReflectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import biweekly.io.ParseContext;
import biweekly.io.scribe.property.RecurrenceRuleScribe;
import biweekly.util.com.google.ical.compat.javautil.DateIterator;
import biweekly.parameter.ICalParameters;
import biweekly.property.RecurrenceRule;

import com.google.common.base.Strings;
import com.google.common.base.CaseFormat;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import com.skillmasters.server.misc.OffsetPageRequest;
import com.skillmasters.server.http.response.EventResponse;
import com.skillmasters.server.http.response.EventInstanceResponse;
import com.skillmasters.server.service.PermissionService;
import com.skillmasters.server.service.EventService;
import com.skillmasters.server.model.User;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.QEvent;
import com.skillmasters.server.model.QPermission;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Events", description="planner's events")
public class EventController
{
  @Autowired
  EventService eventService;

  @Autowired
  PermissionService permissionService;

  @Autowired
  RecurrenceRuleScribe scribe;

  @Autowired
  Validator validator;

  @Autowired
  ParseContext context;

  @PersistenceContext
  EntityManager entityManager;

  @ApiOperation(value = "Get a list of available events instances", response = EventInstanceResponse.class)
  @GetMapping("/events/instances")
  public EventInstanceResponse retrieveInstances(
    @AuthenticationPrincipal User user,
    @ApiParam(value = "Array of event's id")
    @RequestParam(value="id", defaultValue="") List<Long> id,
    @ApiParam(value = "Return only new started instances")
    @RequestParam(value="new_only", defaultValue="false") Boolean newOnly,
    @ApiParam(value = "Owner's unique id")
    @RequestParam(value="owner_id", required=false) String ownerId,
    @ApiParam(value = "Start of requesting range")
    @RequestParam(value="from", required=false) Long from,
    @ApiParam(value = "End of requesting range")
    @RequestParam(value="to", required=false) Long to,
    @ApiParam(value = "Timestamp after that event was created")
    @RequestParam(value="created_from", required=false) Long createdFrom,
    @ApiParam(value = "Timestamp before that event was created")
    @RequestParam(value="created_to", required=false) Long createdTo,
    @ApiParam(value = "Timestamp after that event was updated")
    @RequestParam(value="updated_from", required=false) Long updatedFrom,
    @ApiParam(value = "Timestamp before that event was updated")
    @RequestParam(value="updated_to", required=false) Long updatedTo
  ) {
    EventInstanceResponse response = new EventInstanceResponse();

    Date fromDate;
    if (from == null) {
      fromDate = new Date(0);
    } else {
      fromDate = new Date(from);
    }

    Date toDate;
    if (to == null) {
      toDate = new Date(EventPattern.MAX_TIME);
    } else {
      toDate = new Date(to);
    }

    TimeZone utcTimezone = TimeZone.getTimeZone("UTC");
    TimeZone timezone = utcTimezone;
    DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    df.setTimeZone(utcTimezone);

    Date eventDate;
    JPAQuery query = generateGetQuery(user, id, ownerId, from, to, createdFrom, createdTo, updatedFrom, updatedTo);
    Iterable<Event> events = eventService.getByQuery(query);
    for (Event event : events) {
      for (EventPattern pattern : event.getPatterns()) {
        String rruleStr = pattern.getRrule();
        if (Strings.isNullOrEmpty(rruleStr)) {
          response.addInstance(event, pattern);
        } else {
          Date start = pattern.getStartedAt();
          Date end = pattern.getEndedAt().before(toDate) ? pattern.getEndedAt() : toDate;
          rruleStr += ";UNTIL=" + df.format(end);

          if (Strings.isNullOrEmpty(pattern.getTimezone())) {
            timezone = utcTimezone;
          } else {
            timezone = TimeZone.getTimeZone(pattern.getTimezone());
          }

          RecurrenceRule rrule = scribe.parseText(rruleStr, null, new ICalParameters(), context);
          DateIterator dateIt = rrule.getDateIterator(start, timezone);

          Date advanceDate;
          if (newOnly == true) {
            advanceDate = fromDate;
          } else {
            advanceDate = new Date(fromDate.getTime() - pattern.getDuration());
          }
          if (start.before(advanceDate)) {
            dateIt.advanceTo(advanceDate);
          }

          for (int i = 0; i < 1000 && dateIt.hasNext(); ++i) {
            eventDate = dateIt.next();
            if (eventDate.after(end)) {
              break;
            }

            response.addInstance(event, pattern, eventDate, new Date(eventDate.getTime() + pattern.getDuration()));
          }
        }
      }
    }
    return response.success();
  }

  @ApiOperation(value = "Get a list of available events", response = EventResponse.class)
  @GetMapping("/events")
  public EventResponse retrieve(
    @AuthenticationPrincipal User user,
    @ApiParam(value = "Array of event's id")
    @RequestParam(value="id", defaultValue="") List<Long> id,
    @ApiParam(value = "Owner's unique id")
    @RequestParam(value="owner_id", required=false) String ownerId,
    @ApiParam(value = "Start of requesting range")
    @RequestParam(value="from", required=false) Long from,
    @ApiParam(value = "End of requesting range")
    @RequestParam(value="to", required=false) Long to,
    @ApiParam(value = "Timestamp after that event was created")
    @RequestParam(value="created_from", required=false) Long createdFrom,
    @ApiParam(value = "Timestamp before that event was created")
    @RequestParam(value="created_to", required=false) Long createdTo,
    @ApiParam(value = "Timestamp after that event was updated")
    @RequestParam(value="updated_from", required=false) Long updatedFrom,
    @ApiParam(value = "Timestamp before that event was updated")
    @RequestParam(value="updated_to", required=false) Long updatedTo,
    @ApiParam(value = "Pagination offset")
    @RequestParam(value="offset", defaultValue="0") long offset,
    @ApiParam(value = "Count of events to retrieve")
    @RequestParam(value="count", defaultValue="100") int count
  ) {
    JPAQuery query = generateGetQuery(user, id, ownerId, from, to, createdFrom, createdTo, updatedFrom, updatedTo);
    return new EventResponse().success( eventService.getByQuery(query, new OffsetPageRequest(offset, count)) );
  }

  @ApiOperation(value = "Get event by id", response = EventResponse.class)
  @GetMapping("/events/{id}")
  public EventResponse retrieveById(@PathVariable Long id)
  {
    Event entity = eventService.getById(id);
    if (entity == null) {
      return new EventResponse().error(404, "Event not found");
    }
    return new EventResponse().success(entity);
  }

  @ApiOperation(value = "Create event", response = EventResponse.class)
  @PostMapping("/events")
  public EventResponse create(
    @AuthenticationPrincipal User user,
    @RequestBody @Validated Event event,
    BindingResult binding
  ) {
    if (binding.hasErrors()) {
      return new EventResponse().error(400, binding.getAllErrors().get(0).getDefaultMessage());
    }

    event.setOwnerId(user.getId());
    return new EventResponse().success( eventService.save(event) );
  }

  @ApiImplicitParams(
    @ApiImplicitParam(
      name = "updates",
      value = "Object with updated values for Event",
      required = true,
      dataType = "Event"
    )
  )
  @ApiOperation(value = "Update event", response = EventResponse.class)
  @PatchMapping("/events/{id}")
  public EventResponse update(
    @PathVariable Long id,
    @RequestBody Map<String, Object> updates,
    BindingResult binding
  ) {
    Event entity = eventService.getById(id);
    if (entity == null) {
      return new EventResponse().error(404, "Event not found");
    }
    eventService.update(entity, updates);
    validator.validate(entity, binding);
    if (binding.hasErrors()) {
      return new EventResponse().error(400, binding.getAllErrors().get(0).getDefaultMessage());
    }
    return new EventResponse().success( eventService.save(entity) );
  }

  @ApiOperation(value = "Delete event")
  @DeleteMapping("/events/{id}")
  public EventResponse delete(@PathVariable Long id)
  {
    Event entity = eventService.getById(id);
    if (entity == null) {
      return new EventResponse().error(404, "Event not found");
    }
    eventService.delete(entity);
    return new EventResponse().success();
  }

  private JPAQuery generateGetQuery(
    User user,
    List<Long> id,
    String ownerId,
    Long from,
    Long to,
    Long createdFrom,
    Long createdTo,
    Long updatedFrom,
    Long updatedTo
  ) {
    QEvent qEvent = QEvent.event;
    QPermission qPermission = QPermission.permission;
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qEvent);
    BooleanExpression where = null;

    if (id.size() > 0) {
      where = qEvent.id.in(id).and(where);
    }

    String userId = user.getId();
    BooleanExpression hasPermission = permissionService.getHasPermissionQuery(userId, "READ_EVENT");
    if (ownerId != null && ownerId != userId) {
      hasPermission = hasPermission
          .and(qEvent.id.stringValue().eq(qPermission.entityId).or(qPermission.entityId.eq(ownerId)));
      query.innerJoin(qPermission).on(hasPermission);
      where = qEvent.ownerId.eq(ownerId).and(where);
    } else {
      query.leftJoin(qPermission).on(hasPermission);
      where = qEvent.ownerId.eq(userId)
          .or(qEvent.id.stringValue().eq(qPermission.entityId))
          .or(qEvent.ownerId.eq(qPermission.entityId))
          .and(where);
    }
    query.groupBy(qEvent.id);

    if (createdFrom != null) {
      where = qEvent.createdAt.goe(new Date(createdFrom)).and(where);
    }

    if (createdTo != null) {
      where = qEvent.createdAt.loe(new Date(createdTo)).and(where);
    }

    if (updatedFrom != null) {
      where = qEvent.updatedAt.goe(new Date(updatedFrom)).and(where);
    }

    if (updatedTo != null) {
      where = qEvent.updatedAt.loe(new Date(updatedTo)).and(where);
    }

    if (from != null) {
      where = qEvent.patterns.any().endedAt.goe(new Date(from)).and(where);
    }

    if (to != null) {
      where = qEvent.patterns.any().startedAt.loe(new Date(to)).and(where);
    }

    query.where(where);
    return query;
  }
}
