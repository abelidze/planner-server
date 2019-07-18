package com.skillmasters.server.http.response;

import com.skillmasters.server.model.User;

public class UserResponse extends Response<UserResponse.UserDto, UserResponse>
{
  public static class UserDto
  {
    public String id = null;
    public String username = null;
    public String photo = null;
    public boolean enabled = true;
    public boolean credentialsNonExpired = true;
    public boolean accountNonLocked = true;
    public boolean accountNonExpired = true;

    public UserDto()
    {
      //
    }

    public UserDto(User user)
    {
      this.id = user.getId();
      this.username = user.getUsername();
      this.photo = user.getPhoto();
      this.enabled = user.isEnabled();
      this.credentialsNonExpired = user.isCredentialsNonExpired();
      this.accountNonLocked = user.isAccountNonLocked();
      this.accountNonExpired = user.isAccountNonExpired();
    }

    public UserDto(String uid, String username, String photo)
    {
      this.id = uid;
      this.username = username;
      this.photo = photo;
    }
  }

  public UserResponse()
  {
    super(UserResponse.class);
  }
}