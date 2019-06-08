package com.skillmasters.server.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.Date;
import javax.persistence.*;
// import org.springframework.data.annotation.CreatedDate;
// import org.springframework.data.annotation.LastModifiedDate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Builder(toBuilder=true)
public class Event
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ApiModelProperty(readOnly = true)
  private Long id;

  private Long ownerId;
  private String name;
  private String details;
  private String status;
  private String location;

  @Transient
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date startedAt;

  @Transient
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date endedAt;

  @CreationTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(readOnly = true, example = "1556712345000")
  private Date updatedAt;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<EventPattern> patterns;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<Task> tasks;

  Event()
  {
    // default
  }

  Event(
    Long id,
    Long ownerId,
    String name,
    String details,
    String status,
    String location,
    Date startedAt,
    Date endedAt,
    Date createdAt,
    Date updatedAt,
    List<EventPattern> patterns,
    List<Task> tasks
  ) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.details = details;
    this.status = status;
    this.location = location;
    this.startedAt = startedAt;
    this.endedAt = endedAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.patterns = patterns;
    this.tasks = tasks;
  }
}