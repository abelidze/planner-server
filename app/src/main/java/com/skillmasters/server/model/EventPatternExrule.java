package com.skillmasters.server.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillmasters.server.validation.RecurrenceRule;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Table(name = "exrules")
@SequenceGenerator(name = "exruleId", sequenceName = "exrule_seq", allocationSize = 1)
public class EventPatternExrule
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exruleId")
  @ApiModelProperty(readOnly = true)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "pattern_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private EventPattern pattern;

  @NotNull(message = "Field rule for exrule can't be null")
  @RecurrenceRule(message = "Field rule for exrule is not valid")
  @Column(nullable = false)
  @ApiModelProperty(example = "FREQ=WEEKLY;INTERVAL=2;BYDAY=TU,TH")
  private String rule;

  @CreationTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date updatedAt;

  public EventPatternExrule()
  {
    //
  }
}