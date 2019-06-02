package com.skillmasters.server.http.controller;

import java.util.Arrays;
import java.util.List;

import com.skillmasters.server.model.EventPattern;
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

import com.skillmasters.server.http.response.EventResponse;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.Event;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Events", description="planer's events")
public class EventController
{
  @Autowired
  EventRepository repository;

  @ApiOperation(value = "Get a list of available events", response = EventResponse.class, authorizations = {@Authorization(value = "access_token")})
  @GetMapping("/events")
  public EventResponse retrieve(@RequestParam(value="ids", defaultValue="") List<Long> ids)
  {
    return new EventResponse().success( repository.findAll() );
  }

  @ApiOperation(value = "Create event", response = EventResponse.class, authorizations = {@Authorization(value = "access_token")})
  @PostMapping("/events")
  public EventResponse create(@RequestBody Event event)
  {
    for (EventPattern r : event.getPatterns()) {
      r.setEvent(event);
    }
    return new EventResponse().success( Arrays.asList(repository.save(event)) );
  }

  @ApiOperation(value = "Update event", response = EventResponse.class, authorizations = {@Authorization(value = "access_token")})
  @PutMapping("/events/{id}")
  public EventResponse update(@PathVariable Long id, @RequestBody Event event)
  {
    // event.setId(id);
    return new EventResponse().success( Arrays.asList(repository.save(event)) );
  }

  @ApiOperation(value = "Delete event", authorizations = {@Authorization(value = "access_token")})
  @DeleteMapping("/events/{id}")
  public void delete(@PathVariable Long id)
  {
    repository.deleteById(id);
  }
}
