package com.skillmasters.server.model;

import java.util.Collection;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.skillmasters.server.service.PermissionService;

@Data
public class User implements UserDetails
{
  @JsonIgnore public static final String DEFAULT_USER_ID = "227";
  @JsonIgnore private static final long serialVersionUID = 1L;

  private String id = null;
  private String username = null;
  private String photo = null;
  private final boolean enabled = true;
  private final boolean credentialsNonExpired = true;
  private final boolean accountNonLocked = true;
  private final boolean accountNonExpired = true;
  @JsonIgnore private final String password = null;
  @JsonIgnore private final PermissionService permissionService;

  public User()
  {
    this.permissionService = null;
  }

  public User(String username, String uid, PermissionService service)
  {
    this.permissionService = service;
    this.username = username;
    this.id = uid;
  }

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Stuff below is a very big hack and needs refactor
   */

  public boolean can(String action, IEntity entity)
  {
    return entity == null
        || this.isOwner(entity)
        || (this.permissionService != null && this.permissionService.hasPermission(this, action, entity));
  }

  @JsonIgnore
  public boolean isOwner(IEntity entity)
  {
    return entity.getOwnerId().equals(id)
        || (entity instanceof Permission && ((Permission) entity).getUserId().equals(id));
  }

}