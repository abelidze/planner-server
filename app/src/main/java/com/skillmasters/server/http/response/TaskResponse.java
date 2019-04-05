package com.skillmasters.server.http.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.skillmasters.server.model.Task;

public class TaskResponse extends Response<TaskResponse>
{
  @JsonInclude(Include.NON_NULL)
  protected List<Task> data;

  public TaskResponse()
  {
    super(TaskResponse.class);
  }

  public void setData(List<Task> newData)
  {
    data = newData;
  }

  public List<Task> getData()
  {
    return data;
  }

  public TaskResponse success(List<Task> objects)
  {
    // TaskResponse response = (TaskResponse) Response.success();
    // response.setData(objects);
    return self;
  }
}