package com.skillmasters.server.http.response;

import com.skillmasters.server.model.Permission;

public class PermissionResponse extends Response<Permission, PermissionResponse>
{
  public PermissionResponse()
  {
    super(PermissionResponse.class);
  }
}