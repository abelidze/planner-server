package com.skillmasters.server.model;

import com.skillmasters.server.common.EventGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskTests extends ModelTests
{
  @Test
  public void testGettersSetters()
  {
    Task t = new Task();

    t.setId(testLong);
    assertThat(t.getId()).isEqualTo(testLong);

    Event e = EventGenerator.genEvent(123);

    t.setEvent(e);
    assertThat(t.getEvent()).isEqualTo(e);

    t.setParentId(testLong);
    assertThat(t.getParentId()).isEqualTo(testLong);

    t.setName(testString);
    assertThat(t.getName()).isEqualTo(testString);

    t.setDetails(testString);
    assertThat(t.getDetails()).isEqualTo(testString);

    t.setStatus(testString);
    assertThat(t.getStatus()).isEqualTo(testString);

    t.setDeadlineAt(testDateEnd);
    assertThat(t.getDeadlineAt()).isEqualTo(testDateEnd);

    t.setCreatedAt(testDateStart);
    assertThat(t.getCreatedAt()).isEqualTo(testDateStart);

    t.setUpdatedAt(testDateEnd);
    assertThat(t.getUpdatedAt()).isEqualTo(testDateEnd);
  }

  @Test
  public void testGetOwnerId()
  {
    Task t = new Task();
    Event e = EventGenerator.genEventWithOwner(10, "ownerid");
    t.setEvent(e);

    assertThat(t.getOwnerId()).isEqualTo("ownerid");
  }

  @Test
  public void testGetEventId()
  {
    Task t = new Task();
    Event e = EventGenerator.genEventWithOwner(10, "ownerid");
    e.setId(9009L);
    t.setEvent(e);

    assertThat(t.getEventId()).isEqualTo(9009L);
  }
}
