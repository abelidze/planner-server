package com.skillmasters.server.common;

import java.util.List;

///api/v1/events
//    Get a list of available events
public class ListEventsRequestBuilder extends AppRequestBuilder<ListEventsRequestBuilder>
{
  // setters
  public ListEventsRequestBuilder id(List<Long> id)
  {
    return set("id", id);
  }

  public ListEventsRequestBuilder ownerId(String ownerId)
  {
    return set("owner_id", ownerId);
  }

  public ListEventsRequestBuilder from(Long from)
  {
    return set("from", from);
  }

  public ListEventsRequestBuilder to(Long to)
  {
    return set("to", to);
  }

  public ListEventsRequestBuilder createdFrom(Long from)
  {
    return set("created_from", from);
  }

  public ListEventsRequestBuilder createdTo(Long to)
  {
    return set("created_to", to);
  }

  public ListEventsRequestBuilder updatedFrom(Long from)
  {
    return set("updated_from", from);
  }

  public ListEventsRequestBuilder updatedTo(Long to)
  {
    return set("updated_to", to);
  }
}
