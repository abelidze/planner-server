package com.skillmasters.server.common;

import com.skillmasters.server.model.EventPattern;

import java.util.Date;

public class EventPatternGenerator
{
  public EventPattern genEventPattern()
  {
    EventPattern eventPattern = new EventPattern();
    Date curDate = new Date();
    eventPattern.setStartedAt(curDate);
    eventPattern.setEndedAt(new Date(curDate.getTime() + 1000));
    return eventPattern;
  }
}
