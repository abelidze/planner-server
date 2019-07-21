package com.skillmasters.server.common.requestbuilder.event;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;

public class CreateEventRequestBuilderGeneric<R extends CreateEventRequestBuilderGeneric> extends AppRequestBuilder<R>
{
  public R details(String details)
  {
    return set("details", details);
  }

  public R location(String location)
  {
    return set("location", location);
  }

  public R name(String name)
  {
    return set("name", name);
  }

  public R status(String status)
  {
    return set("status", status);
  }
}
