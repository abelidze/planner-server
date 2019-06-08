package com.skillmasters.server.http.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.skillmasters.server.model.Event;

public class EventResponse extends Response<Event, EventResponse>
{
  public EventResponse()
  {
    super(EventResponse.class);
  }
}