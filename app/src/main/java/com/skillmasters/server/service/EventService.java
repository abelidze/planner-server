package com.skillmasters.server.service;

import org.springframework.stereotype.Service;
import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.Event;

@Service
public class EventService extends EntityService<EventRepository, Event, Long>
{
  public EventService()
  {
    super(Event.class, "EVENT");
  }
}