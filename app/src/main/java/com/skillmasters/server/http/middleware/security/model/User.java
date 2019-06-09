package com.skillmasters.server.http.middleware.security.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class User implements UserDetails
{
  private static final long serialVersionUID = 1L;

  private final boolean enabled = true;
  private final boolean credentialsNonExpired = true;
  private final boolean accountNonLocked = true;
  private final boolean accountNonExpired = true;
  private final String password = null;
  private String username = null;
  private String id = null;

  public User(String username, String uid)
  {
    this.username = username;
    this.id = uid;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities()
  {
    // TODO Auto-generated method stub
    return null;
  }

  // public boolean isEnabled()
  // {
  //   return enabled;
  // }

  // public boolean isCredentialsNonExpired()
  // {
  //   return credentialsNonExpired;
  // }

  // public boolean isAccountNonLocked()
  // {
  //   return accountNonLocked;
  // }

  // public boolean isAccountNonExpired()
  // {
  //   return accountNonExpired;
  // }

}