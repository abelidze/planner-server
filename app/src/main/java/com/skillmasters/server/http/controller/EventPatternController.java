package com.skillmasters.server.http.controller;

import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.repository.EventPatternRepository;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.EventPattern;

@RestController
@RequestMapping("/api/v1")
@Api(tags="EventPatterns", description="planer's patterns")
public class EventPatternController
{
  @Autowired
  EventPatternRepository repository;

  @Autowired
  EventRepository eventRepository;

  @ApiOperation(value = "Get a list of patterns for given event", response = EventPatternResponse.class, authorizations = {@Authorization(value = "access_token")})
  @GetMapping("/patterns")
  public EventPatternResponse retrieve(
    @RequestParam(value="event_id", required=true) Long eventId
  ) {
    if (!eventRepository.existsById(eventId)) {
      return new EventPatternResponse().error("Event not found");
    }
    return new EventPatternResponse().success( eventRepository.getOne(eventId).getPatterns() );
  }

  @ApiOperation(value = "Create pattern", response = EventPatternResponse.class, authorizations = {@Authorization(value = "access_token")})
  @PostMapping("/patterns")
  public EventPatternResponse create(
    @RequestParam(value="event_id", required=true) Long eventId,
    @RequestBody EventPattern pattern
  ) {
    if (!eventRepository.existsById(eventId)) {
      return new EventPatternResponse().error("Event not found");
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
  @ApiOperation(value = "Update pattern", response = EventPatternResponse.class, authorizations = {@Authorization(value = "access_token")})
  @PatchMapping("/patterns/{id}")
  public EventPatternResponse update(@PathVariable Long id, @RequestBody Map<String, Object> updates)
  {
    if (!repository.existsById(id)) {
      return new EventPatternResponse().error("EventPattern not found");
    }
    EventPattern pattern = repository.findById(id).get();
    updates.forEach((k, v) -> {
      Field field = ReflectionUtils.findField(EventPattern.class, k);
      if (field != null) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, pattern, v);
      }
    });
    return new EventPatternResponse().success(Arrays.asList( repository.save(pattern) ));
  }

  @ApiOperation(value = "Delete pattern", authorizations = {@Authorization(value = "access_token")})
  @DeleteMapping("/patterns/{id}")
  public EventPatternResponse delete(@PathVariable Long id)
  {
    if (!repository.existsById(id)) {
      return new EventPatternResponse().error("EventPattern not found");
    }
    repository.deleteById(id);
    return new EventPatternResponse().ok("ok");
  }
}
