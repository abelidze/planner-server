package com.skillmasters.server.common;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

public class AppRequestBuilder
{
  protected MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

  public MultiValueMap<String, String> build()
  {
    return map;
  }

  public void offset(Long offset)
  {
    set("offset", offset);
  }

  public void count(Long count)
  {
    set("count", count);
  }

  public void set(String k, Long v)
  {
    map.set(k, v.toString());
  }

  public void set(String k, String v)
  {
    map.set(k, v);
  }

  public void set(String k, List<Long> v)
  {
    List<String> strList = new ArrayList<>();
    for (Long l : v) {
      strList.add(l.toString());
    }
    map.put(k, strList);
  }

}
