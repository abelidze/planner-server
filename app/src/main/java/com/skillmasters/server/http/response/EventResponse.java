package com.skillmasters.server.http.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.skillmasters.server.model.Event;

public class EventResponse extends Response<EventResponse>
{
  @JsonInclude(Include.NON_NULL)
  protected List<Event> data;

  public EventResponse()
  {
    super(EventResponse.class);
  }

  public void setData(List<Event> newData)
  {
    data = newData;
  }

  public List<Event> getData()
  {
    return data;
  }

  public EventResponse success(List<Event> objects)
  {
    super.success();
    this.setData(objects);
    return self;
  }
}