package com.skillmasters.server.common;

public class CreateEventRequestBuilder extends AppRequestBuilder<CreateEventRequestBuilder>
{
  public CreateEventRequestBuilder details(String details)
  {
    return set("details", details);
  }

  public CreateEventRequestBuilder location(String location)
  {
    return set("location", location);
  }

  public CreateEventRequestBuilder name(String name)
  {
    return set("name", name);
  }

  public CreateEventRequestBuilder status(String status)
  {
    return set("status", status);
  }
}
