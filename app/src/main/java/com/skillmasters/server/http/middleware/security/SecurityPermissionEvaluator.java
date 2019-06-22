package com.skillmasters.server.http.middleware.security;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import com.skillmasters.server.service.PermissionService;

public class SecurityPermissionEvaluator implements PermissionEvaluator
{
  @Autowired
  private PermissionService permissionService;

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
  {
    // User user = (User) authentication.getPrincipal();
    // return permissionService.isAuthorized(user, targetDomainObject, permission.toString());
    return true;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)
  {
    return true;
  }
}