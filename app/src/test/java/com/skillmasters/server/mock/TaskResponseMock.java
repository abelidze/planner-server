package com.skillmasters.server.mock;

import com.skillmasters.server.http.response.Response;

public class TaskResponseMock extends Response<TaskMock, TaskResponseMock>
{
  public TaskResponseMock() {
    super(TaskResponseMock.class);
  }
}
