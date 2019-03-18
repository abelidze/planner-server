package com.skillmasters.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Events", description="planer's events")
public class EventController
{
  @ApiOperation(value = "Get a list of available events", response = String.class)
  @GetMapping("/events")
  public String retrieve(@RequestParam(value="id", defaultValue="0") Integer id)
  {
    return "retrieve";
  }

  @ApiOperation(value = "Create event", response = String.class)
  @PostMapping("/events")
  public String create(@RequestParam(value="id", defaultValue="0") Integer id)
  {
    return "create";
  }

  @ApiOperation(value = "Update event", response = String.class)
  @PatchMapping("/events")
  public String update(@RequestParam(value="id", defaultValue="0") Integer id)
  {
    return "update";
  }

  @ApiOperation(value = "Delete event", response = String.class)
  @DeleteMapping("/events")
  public String delete(@RequestParam(value="id", defaultValue="0") Integer id)
  {
    return "delete";
  }
}
