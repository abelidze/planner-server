package com.skillmasters.server.common.requestbuilder.permission;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.http.request.PermissionRequest;

public class GrantRequestBuilder extends AppRequestBuilder<GrantRequestBuilder>
{
  public GrantRequestBuilder userId(String userId)
  {
    return set("user_id", userId);
  }

  public GrantRequestBuilder entityId(Long entityId)
  {
    return set("entity_id", entityId);
  }

  public GrantRequestBuilder entityType(PermissionRequest.EntityType  entityType)
  {
    return set("entity_type", entityType);
  }

  public GrantRequestBuilder action(PermissionRequest.ActionType action)
  {
    return set("action", action);
  }
}
