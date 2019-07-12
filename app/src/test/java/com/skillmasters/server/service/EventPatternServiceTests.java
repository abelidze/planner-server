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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
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
    return eventPatterns;
  }

  @Test
  public void testCreate()
  {
    ArrayList<EventPattern> eventPatterns = populate();

    assertThat(countRowsInTable(eventPatternsTablename)).isEqualTo(10);
    assertThat(eventPatternService.count(qEventPattern.id.isNotNull())).isEqualTo(10);
    JPAQuery query = getQueryFromEventPattern();

    BooleanExpression where = qEventPattern.isNotNull();
    query.where(where).orderBy(qEventPattern.id.asc());
    Iterable<EventPattern> result = eventPatternService.getByQuery(query);

    int i = 0;
    for (EventPattern ep : result) {
      assertThat(ep.equals(eventPatterns.get(i)));
      i++;
    }
  }

  public void testUpdate()
  {

  }

  public void testRemove()
  {

  }

  private JPAQuery getQueryFromEventPattern()
  {
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qEventPattern);
    return query;
  }
}
