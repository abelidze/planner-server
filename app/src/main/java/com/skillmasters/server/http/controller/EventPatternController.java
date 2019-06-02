package com.skillmasters.server.http.controller;

import java.util.Arrays;
import java.util.List;

import io.swagger.annotations.*;
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

import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.repository.EventPatternRepository;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.Event;

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
  public EventPatternResponse retrieve()
  {
    return new EventPatternResponse().success( repository.findAll() );
  }

  @ApiOperation(value = "Create pattern", response = EventPatternResponse.class, authorizations = {@Authorization(value = "access_token")})
  @PostMapping("/patterns")
  public EventPatternResponse create(@RequestBody EventPattern pattern)
  {
    // if (eventRepository.existsById(pattern.getEventId())) {
    //   return EventPatternResponse.success(Arrays.asList( repository.save(pattern) ));
    // }
    return new EventPatternResponse().error("Event not found");
  }

  @ApiOperation(value = "Update pattern", response = EventPatternResponse.class, authorizations = {@Authorization(value = "access_token")})
  @PutMapping("/patterns/{id}")
  public EventPatternResponse update(@PathVariable Long id, @RequestBody EventPattern pattern)
  {
    // pattern.setId(id);
    return new EventPatternResponse().success(Arrays.asList( repository.save(pattern) ));
  }

  @ApiOperation(value = "Delete pattern", authorizations = {@Authorization(value = "access_token")})
  @DeleteMapping("/patterns/{id}")
  public void delete(@PathVariable Long id)
  {
    repository.deleteById(id);
  }
}
