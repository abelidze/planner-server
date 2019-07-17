package com.skillmasters.server.common.requestbuilder.task;


import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;

import java.util.List;

public class CreateTaskRequestBuilderGeneric<R extends CreateTaskRequestBuilderGeneric> extends AppRequestBuilder<R>
{
  public CreateTaskRequestBuilderGeneric id(List<Long> id)
  {
    return set("id", id);
  }

  public R parentId(Long parentId)
  {
    return set("parent_id", parentId);
  }

  public R name(String name)
  {
    return set("name", name);
  }

  public R details(String details)
  {
    return set("details", details);
  }

  public R status(String status)
  {
    return set("status", status);
  }

  public R deadlineAt(Long deadlineAt)
  {
    return set("deadline_at", deadlineAt);
  }
}
