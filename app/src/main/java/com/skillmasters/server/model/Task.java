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
@SequenceGenerator(name = "taskId", sequenceName = "task_seq", allocationSize = 1)
public class Task implements IEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskId")
  @ApiModelProperty(readOnly = true)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
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
    return "TASK";
  }
}