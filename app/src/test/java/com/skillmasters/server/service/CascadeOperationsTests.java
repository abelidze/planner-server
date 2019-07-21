package com.skillmasters.server.service;

import com.skillmasters.server.common.EventGenerator;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CascadeOperationsTests extends ServiceTests
{
  @Test
  public void testGetTaskOwnerIdFromEvent()
  {
    Task task = new Task();
    Event event = EventGenerator.genEventWithOwner(220, testUser.getId());
    event.addTask(task);
    eventService.save(event);
    taskService.save(task);
    flushAll();

    Task taskFromDb = taskService.getById(task.getId());
    assertThat(taskFromDb).isNotNull();
    assertThat(taskFromDb.getEvent()).isNotNull();
  }

  @Test
  public void testGetEventFromTask()
  {
    Task task = new Task();
    Event event = EventGenerator.genEventWithOwner(220, testUser.getId());
    event.getTasks().add(task);
    eventService.save(event);
    taskService.save(task);
    flushAll();

    Event eventFromDb = eventService.getById(event.getId());
    assertThat(eventFromDb).isNotNull();
    assertThat(eventFromDb.getTasks()).isNotNull();
    assertThat(eventFromDb.getTasks().size()).isEqualTo(1);
  }
}
