package com.skillmasters.server.http.controller;

import java.util.Date;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

import com.google.common.base.CaseFormat;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.repository.EventPatternRepository;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.User;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.QEventPattern;
import com.skillmasters.server.model.QEvent;

@RestController
@RequestMapping("/api/v1")
@Api(tags="EventPatterns", description="planer's patterns")
public class EventPatternController
{
  @Autowired
  EventPatternRepository repository;

  @Autowired
  EventRepository eventRepository;

  @ApiOperation(value = "Get a list of patterns for given event", response = EventPatternResponse.class)
  @GetMapping("/patterns")
  public EventPatternResponse retrieve(
    @AuthenticationPrincipal User user,
    @RequestParam(value="event_id", required=true) Long eventId
  ) {
    QEvent qEvent = QEvent.event;
    if (!eventRepository.exists( qEvent.id.eq(eventId).and(qEvent.ownerId.eq(user.getId())) )) {
      return new EventPatternResponse().error(404, "Event not found or you don't have access to it");
    }
    return new EventPatternResponse().success( eventRepository.getOne(eventId).getPatterns() );
  }

  @ApiOperation(value = "Create pattern", response = EventPatternResponse.class)
  @PostMapping("/patterns")
  public EventPatternResponse create(
    @AuthenticationPrincipal User user,
    @RequestParam(value="event_id", required=true) Long eventId,
    @RequestBody EventPattern pattern
  ) {
    QEvent qEvent = QEvent.event;
    if (!eventRepository.exists( qEvent.id.eq(eventId).and(qEvent.ownerId.eq(user.getId())) )) {
      return new EventPatternResponse().error(404, "Event not found or you don't have access to it");
    }
    pattern.setEvent( eventRepository.getOne(eventId) );
    return new EventPatternResponse().success(Arrays.asList( repository.save(pattern) ));
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
    @AuthenticationPrincipal User user,
    @PathVariable Long id,
    @RequestBody Map<String, Object> updates
  ) {
    QEventPattern qPattern = QEventPattern.eventPattern;
    if (!repository.exists( qPattern.id.eq(id).and(qPattern.event.ownerId.eq(user.getId())) )) {
      return new EventPatternResponse().error(404, "Task not found or you don't have access to it");
    }

    EventPattern entity = repository.findById(id).get();
    updates.forEach((k, v) -> {
      String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, k);
      Field field = ReflectionUtils.findField(EventPattern.class, fieldName);
      if (field != null) {
        ReflectionUtils.makeAccessible(field);
        final Class<?> type = field.getType();
        System.out.println(type.getName());
        if (type.equals(Long.class)) {
          ReflectionUtils.setField(field, entity, ((Number) v).longValue());
        } else if (type.equals(Date.class)) {
          ReflectionUtils.setField(field, entity, new Date( ((Number) v).longValue() ));
        } else {
          ReflectionUtils.setField(field, entity, v);
        }
      }
    });
    return new EventPatternResponse().success(Arrays.asList( repository.save(entity) ));
  }

  @ApiOperation(value = "Delete pattern")
  @DeleteMapping("/patterns/{id}")
  public EventPatternResponse delete(@AuthenticationPrincipal User user, @PathVariable Long id)
  {
    QEventPattern qPattern = QEventPattern.eventPattern;
    if (!repository.exists( qPattern.id.eq(id).and(qPattern.event.ownerId.eq(user.getId())) )) {
      return new EventPatternResponse().error(404, "Task not found or you don't have access to it");
    }
    repository.deleteById(id);
    return new EventPatternResponse().ok("ok");
  }
}
