package com.skillmasters.server.common.requestbuilder;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

public class AppRequestBuilder<T extends AppRequestBuilder>
{
  // for post parameters
  protected Map<String, Object> map = new HashMap<>();
  // for get parameters
  protected MultiValueMap<String, String> mvmap = new LinkedMultiValueMap<>();

  public MultiValueMap<String, String> buildGet()
  {
    return mvmap;
  }

  public Map<String, Object> buildPost()
  {
    return map;
  }

  public T offset(Long offset)
  {
    return set("offset", offset);
  }

  public T count(Long count)
  {
    return set("count", count);
  }

  public T set(String k, Long v)
  {
    mvmap.set(k, v.toString());
    map.put(k, v);
    return (T) this;
  }

  public T set(String k, String v)
  {
    mvmap.set(k, v);
    map.put(k, v);
    return (T) this;
  }

  public T set(String k, List<Long> v)
  {
    List<String> strList = new ArrayList<>();
    for (Long l : v) {
      strList.add(l.toString());
    }
    mvmap.put(k, strList);
    map.put(k, v);
    return (T) this;
  }

}
