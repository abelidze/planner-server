package com.skillmasters.server.http.response;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonBackReference;

import org.springframework.data.domain.Page;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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

  public T success(M object)
  {
    this.setData( Arrays.asList(object) );
    return this.success();
  }

  public T success(List<M> objects)
  {
    this.setData(objects);
    return this.success();
  }

  public T success(Iterable<M> objects)
  {
    return this.success( Lists.newArrayList(objects) );
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

  public void setStatus(Integer value)
  {
    this.status = value;

    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) return;

    HttpServletResponse response = requestAttributes.getResponse();
    if (response == null) return;
    
    response.setStatus(value);
  }
}