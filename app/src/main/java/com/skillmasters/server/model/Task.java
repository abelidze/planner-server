package com.skillmasters.server.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
public class Task
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(readOnly = true)
  private Long id;
  private Long eventId;
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
    Long eventId,
    Long parentId,
    String name,
    String details,
    String status
  ) {
    this.eventId = eventId;
    this.parentId = parentId;
    this.name = name;
    this.details = details;
    this.status = status;
  }
}