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

import com.skillmasters.server.repository.EventPatternRepository;
import com.skillmasters.server.model.EventPattern;

@RestController
@RequestMapping("/api/v1")
@Api(tags="EventPatterns", description="planer's patterns")
public class EventPatternController
{
  @Autowired
  EventPatternRepository repository;

  @ApiOperation(value = "Get a list of available patterns", response = EventPattern.class, responseContainer="List")
  @GetMapping("/patterns")
  public List<EventPattern> retrieve(@RequestParam(value="ids", defaultValue="") List<Long> ids)
  {
    return repository.all();
  }

  @ApiOperation(value = "Create pattern", response = EventPattern.class)
  @PostMapping("/patterns")
  public EventPattern create(@RequestBody EventPattern pattern)
  {
    return repository.add(pattern);
  }

  @ApiOperation(value = "Update pattern", response = EventPattern.class)
  @PutMapping("/patterns/{id}")
  public EventPattern update(@PathVariable Long id, @RequestBody EventPattern pattern)
  {
    return repository.update(id, pattern);
  }

  @ApiOperation(value = "Delete pattern")
  @DeleteMapping("/patterns/{id}")
  public void delete(@PathVariable Long id)
  {
    repository.delete(id);
  }
}
