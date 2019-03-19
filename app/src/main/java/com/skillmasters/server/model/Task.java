package com.skillmasters.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Task
{
  private @Id @GeneratedValue Long id;
  private Long eventId;
  private Long parentId;
  private String name;
  private String details;
  private String status;
  // private Date deadlineAt;
  // private Date createdAt;
  // private Date updatedAt;

  Task(String name)
  {
    this.name = name;
  }
}