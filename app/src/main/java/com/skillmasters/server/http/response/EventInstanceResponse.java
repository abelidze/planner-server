package com.skillmasters.server.http.response;

import java.util.Date;
import java.util.ArrayList;
import io.swagger.annotations.ApiModelProperty;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;

public class EventInstanceResponse extends Response<EventInstanceResponse.EventInstance, EventInstanceResponse>
{
  public EventInstanceResponse()
  {
    super(EventInstanceResponse.class);
    this.data = new ArrayList<>();
  }

  public static class EventInstance
  {
    @ApiModelProperty(value = "Instance's event")
    public Long eventId;

    @ApiModelProperty(value = "Pattern that was used to generate instance")
    public Long patternId;

    @ApiModelProperty(value = "Start of the instance", readOnly = true, example = "1556712345000")
    public Date startedAt;

    @ApiModelProperty(value = "End of the instance", readOnly = true, example = "1556712345000")
    public Date endedAt;

    public EventInstance()
    {
      //
    }

    public EventInstance(Long eventId, Long patternId, Date startedAt, Date endedAt)
    {
      this.eventId = eventId;
      this.patternId = patternId;
      this.startedAt = startedAt;
      this.endedAt = endedAt;
    }
  }

  public EventInstanceResponse addInstance(Event event, EventPattern pattern)
  {
    this.data.add(new EventInstance(event.getId(), pattern.getId(), pattern.getStartedAt(), pattern.getEndedAt()));
    return self;
  }

  public EventInstanceResponse addInstance(Event event, EventPattern pattern, Date startedAt, Date endedAt)
  {
    this.data.add(new EventInstance(event.getId(), pattern.getId(), startedAt, endedAt));
    return self;
  }

  public EventInstanceResponse addInstance(Long eventId, Long patternId, Date startedAt, Date endedAt)
  {
    this.data.add(new EventInstance(eventId, patternId, startedAt, endedAt));
    return self;
  }
}