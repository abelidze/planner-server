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
@Table(name = "tasks")
@SequenceGenerator(name = "seq", sequenceName = "task_seq")
public class Task
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

  private Long parentId;
  private String name;
  private String details;
  private String status;

  @ApiModelProperty(example = "1556712345000")
  private Date deadlineAt;

  @CreationTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date updatedAt;

  Task()
  {
    // default
  }

  Task(
    Event event,
    Long parentId,
    String name,
    String details,
    String status
  ) {
    this.event = event;
    this.parentId = parentId;
    this.name = name;
    this.details = details;
    this.status = status;
  }
}