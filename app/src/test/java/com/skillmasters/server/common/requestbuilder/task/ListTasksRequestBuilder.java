package com.skillmasters.server.common.requestbuilder.task;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.common.requestbuilder.event.ListEventsRequestBuilder;

import java.util.List;

public class ListTasksRequestBuilder extends AppRequestBuilder<ListTasksRequestBuilder>
{
  public ListTasksRequestBuilder id(List<Long> id)
  {
    return set("id", id);
  }

  public ListTasksRequestBuilder eventId(Long eventId)
  {
    return set("event_id", eventId);
  }

  public ListTasksRequestBuilder parentId(Long parentId)
  {
    return set("parent_id", parentId);
  }

  public ListTasksRequestBuilder status(String status)
  {
    return set("status", status);
  }

  public ListTasksRequestBuilder deadline(Long deadlineTo)
  {
    return set("deadline", deadlineTo);
  }

  public ListTasksRequestBuilder createdFrom(Long from)
  {
    return set("created_from", from);
  }

  public ListTasksRequestBuilder createdTo(Long to)
  {
    return set("created_to", to);
  }

  public ListTasksRequestBuilder updatedFrom(Long from)
  {
    return set("updated_from", from);
  }

  public ListTasksRequestBuilder updatedTo(Long to)
  {
    return set("updated_to", to);
  }


}
