package com.skillmasters.server.model;
import com.skillmasters.server.common.EventGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventPatternTests extends ModelTests
{
  @Test
  public void testGettersSetters()
  {
    EventPattern ep = new EventPattern();

    ep.setId(testLong);
    assertThat(ep.getId()).isEqualTo(testLong);

    Event e = EventGenerator.genEvent(100);
    ep.setEvent(e);
    assertThat(ep.getEvent()).isEqualTo(e);

    ep.setDuration(testLong);
    assertThat(ep.getDuration()).isEqualTo(testLong);

    ep.setTimezone(testString);
    assertThat(ep.getTimezone()).isEqualTo(testString);

    ep.setRrule(testString);
    assertThat(ep.getRrule()).isEqualTo(testString);

    EventPatternExrule epe1 = new EventPatternExrule();
    EventPatternExrule epe2 = new EventPatternExrule();
    List<EventPatternExrule> eventPatternExruleList = new ArrayList<>();
    eventPatternExruleList.add(epe1);
    eventPatternExruleList.add(epe2);

    ep.setExrules(eventPatternExruleList);
    assertThat(ep.getExrules().get(0)).isEqualTo(epe1);
    assertThat(ep.getExrules().get(1)).isEqualTo(epe2);

    ep.setStartedAt(testDateStart);
    assertThat(ep.getStartedAt()).isEqualTo(testDateStart);

    ep.setEndedAt(testDateEnd);
    assertThat(ep.getEndedAt()).isEqualTo(testDateEnd);

    ep.setUpdatedAt(testDateStart);
    assertThat(ep.getUpdatedAt()).isEqualTo(testDateStart);

    ep.setCreatedAt(testDateStart);
    assertThat(ep.getCreatedAt()).isEqualTo(testDateStart);
  }

  @Test
  public void testGetEventId()
  {
    Event e = EventGenerator.genEventWithOwner(100, "127");
    e.setId(113L);
    EventPattern ep = new EventPattern();
    ep.setEvent(e);

    assertThat(ep.getEventId()).isEqualTo(113L);
  }

}
