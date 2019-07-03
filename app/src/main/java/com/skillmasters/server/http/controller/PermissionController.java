package com.skillmasters.server.http.controller;

import java.util.List;
import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.http.request.PermissionRequest;
import com.skillmasters.server.http.response.PermissionResponse;
import com.skillmasters.server.misc.ResourceNotFoundException;
import com.skillmasters.server.misc.PermissionDeniedException;
import com.skillmasters.server.misc.OffsetPageRequest;
import com.skillmasters.server.service.*;
import com.skillmasters.server.model.*;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Permission", description="control the permissions for Calendar entities")
public class PermissionController
{
  @Autowired
  EventService eventService;

  @Autowired
  EventPatternService patternService;

  @Autowired
  TaskService taskService;

  @Autowired
  ShareService shareService;

  @Autowired
  PermissionService permissionService;

  @ApiOperation(value = "Generate a link for sharing permission on specific entity", produces="text/plain")
  @GetMapping("/share")
  public String share(
    @RequestParam(value="entity_id", required=true) Long entityId,
    @RequestParam(value="entity_type", required=true) PermissionRequest.EntityType entityType,
    @RequestParam(value="action", required=true) PermissionRequest.ActionType action
  ) {
    if (entityId == null || entityType == null || action == null) {
      throw new IllegalArgumentException("Invalid request parameters");
    }

    // Generate permission object and store it
    Permission perm = permissionService.generatePermission(null, action.name(), retriveEntity(entityId, entityType));
    String token = shareService.cachePermission(perm);
    return ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(token).toUriString();
  }

  @ApiOperation(value = "Generate a link for sharing multiple permissions", produces="text/plain")
  @PostMapping("/share")
  public String share(@RequestBody(required=true) List<PermissionRequest> permissions)
  {
    if (permissions.isEmpty()) {
      throw new IllegalArgumentException("Invalid request parameters");
    }

    // Generate permission objects and store them
    List<Permission> list = new ArrayList();
    for (PermissionRequest r : permissions) {
      IEntity entity = retriveEntity(r.getEntityId(), r.getEntityType());
      list.add( permissionService.generatePermission(null, r.getAction().name(), entity) );
    }
    String token = shareService.cachePermissionList(list);
    return ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(token).toUriString();
  }

  @ApiOperation(value = "Activate generated share-link")
  @GetMapping("/share/{token}")
  public void share(@AuthenticationPrincipal User user, @PathVariable String token)
  {
    List<Permission> permissions = shareService.validateToken(token);
    if (permissions == null) {
      throw new PermissionDeniedException(); 
    }
    for (Permission perm : permissions) {
      perm.setUserId(user.getId());
      permissionService.grantPermission(perm);
    }
  }

  @ApiOperation(value = "Grant permission to user for specific entity")
  @GetMapping("/grant")
  public void grant(
    @RequestParam(value="user_id", required=true) String userId,
    @RequestParam(value="entity_id", required=true) Long entityId,
    @RequestParam(value="entity_type", required=true) PermissionRequest.EntityType entityType,
    @RequestParam(value="action", required=true) PermissionRequest.ActionType action
  ) {
    if (userId == null || entityId == null || entityType == null || action == null) {
      throw new IllegalArgumentException("Invalid request parameters");
    }
    permissionService.grantPermission(userId, action.name(), retriveEntity(entityId, entityType));
  }

  @ApiOperation(value = "Get all granted permission for your resources")
  @GetMapping("/permissions")
  public PermissionResponse retrieve(
    @AuthenticationPrincipal User user,
    @RequestParam(value="offset", defaultValue="0") long offset,
    @RequestParam(value="count", defaultValue="100") int count
  ) {
    BooleanExpression query = QPermission.permission.ownerId.eq(user.getId());
    return new PermissionResponse().success( permissionService.getByQuery(query, new OffsetPageRequest(offset, count)) );
  }


  @ApiOperation(value = "Revoke specified permission")
  @DeleteMapping("/permissions/{id}")
  public PermissionResponse revoke(@PathVariable Long id)
  {
    Permission entity = permissionService.getById(id);
    if (entity == null) {
      return new PermissionResponse().error(404, "Permission not found");
    }
    permissionService.delete(entity);
    return new PermissionResponse().success();
  }

  private IEntity retriveEntity(Long entityId, PermissionRequest.EntityType entityType)
  {
    IEntity entity = null;
    switch (entityType) {
      case EVENT:
        entity = eventService.getById(entityId);
        break;

      case PATTERN:
        entity = patternService.getById(entityId);
        break;

      case TASK:
        entity = taskService.getById(entityId);
        break;
    }

    if (entity == null) {
      throw new ResourceNotFoundException(); 
    }

    // It's not needed due to services, but who knows...
    // if (!user.isOwner(entity)) {
    //   throw new PermissionDeniedException(); 
    // }

    return entity;
  }
}