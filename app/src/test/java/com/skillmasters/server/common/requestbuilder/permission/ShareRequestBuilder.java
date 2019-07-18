package com.skillmasters.server.common.requestbuilder.permission;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.http.request.PermissionRequest;

public class ShareRequestBuilder extends AppRequestBuilder<ShareRequestBuilder>
{
  public ShareRequestBuilder entityId(Long entityId)
  {
    return set("entity_id", entityId);
  }

  public ShareRequestBuilder entityType(PermissionRequest.EntityType entityType)
  {
    return set("entity_type", entityType);
  }

  public ShareRequestBuilder action(PermissionRequest.ActionType action)
  {
    return set("action", action);
  }
}
