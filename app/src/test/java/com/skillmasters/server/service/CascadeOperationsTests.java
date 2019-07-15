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
  @Autowired
  private EventService eventService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private EventPatternService eventPatternService;
  //bug
  @Test
  public void testRemoveTasksOnRemoveEvent()
  {
    List<Task> tasks = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Task task = taskService.save(new Task());
      tasks.add(task);
    }

    Event event = EventGenerator.genEventWithOwner(220, testUser.getId());
    event.setTasks(tasks);
    eventService.save(event);
    flushAll();

    assertThat(countRowsInTable(eventsTablename)).isEqualTo(1);
    assertThat(countRowsInTable(taskTablename)).isEqualTo(10);

    int leftCounter = tasks.size();
    for (Task task : taskService.getByQuery(qTask.isNotNull())) {
      taskService.delete(task);
      flushAll();
      assertThat(countRowsInTable(taskTablename)).isEqualTo(--leftCounter);
      if (leftCounter != 0) {
        assertThat(countRowsInTable(eventsTablename)).isEqualTo(1);
      }
      else {
        assertThat(countRowsInTable(eventsTablename)).isEqualTo(0);
      }
    }
  }

  //unfixed bug
  @Test
  public void testGetTaskOwnerIdFromEvent()
  {
    Task task = taskService.save(new Task());
    Event event = EventGenerator.genEventWithOwner(220, testUser.getId());
    event.setTasks(Arrays.asList(task));

    event = eventService.save(event);
    flushAll();

    Task taskFromDb = taskService.getById(task.getId());
    assertThat(taskFromDb).isNotNull();
    assertThat(taskFromDb.getEvent()).isNotNull();
  }

  //unfixed bug
  @Test
  public void testGetEventFromTask()
  {
    Task task = new Task();

    Event event = eventService.save(EventGenerator.genEventWithOwner(220, testUser.getId()));
    task.setEvent(event);
    taskService.save(task);
    flushAll();

    Event eventFromDb = eventService.getById(event.getId());
    assertThat(eventFromDb).isNotNull();
    assertThat(event.getTasks()).isNotNull();
    assertThat(event.getTasks().size()).isEqualTo(1);
  }

  private void flushAll()
  {
    eventService.getRepository().flush();
    eventPatternService.getRepository().flush();
    taskService.getRepository().flush();

  }

}
