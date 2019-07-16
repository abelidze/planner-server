package com.skillmasters.server.common;

public class UpdateEventRequestBuilder extends CreateEventRequestBuilder
{
  public CreateEventRequestBuilder id(Long id)
  {
    return set("id", id);
  }

  public CreateEventRequestBuilder ownerId(String ownerId)
  {
    return set("owner_id", ownerId);
  }
}
