package com.skillmasters.server.http.controller;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.CreateTaskRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.ListTasksRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.UpdateTaskRequestBuilder;
import com.skillmasters.server.http.response.TaskResponse;
import com.skillmasters.server.mock.model.TaskMock;
import com.skillmasters.server.mock.response.TaskResponseMock;
import com.skillmasters.server.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTests extends ControllerTests
{
  @Test
  public void testReadCreate() throws Exception
  {
    insertTask();

    TaskResponseMock response = authorizedOkResultResponse(HttpMethod.GET, tasksEndpoint,
        new ListTasksRequestBuilder(), TaskResponseMock.class);

    assertThat(response.getCount()).isEqualTo(1);
    assertThat(response.getData().size()).isEqualTo(1);
    assertThat(response.getData().get(0).getEvent_id()).isInstanceOf(Long.class);
  }

  @Test
  public void testCreateTaskWithNotExistingEvent() throws Exception
  {
    Long notExistingEventId = 2000L;
    CreateTaskRequestBuilder b = new CreateTaskRequestBuilder();
    performReq404(authorizedRequest(
        HttpMethod.POST, tasksEndpoint+"?event_id="+notExistingEventId, b));
  }

  @Test
  public void testGetById() throws Exception
  {
    List<TaskMock> createTaskResponses = insertTasks(10);
    Task task = createTaskResponses.get(5);

    TaskResponseMock response = getTaskOkById(task.getId());
    assertThat(response.getData().size()).isEqualTo(1);
    assertThat(response.getData().get(0)).isEqualTo(task);
  }

  @Test
  public void testGetBySeveralIds() throws Exception
  {
    List<TaskMock> createTaskMocks = insertTasks(10);

    Map<Long, Boolean> idsSubset = new HashMap<>();
    idsSubset.put(createTaskMocks.get(1).getId(), false);
    idsSubset.put(createTaskMocks.get(3).getId(), false);
    idsSubset.put(createTaskMocks.get(7).getId(), false);

    ListTasksRequestBuilder b = new ListTasksRequestBuilder();
    b.id(new ArrayList<>(idsSubset.keySet()));

    TaskResponseMock getTasksResponse = getTasks(b);
    assertThat(getTasksResponse.getCount()).isEqualTo(3);
    assertThat(getTasksResponse.getData().size()).isEqualTo(3);

    for (Task t : getTasksResponse.getData()) {
      idsSubset.put(t.getId(), true);
    }

    for (Boolean v : idsSubset.values()) {
      assertThat(v).isTrue();
    }
  }

  @Test
  public void testGetByEventId() throws Exception
  {
    List<TaskMock> tasks = insertTasks(10);

    for (TaskMock task : tasks) {
      ListTasksRequestBuilder b = new ListTasksRequestBuilder();
      b.eventId(task.getEvent_id());

      TaskResponseMock getResponse = getTasks(b);
      assertThat(getResponse.getCount()).isEqualTo(1);
      assertThat(getResponse.getData().size()).isEqualTo(1);
      assertThat(getResponse.getData().get(0).getEvent_id()).isEqualTo(task.getEvent_id());

    }

    for (TaskMock task : tasks) {
      ListTasksRequestBuilder b = new ListTasksRequestBuilder();
      b.eventId(1893333L);

      TaskResponseMock getResponse = getTasks(b);
      assertThat(getResponse.getCount()).isEqualTo(0);
      assertThat(getResponse.getData().size()).isEqualTo(0);
    }
  }

  @Test
  public void testGetByParentId() throws Exception
  {
    TaskResponseMock parentResponse = insertTask();
    TaskMock parent = parentResponse.getData().get(0);

    CreateTaskRequestBuilder createChildBuilder = new CreateTaskRequestBuilder();
    createChildBuilder.parentId(parent.getId());

    TaskResponseMock childResponse = insertTask(createChildBuilder);
    TaskMock child = childResponse.getData().get(0);
    ListTasksRequestBuilder listTasksBuilder = new ListTasksRequestBuilder();
    listTasksBuilder.parentId(parent.getId());

    TaskResponseMock listResponse = getTasks(listTasksBuilder);
    assertThat(listResponse.getCount()).isEqualTo(1);
    assertThat(listResponse.getData().size()).isEqualTo(1);
    assertThat(listResponse.getData().get(0)).isEqualTo(child);


    ListTasksRequestBuilder listEmptyTasksBuilder = new ListTasksRequestBuilder();
    listEmptyTasksBuilder.parentId(1333L);
    TaskResponseMock listResponseEmpty = getTasks(listEmptyTasksBuilder);
    assertThat(listResponseEmpty.getCount()).isEqualTo(0);
    assertThat(listResponseEmpty.getData().size()).isEqualTo(0);
  }

  @Test
  public void testGetByStatus() throws Exception
  {
    String goodStatus = "good status";
    int goodStatusesAmount = 20;
    for (int i = 0; i < goodStatusesAmount; i++) {
      insertTaskWithStatus(goodStatus);
    }

    ListTasksRequestBuilder goodStatusBuilder = new ListTasksRequestBuilder();
    goodStatusBuilder.status(goodStatus);
    TaskResponseMock goodResponse = getTasks(goodStatusBuilder);

    assertThat(goodResponse.getCount()).isEqualTo(goodStatusesAmount);
    assertThat(goodResponse.getData().size()).isEqualTo(goodStatusesAmount);
    for (TaskMock m : goodResponse.getData()) {
      assertThat(m.getStatus()).isEqualTo(goodStatus);
    }

    String badStatus = "bad status";
    int badStatusesAmount = 10;
    for (int i = 0; i < badStatusesAmount; i++) {
      insertTaskWithStatus(badStatus);
    }

    ListTasksRequestBuilder badStatusBuilder = new ListTasksRequestBuilder();
    badStatusBuilder.status(badStatus);
    TaskResponseMock badResponse = getTasks(badStatusBuilder);

    assertThat(badResponse.getCount()).isEqualTo(badStatusesAmount);
    assertThat(badResponse.getData().size()).isEqualTo(badStatusesAmount);

    for (TaskMock m : badResponse.getData()) {
      assertThat(m.getStatus()).isEqualTo(badStatus);
    }
  }

  @Test
  public void testGetByDeadline() throws Exception
  {
    Long deadline1 = new Date(1563423879L).getTime();
    int deadline1Amount = 23;
    for (int i = 0; i < deadline1Amount; i++) {
      insertTaskWithDeadline(deadline1);
    }

    ListTasksRequestBuilder deadline1Builder = new ListTasksRequestBuilder();
    deadline1Builder.deadline(deadline1);
    TaskResponseMock deadline1Response = getTasks(deadline1Builder);

    assertThat(deadline1Response.getCount()).isEqualTo(deadline1Amount);
    assertThat(deadline1Response.getData().size()).isEqualTo(deadline1Amount);
    for (TaskMock m : deadline1Response.getData()) {
      assertThat(m.getDeadlineAt().getTime()).isEqualTo(deadline1);
    }

    Long deadline2 = new Date(1583423879L).getTime();
    int deadline2Amount = 23;
    for (int i = 0; i < deadline2Amount; i++) {
      insertTaskWithDeadline(deadline2);
    }

    ListTasksRequestBuilder deadline2Builder = new ListTasksRequestBuilder();
    deadline2Builder.deadline(deadline2);
    TaskResponseMock deadline2Response = getTasks(deadline2Builder);

    assertThat(deadline2Response.getCount()).isEqualTo(deadline2Amount);
    assertThat(deadline2Response.getData().size()).isEqualTo(deadline2Amount);
    for (TaskMock m : deadline2Response.getData()) {
      assertThat(m.getDeadlineAt().getTime()).isEqualTo(deadline2);
    }
  }

  @Test
  public void testGetCreatedFromTo() throws Exception
  {
    Date start = new Date();
    insertTasks(2);
    Date end = new Date();

    testGetCreatedFromToHelper(0L, start.getTime(), 0);
    testGetCreatedFromToHelper(end.getTime()+10000, end.getTime()+1000000, 0);
    testGetCreatedFromToHelper(start.getTime()-10000, end.getTime()+10000, 2);
  }

  private void testGetCreatedFromToHelper(Long start, Long end, int expected) throws Exception
  {
    ListTasksRequestBuilder b = new ListTasksRequestBuilder();
    b.createdFrom(start);
    b.createdTo(end);
    TaskResponseMock response = getTasks(b);
    assertThat(response.getCount()).isEqualTo(expected);
    assertThat(response.getData().size()).isEqualTo(expected);
  }

  @Test
  public void testGetById404() throws Exception
  {
    Long notExistingId = 3232L;
    performReq404(authorizedRequest(
        HttpMethod.GET, tasksEndpoint+"/"+notExistingId));
  }

  @Test
  public void testDeleteTask() throws Exception
  {
    TaskResponseMock createResponses = insertTask();

    TaskResponseMock beforeDeleteResp = authorizedOkResultResponse(
        HttpMethod.GET, tasksEndpoint, new AppRequestBuilder(), TaskResponseMock.class);

    assertThat(beforeDeleteResp.getData().size()).isEqualTo(1);

    deleteTask(beforeDeleteResp.getData().get(0));

    TaskResponseMock afterDeleteResp = authorizedOkResultResponse(
        HttpMethod.GET, tasksEndpoint, new AppRequestBuilder(), TaskResponseMock.class);

    assertThat(afterDeleteResp.getData().size()).isEqualTo(0);
  }

  @Test
  public void testDelete404() throws Exception
  {
    Long notExistingId = 3232L;
    performReq404(authorizedRequest(
        HttpMethod.DELETE, tasksEndpoint+"/"+notExistingId));
  }


  @Test
  public void testUpdateTask() throws Exception
  {
    TaskMock taskMock = insertTask().getData().get(0);
    UpdateTaskRequestBuilder b = new UpdateTaskRequestBuilder();
    Date newDeadline = new Date();
    b.name("new name").details("new details").status("new status")
        .deadlineAt(newDeadline.getTime());


    List<TaskMock> tasks = getAllTasks();
    assertThat(tasks.size()).isEqualTo(1);
    TaskMock task = tasks.get(0);
    TaskMock updatedTask = updateTask(task, b);
    assertThat(updatedTask.getName()).isEqualTo("new name");
    assertThat(updatedTask.getDetails()).isEqualTo("new details");
    assertThat(updatedTask.getStatus()).isEqualTo("new status");
    assertThat(updatedTask.getDeadlineAt()).isEqualTo(newDeadline);
  }

  @Test
  public void testUpdate404() throws Exception
  {
    Long notExistingId = 2220L;
    performReq404(authorizedRequest(
        HttpMethod.PATCH, tasksEndpoint+"/"+notExistingId, new AppRequestBuilder()));
  }
}
