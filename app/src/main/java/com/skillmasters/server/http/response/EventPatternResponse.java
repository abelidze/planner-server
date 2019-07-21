package com.skillmasters.server.http.response;

import com.skillmasters.server.model.EventPattern;

public class EventPatternResponse extends Response<EventPattern, EventPatternResponse>
{
  public EventPatternResponse()
  {
    super(EventPatternResponse.class);
  }
}