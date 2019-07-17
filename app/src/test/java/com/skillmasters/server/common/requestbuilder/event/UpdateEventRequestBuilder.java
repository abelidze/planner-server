package com.skillmasters.server.common.requestbuilder.event;

public class UpdateEventRequestBuilder extends CreateEventRequestBuilderGeneric<UpdateEventRequestBuilder>
{
  public UpdateEventRequestBuilder id(Long id)
  {
    return set("id", id);
  }

  public UpdateEventRequestBuilder ownerId(String ownerId)
  {
    return set("owner_id", ownerId);
  }
}
