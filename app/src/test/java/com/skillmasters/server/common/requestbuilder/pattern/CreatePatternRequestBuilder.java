package com.skillmasters.server.common.requestbuilder.pattern;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.model.EventPatternExrule;

import java.util.List;

public class CreatePatternRequestBuilder extends AppRequestBuilder<CreatePatternRequestBuilder>
{
  public CreatePatternRequestBuilder id(Long id)
  {
    return set("id", id);
  }

  public CreatePatternRequestBuilder duration(Long duration)
  {
    return set("duration", duration);
  }

  public CreatePatternRequestBuilder timezone(String timezone)
  {
    return set("timezone", timezone);
  }

  public CreatePatternRequestBuilder rrule(String  rrule)
  {
    return set("rrule", rrule);
  }

  public CreatePatternRequestBuilder exrule(String  exrule)
  {
    return set("exrule", exrule);
  }

  public CreatePatternRequestBuilder exrules(List<EventPatternExrule> exrules)
  {
    return set("exrules", exrules);
  }

  public CreatePatternRequestBuilder startedAt(Long startedAt)
  {
    return set("started_at", startedAt);
  }

  public CreatePatternRequestBuilder endedAt(Long endedAt)
  {
    return set("ended_at", endedAt);
  }

}
