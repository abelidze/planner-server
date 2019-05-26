package com.skillmasters.server.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.FetchType;
import javax.persistence.EmbeddedId;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
public class EventPattern
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(readOnly = true)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Event event;

  private Byte type;
  private String year;
  private String weekday;
  private String month;
  private String day;
  private String hour;
  private String minute;
  private Long duration;
  private Date startedAt;
  private Date endedAt;

  @ApiModelProperty(readOnly = true)
  @UpdateTimestamp
  private Date createdAt;
  @ApiModelProperty(readOnly = true)
  @UpdateTimestamp
  private Date updatedAt;

  EventPattern()
  {
    // default
  }

  EventPattern(
    Byte type,
    String year,
    String weekday,
    String month,
    String day,
    String hour,
    String minute,
    Long duration
  ) {
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