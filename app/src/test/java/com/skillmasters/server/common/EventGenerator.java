package com.skillmasters.server.common;

import com.skillmasters.server.model.Event;

public class EventGenerator
{
  public static Event genEvent(int id)
  {
    Event e = new Event();
    e.setDetails("Details for event number " + id);
    e.setName("Name for event number " + id);
    e.setLocation("Location for event number " + id);
    return e;
  }
}
