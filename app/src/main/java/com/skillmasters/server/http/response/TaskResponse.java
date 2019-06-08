package com.skillmasters.server.http.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.skillmasters.server.model.Task;

public class TaskResponse extends Response<Task, TaskResponse>
{
  public TaskResponse()
  {
    super(TaskResponse.class);
  }
}