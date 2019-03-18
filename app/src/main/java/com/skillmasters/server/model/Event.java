package com.skillmasters.server.model;

// import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// @Data
@Entity
class Event
{
  private @Id @GeneratedValue Long id;
  private Long ownerId;
  private String name;
  private String details;
  private String status;
  private String location;
  // private Date createdAt;
  // private Date updatedAt;

  Event(String name)
  {
    this.name = name;
  }
}