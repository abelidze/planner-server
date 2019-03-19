package com.skillmasters.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class EventPattern
{
  private @Id @GeneratedValue Long id;
  private Long eventId;
  private String type;
  private String year;
  private String weekday;
  private String month;
  private String day;
  private String hour;
  private String minute;
  private Long duration;
  // private Date startedAt;
  // private Date endedAt;
  // private Date createdAt;
  // private Date updatedAt;

  EventPattern(Long eventId)
  {
    this.eventId = eventId;
  }
}