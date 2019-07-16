package com.skillmasters.server.common;

import java.util.List;

///api/v1/events
//    Get a list of available events
public class ListEventsRequestBuilder extends AppRequestBuilder
{
  // setters
  public void id(List<Long> id)
  {
    set("id", id);
  }

  public void ownerId(String ownerId)
  {
    set("owner_id", ownerId);
  }

  public void from(Long from)
  {
    set("from", from);
  }

  public void to(Long to)
  {
    set("to", to);
  }

  public void createdFrom(Long from)
  {
    set("created_from", from);
  }

  public void createdTo(Long to)
  {
    set("created_to", to);
  }

  public void updatedFrom(Long from)
  {
    set("updated_from", from);
  }

  public void updatedTo(Long to)
  {
    set("updated_to", to);
  }
}
