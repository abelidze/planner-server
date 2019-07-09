package com.skillmasters.server.http.response;

import com.skillmasters.server.model.Task;

public class TaskResponse extends Response<Task, TaskResponse>
{
  public TaskResponse()
  {
    super(TaskResponse.class);
  }
}