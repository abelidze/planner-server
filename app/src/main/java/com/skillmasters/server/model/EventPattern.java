package com.skillmasters.server.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Table(name = "patterns")
@Check(constraints = "ended_at >= started_at")
@SequenceGenerator(name = "patternId", sequenceName = "pattern_seq", allocationSize = 1)
public class EventPattern implements IEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patternId")
  @ApiModelProperty(value = "Pattern's unique id", readOnly = true)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Event event;

  @NotNull(message = "Field duration can't be null")
  @Min(value = 0, message = "Duration can't be less then 0")
  @Column(nullable = false)
  @ApiModelProperty(value = "Duration of a single event's instance")
  private Long duration = 0L;

  @NotNull(message = "Field timezone can't be null")
  @Column(nullable = false)
  @ApiModelProperty(value = "Timezone to work in", example = "UTC")
  private String timezone = "UTC";

  @Pattern(regexp = "|^((?!UNTIL[\\s=]+).)*$", message = "UNTIL is auto-generated and can't be setted manually")
  @ApiModelProperty(value = "iCal's RRULE string", example = "FREQ=DAILY;INTERVAL=1")
  private String rrule;

  @Deprecated
  @ApiModelProperty(example = "FREQ=WEEKLY;INTERVAL=2;BYDAY=TU,TH")
  private String exrule;

  @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL)
  @ApiModelProperty(value = "Array of iCal's EXRULE")
  private List<EventPatternExrule> exrules = new ArrayList<>();

  @NotNull(message = "Field started_at can't be null")
  @Column(name = "started_at", nullable = false)
  @ApiModelProperty(value = "Start of the first event's instance", example = "1556712345000")
  private Date startedAt = new Date();

  @NotNull(message = "Field ended_at can't be null")
  @Column(name = "ended_at", nullable = false)
  @ApiModelProperty(value = "Timestamp until that event can occur", example = "1556712345000")
  private Date endedAt = new Date(Long.MAX_VALUE);

  @CreationTimestamp
  @ApiModelProperty(value = "Creation timestamp", readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(value = "Update timestamp", readOnly = true, example = "1556712345000")
  private Date updatedAt;

  public EventPattern()
  {
    //
  }

  @AssertTrue(message = "Field ended_at must be greater or equal to started_at")
  private boolean isRangeValid()
  {
    return endedAt == null || startedAt == null || !endedAt.before(startedAt);
  }

  @ApiModelProperty(readOnly = true)
  public Long getEventId()
  {
    return event.getId();
  }

  @JsonIgnore
  public String getOwnerId()
  {
    return event.getOwnerId();
  }

  @JsonIgnore
  public String getEntityName()
  {
    return "PATTERN";
  }
}