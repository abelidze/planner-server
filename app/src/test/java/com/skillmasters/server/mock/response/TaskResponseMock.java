package com.skillmasters.server.mock.response;

import com.skillmasters.server.http.response.Response;
import com.skillmasters.server.mock.model.TaskMock;

public class TaskResponseMock extends Response<TaskMock, TaskResponseMock>
{
  public TaskResponseMock() {
    super(TaskResponseMock.class);
  }
}
