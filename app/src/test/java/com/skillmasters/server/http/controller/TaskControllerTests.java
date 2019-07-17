package com.skillmasters.server.http.controller;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.ListTasksRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.UpdateTaskRequestBuilder;
import com.skillmasters.server.http.response.TaskResponse;
import com.skillmasters.server.mock.model.TaskMock;
import com.skillmasters.server.mock.response.TaskResponseMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
  public void testUpdateEvent() throws Exception
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

}
