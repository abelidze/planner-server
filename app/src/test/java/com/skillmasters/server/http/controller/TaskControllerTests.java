package com.skillmasters.server.http.controller;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.ListTasksRequestBuilder;
import com.skillmasters.server.mock.response.TaskResponseMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

}
