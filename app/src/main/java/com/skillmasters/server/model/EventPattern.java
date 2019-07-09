package com.skillmasters.server.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Date;
import java.util.List;
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
  // @JsonProperty(value = "event_id", access = JsonProperty.Access.READ_ONLY)
  // @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  // @JsonIdentityReference(alwaysAsId = true)
  @JsonIgnore
  private Event event;

  @NotNull
  @Column(nullable = false)
  @ApiModelProperty(value = "Duration of a single event's instance")
  private Long duration = 0L;

  @NotNull
  @Column(nullable = false)
  @ApiModelProperty(value = "Timezone to work in", example = "UTC")
  private String timezone = "UTC";

  @ApiModelProperty(value = "iCal's RRULE string", example = "FREQ=DAILY;INTERVAL=1")
  private String rrule;

  @Deprecated
  @ApiModelProperty(example = "FREQ=WEEKLY;INTERVAL=2;BYDAY=TU,TH")
  private String exrule;

  @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL)
  @ApiModelProperty(value = "Array of iCal's EXRULE")
  private List<EventPatternExrule> exrules = new ArrayList<>();

  @NotNull
  @Column(nullable = false)
  @ApiModelProperty(value = "Start of the first event's instance", example = "1556712345000")
  private Date startedAt = new Date();

  @NotNull
  @Column(nullable = false)
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

  @ApiModelProperty(readOnly = true)
  public Long getEventId()
  {
    return event.getId();
  }

  @JsonIgnore
  public String getOwnerId()
  {
    return this.event.getOwnerId();
  }

  @JsonIgnore
  public String getEntityName()
  {
    return "PATTERN";
  }
}