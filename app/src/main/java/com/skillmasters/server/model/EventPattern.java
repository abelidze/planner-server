package com.skillmasters.server.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
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

  private Byte type = 0;
  private Long duration = 0L;

  @ApiModelProperty(example = "FREQ=DAILY;INTERVAL=1")
  private String rrule;

  @ApiModelProperty(example = "FREQ=WEEKLY;INTERVAL=2;BYDAY=TU,TH")
  private String exrule;

  @ApiModelProperty(example = "1556712345000")
  private Date startedAt;

  @ApiModelProperty(example = "1556712345000")
  private Date endedAt;

  @CreationTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date updatedAt;

  EventPattern()
  {
    // default
  }

  EventPattern(
    Byte type,
    String rrule,
    String exrule,
    Long duration,
    Date startedAt,
    Date endedAt
  ) {
    this.type = type;
    this.rrule = rrule;
    this.exrule = exrule;

    if (startedAt == null) {
      startedAt = new Date();
    }

    if (endedAt == null) {
      if (duration == null) {
        endedAt = new Date(Long.MAX_VALUE);
      } else {
        endedAt = new Date(startedAt.getTime() + duration);
      }
    }

    if (duration == null) {
      duration = endedAt.getTime() - startedAt.getTime();
    }

    this.duration = duration;
    this.startedAt = startedAt;
    this.endedAt = endedAt;
  }
}