package com.skillmasters.server.http.controller;

import java.util.List;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.Event;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Events", description="planer's events")
public class EventController
{
  @Autowired
  EventRepository repository;

  @ApiOperation(value = "Get a list of available events", response = Event.class, responseContainer="List")
  @GetMapping("/events")
  public List<Event> retrieve(@RequestParam(value="ids", defaultValue="") List<Long> ids)
  {
    return repository.all();
  }

  @ApiOperation(value = "Create event", response = Event.class)
  @PostMapping("/events")
  public Event create(@RequestBody Event event)
  {
    return repository.add(event);
  }

  @ApiOperation(value = "Update event", response = Event.class)
  @PutMapping("/events/{id}")
  public Event update(@PathVariable Long id, Event event)
  {
    return repository.update(id, event);
  }

  @ApiOperation(value = "Delete event")
  @DeleteMapping("/events/{id}")
  public void delete(@PathVariable Long id)
  {
    repository.delete(id);
  }
}
