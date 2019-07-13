package com.skillmasters.server.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.skillmasters.server.common.EventGenerator;
import com.skillmasters.server.model.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EventServiceTests extends ServiceTests
{
  @Autowired
  protected EventService eventService;

  private ArrayList<Event> populate()
  {
    ArrayList<Event> events = new ArrayList<>(10);
    for (int i = 0; i < 10; i++) {
      Event e = EventGenerator.genEvent(i);
      e.setOwnerId(testUser.getId());
      e = eventService.save(e);
      events.add(e);
    }
    eventService.getRepository().flush();

    return events;
  }

  @Test
  public void testCreate()
  {
    ArrayList<Event> events = populate();

    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);
    assertThat(eventService.count(qEvent.id.isNotNull())).isEqualTo(10);

    JPAQuery query = getQueryFromEvent();

    BooleanExpression where = qEvent.isNotNull();
    query.where(where).orderBy(qEvent.id.asc());
    Iterable<Event> result = eventService.getByQuery(query);

    int i = 0;
    for (Event e : result) {
      assertThat(e.getOwnerId()).isEqualTo(events.get(i).getOwnerId());
      assertThat(e.getDetails()).isEqualTo(events.get(i).getDetails());
      assertThat(e.getName()).isEqualTo(events.get(i).getName());
      assertThat(e.getLocation()).isEqualTo(events.get(i).getLocation());
      i++;
    }
  }

  @Test
  public void testUpdate()
  {
    ArrayList<Event> events = populate();

    assertThat(countRowsInTable("events")).isEqualTo(10);
    Long id = events.get(3).getId();

    Event event = eventService.getById(id);
    assertThat(event).isNotNull();

    Map<String, Object> updates = new HashMap<>();

    String newDetails = "new details";
    String newName = "new name";
    String newLocation = "new location";

    updates.put("details", newDetails);
    updates.put("name", newName);
    updates.put("location", newLocation);

    eventService.update(event, updates);
    eventService.getRepository().flush();

    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);

    for (Event e : eventService.getByQuery(qEvent.isNotNull())) {
      if (e.getId().equals(id)) {
        assertThat(e.getDetails()).isEqualTo(newDetails);
        assertThat(e.getName()).isEqualTo(newName);
        assertThat(e.getLocation()).isEqualTo(newLocation);
        continue;
      }

      assertThat(e.getDetails()).isNotEqualTo(newDetails);
      assertThat(e.getName()).isNotEqualTo(newName);
      assertThat(e.getLocation()).isNotEqualTo(newLocation);
    }
  }

  @Test
  public void testRemove()
  {
    ArrayList<Event> events = populate();
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);

    for (Event e : events) {
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
