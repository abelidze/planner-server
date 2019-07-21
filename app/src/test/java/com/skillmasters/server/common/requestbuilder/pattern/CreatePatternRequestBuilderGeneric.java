package com.skillmasters.server.common.requestbuilder.pattern;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.model.EventPatternExrule;

import java.util.List;

public class CreatePatternRequestBuilderGeneric<R extends CreatePatternRequestBuilderGeneric> extends AppRequestBuilder<R>
{
  public R id(Long id)
  {
    return set("id", id);
  }

  public R duration(Long duration)
  {
    return set("duration", duration);
  }

  public R timezone(String timezone)
  {
    return set("timezone", timezone);
  }

  public R rrule(String  rrule)
  {
    return set("rrule", rrule);
  }

  public R exrule(String  exrule)
  {
    return set("exrule", exrule);
  }

  public R exrules(List<EventPatternExrule> exrules)
  {
    return set("exrules", exrules);
  }

  public R startedAt(Long startedAt)
  {
    return set("started_at", startedAt);
  }

  public R endedAt(Long endedAt)
  {
    return set("ended_at", endedAt);
  }
}
