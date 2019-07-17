package com.skillmasters.server.common.requestbuilder.task;


import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;

import java.util.List;

public class CreateTaskRequestBuilder extends AppRequestBuilder<CreateTaskRequestBuilder>
{
  public CreateTaskRequestBuilder id(List<Long> id)
  {
    return set("id", id);
  }

  public CreateTaskRequestBuilder parentId(Long parentId)
  {
    return set("parent_id", parentId);
  }

  public CreateTaskRequestBuilder name(String name)
  {
    return set("name", name);
  }

  public CreateTaskRequestBuilder details(String details)
  {
    return set("details", details);
  }

  public CreateTaskRequestBuilder status(String status)
  {
    return set("status", status);
  }

  public CreateTaskRequestBuilder deadlineAt(Long deadlineAt)
  {
    return set("deadline_at", deadlineAt);
  }
}
