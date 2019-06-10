package com.skillmasters.server.http.response;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import lombok.Data;

import org.springframework.data.domain.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
public class Response<M, T extends Response<M, ?>>
{
  @JsonBackReference
  protected final T self;

  protected Boolean success;
  protected Integer status;
  @JsonInclude(Include.NON_NULL)
  protected String message;
  @JsonInclude(Include.NON_NULL)
  protected Integer count;
  @JsonInclude(Include.NON_NULL)
  protected Long offset;
  @JsonInclude(Include.NON_NULL)
  protected List<M> data;

  protected Response(final Class<T> selfClass)
  {
    this.self = selfClass.cast(this);
  }

  public T ok(String message)
  {
    this.setSuccess(true);
    this.setStatus(200);
    this.setMessage(message);
    return self;
  }

  public T success()
  {
    this.setSuccess(true);
    this.setStatus(200);
    if (this.data != null) {
      this.setCount(this.data.size());
    }
    return self;
  }

  public T success(List<M> objects)
  {
    this.setCount(objects.size());
    this.setData(objects);
    return this.success();
  }

  public T success(Page<M> page)
  {
    this.setOffset(page.getPageable().getOffset());
    return this.success(page.getContent());
  }

  public T empty()
  {
    this.setSuccess(true);
    this.setStatus(204);
    return self;
  }

  public T error(String errorMessage)
  {
    return this.error(400, errorMessage);
  }

  public T error(Integer errorStatus, String errorMessage)
  {
    this.setSuccess(false);
    this.setStatus(errorStatus);
    this.setMessage(errorMessage);
    return self;
  }
}