package com.skillmasters.server.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.skillmasters.server.common.EventGenerator;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EventPatternServiceTests extends ServiceTests
{
  @Autowired
  protected EventPatternService eventPatternService;

  @Autowired
  protected EventService eventService;

  private ArrayList<EventPattern> populate()
  {
    ArrayList<EventPattern> eventPatterns = new ArrayList<>(10);
    for (int i = 0; i < 10; i ++) {
      Event e = EventGenerator.genEvent(i);
      e.setOwnerId(testUser.getId());
      e = eventService.save(e);

      EventPattern ep = new EventPattern();
      ep.setEvent(e);
      ep.setDuration(200L);
      ep.setRrule("FREQ=DAILY;INTERVAL=1");
      ep.setEndedAt(new Date(ep.getStartedAt().getTime() + 1000));
      ep = eventPatternService.save(ep);
      eventPatterns.add(ep);
    }
    eventPatternService.getRepository().flush();
    eventService.getRepository().flush();
    return eventPatterns;
  }

  @Test
  public void testCreate()
  {
    ArrayList<EventPattern> eventPatterns = populate();

    assertThat(countRowsInTable(eventPatternsTablename)).isEqualTo(10);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);
    assertThat(eventPatternService.count(qEventPattern.id.isNotNull())).isEqualTo(10);

    JPAQuery query = getQueryFromEventPattern();
    BooleanExpression where = qEventPattern.isNotNull();
    query.where(where).orderBy(qEventPattern.id.asc());
    Iterable<EventPattern> result = eventPatternService.getByQuery(query);

    int i = 0;
    for (EventPattern ep : result) {
      assertThat(ep).isEqualTo(eventPatterns.get(i));
      i++;
    }
  }

  @Test
  public void testUpdate()
  {
    ArrayList<EventPattern> eventPatterns = populate();
    assertThat(countRowsInTable(eventPatternsTablename)).isEqualTo(10);

    Long id = eventPatterns.get(4).getId();
    EventPattern eventPattern = eventPatternService.getById(id);
    assertThat(eventPattern).isNotNull();

    Map<String, Object> updates = new HashMap<>();

    Event newEvent = EventGenerator.genEvent(100);
    Long newDuration = 400L;
    String newRule = "FREQ=WEEKLY;INTERVAL=1";
    Long newStartedAt = new Date().getTime();
    Long newEndedAt = newStartedAt + 300;

    updates.put("event", newEvent);
    updates.put("duration", newDuration);
    updates.put("rrule", newRule);
    updates.put("startedAt", newStartedAt);
    updates.put("ended_at", newEndedAt);

    eventService.save(newEvent);
    eventPattern = eventPatternService.update(eventPattern, updates);
    eventPatternService.getRepository().flush();

    for (EventPattern ep : eventPatternService.getByQuery(qEventPattern.isNotNull())) {
      if (ep.getId().equals(id)) {
        assertThat(ep).isEqualTo(eventPattern);
        continue;
      }

      assertThat(ep).isNotEqualTo(eventPattern);
    }

  }

  @Test
  public void testRemove()
  {
    ArrayList<EventPattern> eventPatterns = populate();
    assertThat(countRowsInTable(eventPatternsTablename)).isEqualTo(10);

    for (EventPattern ep : eventPatterns) {
      eventPatternService.delete(ep);
    }
    eventService.getRepository().flush();
    assertThat(countRowsInTable(eventPatternsTablename)).isEqualTo(0);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);
  }

  @Test
  public void testRemoveCascade()
  {
    ArrayList<EventPattern> eventPatterns = populate();
    assertThat(countRowsInTable(eventPatternsTablename)).isEqualTo(10);

    for (EventPattern ep : eventPatterns) {
      eventService.delete(ep.getEvent());
    }

//    for (Event e : eventService.getByQuery(qEvent.isNotNull())) {
//      eventService.delete(e);
//    }

    eventService.getRepository().flush();
    eventPatternService.getRepository().flush();

    assertThat(countRowsInTable(eventPatternsTablename)).isEqualTo(0);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(0);
  }

  private JPAQuery getQueryFromEventPattern()
  {
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qEventPattern);
    return query;
  }
}
