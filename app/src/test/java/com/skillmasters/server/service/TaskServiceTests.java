package com.skillmasters.server.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.skillmasters.server.common.EventGenerator;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class TaskServiceTests extends ServiceTests
{
  @Autowired
  protected TaskService taskService;

  @Autowired
  protected EventService eventService;

  private ArrayList<Task> populate()
  {
    ArrayList<Task> tasks = new ArrayList<>(10);

    for (int i = 0; i < 10; i++) {
      Event event = EventGenerator.genEvent(i);
      event.setOwnerId(testUser.getId());
      eventService.save(event);

      Task task = new Task();
      task.setEvent(event);
      if (1 < i && i < 5)
        task.setParentId(tasks.get(i - 1).getId());

      task.setName("Name for task " + i);
      task.setDetails("Details for task " + i);
      task.setStatus("Status for task " + i);
      task.setDeadlineAt(new Date());

      task = taskService.save(task);
      tasks.add(task);
    }

    eventService.getRepository().flush();
    taskService.getRepository().flush();
    return tasks;
  }

  @Test
  public void testCreate()
  {
    ArrayList<Task> tasks = populate();
    assertThat(countRowsInTable(taskTablename)).isEqualTo(10);
    assertThat(taskService.count(qTask.id.isNotNull())).isEqualTo(10);

    JPAQuery query = getQueryFromTask();
    query.where(qTask.isNotNull()).orderBy(qTask.id.asc());
    Iterable<Task> result = taskService.getByQuery(query);
    int i = 0;
    for (Task t: result) {
      assertThat(t).isEqualTo(tasks.get(i));
      i++;
    }
  }

  @Test
  public void testUpdate()
  {
    ArrayList<Task> tasks = populate();
    assertThat(countRowsInTable(taskTablename)).isEqualTo(10);

    Long id = tasks.get(3).getId();
    Task task = taskService.getById(id);
    assertThat(task).isNotNull();

    Map<String, Object> updates = new HashMap<>();
    Event newEvent = EventGenerator.genEvent(100);
    newEvent = eventService.save(newEvent);

    Object newParentId = null;
    String newName = "New name";
    String newDetails = "New details";
    String newStatus = "New status";
    Long newDeadlineAt = new Date().getTime();

    updates.put("event", newEvent);
    updates.put("parent_id", newParentId);
    updates.put("name", newName);
    updates.put("details", newDetails);
    updates.put("status", newStatus);
    updates.put("deadline_at", newDeadlineAt);

    task = taskService.update(task, updates);
    taskService.getRepository().flush();

    for (Task t : taskService.getByQuery(qTask.isNotNull())) {
      if (t.getId().equals(id)) {
        assertThat(t).isEqualTo(task);
        continue;
      }
      assertThat(t).isNotEqualTo(task);
    }
  }

  @Test
  public void testRemove()
  {
    ArrayList<Task> tasks = populate();
    assertThat(countRowsInTable(taskTablename)).isEqualTo(10);

    for (Task ep : tasks) {
      taskService.delete(ep);
    }

    eventService.getRepository().flush();
    taskService.getRepository().flush();
    assertThat(countRowsInTable(taskTablename)).isEqualTo(0);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(10);
  }

  @Test
  public void testCascadeRemove()
  {
    ArrayList<Task> tasks = populate();
    assertThat(countRowsInTable(taskTablename)).isEqualTo(10);

    for (Task ep : tasks) {
      eventService.delete(ep.getEvent());
    }

    eventService.getRepository().flush();
    taskService.getRepository().flush();
    assertThat(countRowsInTable(taskTablename)).isEqualTo(0);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(0);
  }

  @Test
  public void testCascadeRemoveSeveralTasks()
  {
    Event event = EventGenerator.genEventWithOwner(10, testUser.getId());
    event = eventService.save(event);

    Task task1 = new Task();
    task1.setEvent(event);

    Task task2 = new Task();
    task2.setEvent(event);

    task1 = taskService.save(task1);
    task2 = taskService.save(task2);

    taskService.getRepository().flush();
    eventService.getRepository().flush();

    assertThat(countRowsInTable(taskTablename)).isEqualTo(2);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(1);

    eventService.delete(event);
    eventService.getRepository().flush();

    assertThat(countRowsInTable(taskTablename)).isEqualTo(0);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(0);
  }

  @Test
  public void testCascadeNotRemoveEvent()
  {
    Event event = EventGenerator.genEventWithOwner(10, testUser.getId());
    event = eventService.save(event);

    Task task1 = new Task();
    task1.setEvent(event);

    Task task2 = new Task();
    task2.setEvent(event);

    task1 = taskService.save(task1);
    task2 = taskService.save(task2);

    taskService.getRepository().flush();
    eventService.getRepository().flush();

    assertThat(countRowsInTable(taskTablename)).isEqualTo(2);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(1);

    taskService.delete(task1);
    taskService.getRepository().flush();

    assertThat(countRowsInTable(taskTablename)).isEqualTo(1);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(1);

    taskService.delete(task2);
    taskService.getRepository().flush();
    assertThat(countRowsInTable(taskTablename)).isEqualTo(0);
    assertThat(countRowsInTable(eventsTablename)).isEqualTo(1);
  }

  private JPAQuery getQueryFromTask()
  {
    JPAQuery query = new JPAQuery(entityManager);
    query.from(qTask);
    return query;
  }
}
