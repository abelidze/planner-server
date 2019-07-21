package com.skillmasters.server.mock.response;

import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.http.response.Response;
import com.skillmasters.server.mock.model.EventPatternMock;
import com.skillmasters.server.model.EventPattern;

public class EventPatternResponseMock extends Response<EventPatternMock, EventPatternResponseMock>
{
  public EventPatternResponseMock()
  {
    super(EventPatternResponseMock.class);
  }
}
