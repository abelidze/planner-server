package com.skillmasters.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
public class Event
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(hidden=true)
  private Long id;
  private Long ownerId;
  private String name;
  private String details;
  private String status;
  private String location;
  // private Date createdAt;
  // private Date updatedAt;

  Event()
  {
    // default
  }

  Event(Long ownerId, String name, String details, String status, String location)
  {
    this.ownerId = ownerId;
    this.name = name;
    this.details = details;
    this.status = status;
    this.location = location;
  }
}