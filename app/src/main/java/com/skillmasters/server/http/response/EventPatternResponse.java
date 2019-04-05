package com.skillmasters.server.http.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.skillmasters.server.model.EventPattern;

public class EventPatternResponse extends Response<EventPatternResponse>
{
  @JsonInclude(Include.NON_NULL)
  protected List<EventPattern> data;

  public EventPatternResponse()
  {
    super(EventPatternResponse.class);
  }

  public void setData(List<EventPattern> newData)
  {
    data = newData;
  }

  public List<EventPattern> getData()
  {
    return data;
  }

  public EventPatternResponse success(List<EventPattern> objects)
  {
    super.success();
    this.setData(objects);
    return self;
  }

  // public EventPatternResponse error(String errorMessage)
  // {
  //   return new EventPatternResponse().error(errorMessage);
  // }
}