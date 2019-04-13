package com.skillmasters.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
public class Task
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(hidden=true)
  private Long id;
  private Long eventId;
  private Long parentId;
  private String name;
  private String details;
  private String status;
  // private Date deadlineAt;
  // private Date createdAt;
  // private Date updatedAt;

  Task()
  {
    // default
  }

  Task(Long eventId, Long parentId, String name, String details, String status)
  {
    this.eventId = eventId;
    this.parentId = parentId;
    this.name = name;
    this.details = details;
    this.status = status;
  }
}