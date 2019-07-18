package com.skillmasters.server.http.response;

import com.skillmasters.server.model.User;

public class UserResponse extends Response<User, UserResponse>
{
  public UserResponse()
  {
    super(UserResponse.class);
  }
}