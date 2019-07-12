package com.skillmasters.server.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.skillmasters.server.model.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Commit
public class EventServiceTests extends ServiceTests
{
  @Autowired
  protected EventService eventService;

  @Test
  @Order(1)
  public void testCreate()
  {
    ArrayList<Event> events = new ArrayList<>(10);
    for (int i = 0; i < 10; i++) {
      Event e = new Event();
      e.setOwnerId(testUser.getId());
      e.setDetails("Details for event number " + i);
      e.setName("Name for event number " + i);
      e.setLocation("Location for event number " + i);
      events.add(e);
      eventService.save(e);
    }
    eventService.getRepository().flush();

    assertThat(countRowsInTable("events")).isEqualTo(10);
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

  private JPAQuery getQueryFromEvent()
  {
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qEvent);
    return query;
  }
}
