package com.skillmasters.server.http.response;

import com.skillmasters.server.model.Event;

public class EventResponse extends Response<Event, EventResponse>
{
  public EventResponse()
  {
    super(EventResponse.class);
  }
}