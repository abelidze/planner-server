package com.skillmasters.server.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.skillmasters.server.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>
{
//  private List<Event> events = new ArrayList<Event>();
//
//  public List<Event> all()
//  {
//    return events;
//  }
//
//  public Event add(Event event)
//  {
//    event.setId((long) (events.size() + 1));
//    events.add(event);
//    return event;
//  }
//
//  public Event update(Event event)
//  {
//    events.set(event.getId().intValue() - 1, event);
//    return event;
//  }
//
//  public Event update(Long id, Event event)
//  {
//    events.set(id.intValue() - 1, event);
//    return event;
//  }
//
//  public Event findById(Long id)
//  {
//    Optional<Event> event = events.stream().filter(a -> a.getId().equals(id)).findFirst();
//    if (event.isPresent()) {
//      return event.get();
//    } else {
//      return null;
//    }
//  }
//
//  public void delete(Long id)
//  {
//    events.remove(id.intValue());
//  }
}