package com.skillmasters.server.http.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.skillmasters.server.model.EventPattern;

public class EventPatternResponse extends Response<EventPattern, EventPatternResponse>
{
  public EventPatternResponse()
  {
    super(EventPatternResponse.class);
  }
}