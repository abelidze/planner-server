package com.skillmasters.server.http.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.skillmasters.server.model.Permission;

public class PermissionResponse extends Response<Permission, PermissionResponse>
{
  public PermissionResponse()
  {
    super(PermissionResponse.class);
  }
}