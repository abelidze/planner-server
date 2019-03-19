package com.skillmasters.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.skillmasters.server.model.EventPattern;

@Repository
public class EventPatternRepository
{
  private List<EventPattern> patterns = new ArrayList<EventPattern>();
  
  public EventPattern add(EventPattern pattern)
  {
    pattern.setId((long) (patterns.size()+1));
    patterns.add(pattern);
    return pattern;
  }
  
  public EventPattern update(EventPattern pattern)
  {
    patterns.set(pattern.getId().intValue() - 1, pattern);
    return pattern;
  }
  
  public EventPattern update(Long id, EventPattern pattern)
  {
    patterns.set(id.intValue() - 1, pattern);
    return pattern;
  }
  
  public EventPattern findById(Long id)
  {
    Optional<EventPattern> pattern = patterns.stream().filter(a -> a.getId().equals(id)).findFirst();
    if (pattern.isPresent()) {
      return pattern.get();
    } else {
      return null;
    }
  }
  
  public void delete(Long id)
  {
    patterns.remove(id.intValue());
  }
}