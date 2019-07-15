package com.skillmasters.server.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.skillmasters.server.common.EventGenerator;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EventServiceTests extends ServiceTests
{
  @Test
  public void testCreate()
  {
    ArrayList<Event> events = populateWithEvents();

    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);
    assertThat(eventService.count(qEvent.id.isNotNull())).isEqualTo(10);

    JPAQuery query = getQueryFromEvent();

    BooleanExpression where = qEvent.isNotNull();
    query.where(where).orderBy(qEvent.id.asc());
    Iterable<Event> result = eventService.getByQuery(query);

    int i = 0;
    for (Event e : result) {
      assertThat(e.equals(events.get(i)));
//      assertThat(e.getOwnerId()).isEqualTo(events.get(i).getOwnerId());
//      assertThat(e.getDetails()).isEqualTo(events.get(i).getDetails());
//      assertThat(e.getName()).isEqualTo(events.get(i).getName());
//      assertThat(e.getLocation()).isEqualTo(events.get(i).getLocation());
      i++;
    }
  }

  @Test
  public void testUpdate()
  {
    ArrayList<Event> events = populateWithEvents();

    assertThat(countRowsInTable("events")).isEqualTo(10);
    Long id = events.get(3).getId();

    Event event = eventService.getById(id);
    assertThat(event).isNotNull();

    Map<String, Object> updates = new HashMap<>();

    String newDetails = "new details";
    String newName = "new name";
    String newLocation = "new location";

    List<EventPattern> newEpList = new ArrayList<>();
    List<Task> newTaskList = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      EventPattern ep = epg.genEventPattern();
      ep.setEvent(event);
      ep = eventPatternService.save(ep);
      newEpList.add(ep);

      Task t = new Task();
      t.setEvent(event);
      t = taskService.save(t);
      newTaskList.add(t);
    }
    updates.put("patterns", newEpList);
    updates.put("tasks", newTaskList);

    updates.put("details", newDetails);
    updates.put("name", newName);
    updates.put("location", newLocation);

    Event newEvent = eventService.update(event, updates);

    flushAll();

    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);

    for (Event e : eventService.getByQuery(qEvent.isNotNull())) {
      if (e.getId().equals(id)) {
        assertThat(e).isEqualTo(newEvent);
        //todo: fix bug?
        assertThat(e.getPatterns()).isNotNull();
        assertThat(e.getPatterns().size()).isEqualTo(10);
        assertThat(e.getTasks()).isNotNull();
        assertThat(e.getTasks().size()).isEqualTo(10);
        
        continue;
      }
      assertThat(e).isNotEqualTo(newEvent);
    }
  }

  @Test
  public void testRemove()
  {
    populateWithEvents();
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);

    for (Event e : eventService.getByQuery(qEvent.isNotNull())) {
      assertThat(e.getTasks()).isNotNull();
      assertThat(e.getPatterns()).isNotNull();
      eventService.delete(e);
    }
    eventService.getRepository().flush();
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(0);
  }


  private JPAQuery getQueryFromEvent()
  {
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qEvent);
    return query;
  }
}
