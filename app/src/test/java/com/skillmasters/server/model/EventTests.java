package com.skillmasters.server.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventTests extends ModelTests
{
  @Test
  public void testGettersSetters()
  {
    Event e = new Event();
    e.setId(testLong);
    assertThat(e.getId()).isEqualTo(testLong);

    e.setOwnerId(testString);
    assertThat(e.getOwnerId()).isEqualTo(testString);

    e.setName(testString);
    assertThat(e.getName()).isEqualTo(testString);

    e.setDetails(testString);
    assertThat(e.getDetails()).isEqualTo(testString);

    e.setStatus(testString);
    assertThat(e.getStatus()).isEqualTo(testString);

    e.setLocation(testString);
    assertThat(e.getLocation()).isEqualTo(testString);

    e.setCreatedAt(testDateStart);
    assertThat(e.getCreatedAt()).isEqualTo(testDateStart);

    e.setUpdatedAt(testDateEnd);
    assertThat(e.getUpdatedAt()).isEqualTo(testDateEnd);

    Task t1 = new Task();
    Task t2 = new Task();
    List<Task> taskList = new ArrayList<Task>(Arrays.asList(t1, t2));

    e.setTasks(taskList);
    assertThat(e.getTasks().get(0)).isEqualTo(t1);
    assertThat(e.getTasks().get(1)).isEqualTo(t2);


    EventPattern ep1 = new EventPattern();
    EventPattern ep2 = new EventPattern();
    List<EventPattern> eventPatternList = new ArrayList<EventPattern>(Arrays.asList(ep1, ep2));

    e.setPatterns(eventPatternList);
    assertThat(e.getPatterns().get(0)).isEqualTo(ep1);
    assertThat(e.getPatterns().get(1)).isEqualTo(ep2);
  }
}
