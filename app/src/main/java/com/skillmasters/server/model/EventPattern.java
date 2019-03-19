package com.skillmasters.server.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
public class EventPattern
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(hidden=true)
  private Long id;
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

  EventPattern()
  {
    // default
  }

  EventPattern(
    Long eventId,
    String type,
    String year,
    String weekday,
    String month,
    String day,
    String hour,
    String minute,
    Long duration
  ) {
    this.eventId = eventId;
    this.type = type;
    this.year = year;
    this.weekday = weekday;
    this.month = month;
    this.day = day;
    this.hour = hour;
    this.minute = minute;
    this.duration = duration;
  }
}