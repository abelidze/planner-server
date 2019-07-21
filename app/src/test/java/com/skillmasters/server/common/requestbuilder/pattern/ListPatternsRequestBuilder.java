package com.skillmasters.server.common.requestbuilder.pattern;

import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;

import java.util.List;

public class ListPatternsRequestBuilder extends AppRequestBuilder<ListPatternsRequestBuilder>
{
  public ListPatternsRequestBuilder id(List<Long> id)
  {
    return set("id", id);
  }

  public ListPatternsRequestBuilder events(List<Long> events)
  {
    return set("events", events);
  }

  //eventid

  public ListPatternsRequestBuilder from(Long from)
  {
    return set("from", from);
  }

  public ListPatternsRequestBuilder to(Long to)
  {
    return set("to", to);
  }

  public ListPatternsRequestBuilder created_from(Long created_from)
  {
    return set("created_from", created_from);
  }

  public ListPatternsRequestBuilder created_to(Long created_to)
  {
    return set("created_to", created_to);
  }


  public ListPatternsRequestBuilder updated_from(Long updated_from)
  {
    return set("updated_from", updated_from);
  }

  public ListPatternsRequestBuilder updated_to(Long updated_to)
  {
    return set("updated_to", updated_to);
  }
}
