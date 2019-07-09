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
  @ApiModelProperty(value = "Task's unique id", readOnly = true)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Event event;

  @ApiModelProperty(value = "Id of task's parent (another task)")
  private Long parentId;

  @ApiModelProperty(value = "Task's name")
  private String name;

  @ApiModelProperty(value = "Description for task")
  private String details;

  @ApiModelProperty(value = "Task's status", example = "IN-PROCESS")
  private String status;

  @ApiModelProperty(value = "Task's deadline", example = "1556712345000")
  private Date deadlineAt;

  @CreationTimestamp
  @ApiModelProperty(value = "Creation timestamp", readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(value = "Update timestamp", readOnly = true, example = "1556712345000")
  private Date updatedAt;

  public Task()
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