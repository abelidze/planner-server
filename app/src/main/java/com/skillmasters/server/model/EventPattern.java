package com.skillmasters.server.model;

import lombok.Data;

import java.util.TimeZone;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Table(name = "patterns")
@SequenceGenerator(name = "seq", sequenceName = "pattern_seq")
public class EventPattern
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
  @ApiModelProperty(readOnly = true)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Event event;

  @NotNull
  @Column(nullable = false)
  private Long duration = 0L;

  @NotNull
  @Column(nullable = false)
  @ApiModelProperty(example = "UTC")
  private String timezone = "UTC";

  @ApiModelProperty(example = "FREQ=DAILY;INTERVAL=1")
  private String rrule;

  @ApiModelProperty(example = "FREQ=WEEKLY;INTERVAL=2;BYDAY=TU,TH")
  private String exrule;

  @NotNull
  @Column(nullable = false)
  @ApiModelProperty(example = "1556712345000")
  private Date startedAt = new Date();

  @NotNull
  @Column(nullable = false)
  @ApiModelProperty(example = "1556712345000")
  private Date endedAt = new Date(Long.MAX_VALUE);

  @CreationTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date updatedAt;

  EventPattern()
  {
    //
  }

  EventPattern(
    String timezone,
    String rrule,
    String exrule,
    Long duration,
    Date startedAt,
    Date endedAt
  ) {
    this.setTimezone(timezone);
    this.setRrule(rrule);
    this.setExrule(exrule);
    this.setStartedAt(startedAt);
    this.setDuration(duration);
    this.setEndedAt(endedAt);
  }
}