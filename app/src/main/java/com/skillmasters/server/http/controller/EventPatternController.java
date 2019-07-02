package com.skillmasters.server.http.controller;

import java.util.Date;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

import com.google.common.base.CaseFormat;

import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.repository.EventPatternRepository;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.User;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.EventPatternExrule;
import com.skillmasters.server.model.QEventPattern;
import com.skillmasters.server.model.QEvent;
import com.skillmasters.server.service.EventPatternService;
import com.skillmasters.server.service.EventService;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Patterns", description="events' patterns")
public class EventPatternController
{
  @Autowired
  EventPatternService service;

  @Autowired
  EventService eventService;

  @Autowired
  EventPatternRepository repository;

  @Autowired
  EventRepository eventRepository;

  @ApiOperation(value = "Get a list of patterns for given event", response = EventPatternResponse.class)
  @GetMapping("/patterns")
  public EventPatternResponse retrieve(@RequestParam(value="event_id", required=true) Long eventId)
  {
    Event entity = eventService.getById(eventId);
    if (entity == null) {
      return new EventPatternResponse().error(404, "Event not found");
    }
    return new EventPatternResponse().success( entity.getPatterns() );
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
    @RequestBody EventPattern pattern
  ) {
    Event entity = eventService.getById(eventId);
    if (entity == null) {
      return new EventPatternResponse().error(404, "Event not found");
    }

    for (EventPatternExrule ex : pattern.getExrules()) {
      ex.setPattern(pattern);
    }

    pattern.setEvent(entity);
    if (pattern.getEndedAt().getTime() == Long.MAX_VALUE && pattern.getRrule() != null) {
      pattern.setEndedAt(new Date(pattern.getStartedAt().getTime() + pattern.getDuration()));
    } else if (pattern.getDuration() <= 0) {
      pattern.setDuration(pattern.getEndedAt().getTime() - pattern.getStartedAt().getTime());
    }
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
  public EventPatternResponse update(@PathVariable Long id, @RequestBody Map<String, Object> updates)
  {
    EventPattern entity = service.getById(id);
    if (entity == null) {
      return new EventPatternResponse().error(404, "EventPattern not found");
    }
    return new EventPatternResponse().success( service.update(entity, updates) );
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
}
