package com.skillmasters.server.common.requestbuilder.permission;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.http.request.PermissionRequest;
import com.skillmasters.server.model.Permission;

public class PermissionsRequestBuilder extends AppRequestBuilder<PermissionsRequestBuilder>
{
  public PermissionsRequestBuilder entityType(PermissionRequest.EntityType entityType)
  {
    return set("entity_type", entityType);
  }

  public PermissionsRequestBuilder mine(Boolean mine)
  {
    return set("mine", mine);
  }
}
