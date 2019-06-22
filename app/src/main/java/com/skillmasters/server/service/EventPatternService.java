package com.skillmasters.server.service;

import org.springframework.stereotype.Service;
import com.skillmasters.server.repository.EventPatternRepository;
import com.skillmasters.server.model.EventPattern;

@Service
public class EventPatternService extends EntityService<EventPatternRepository, EventPattern, Long>
{
  public EventPatternService()
  {
    super(EventPattern.class, "PATTERN");
  }
}