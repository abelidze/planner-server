package com.skillmasters.server.model;

import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
// import org.springframework.data.annotation.CreatedDate;
// import org.springframework.data.annotation.LastModifiedDate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Table(name = "events")
@SequenceGenerator(name = "seq", sequenceName = "event_seq")
public class Event
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
  @ApiModelProperty(readOnly = true)
  private Long id;

  @NotNull
  @Column(nullable = false)
  @ApiModelProperty(readOnly = true, example = "0")
  private String ownerId;

  private String name;

  @ApiModelProperty(example = "-")
  private String details;

  @ApiModelProperty(example = "busy")
  private String status;

  @ApiModelProperty(example = "unknown")
  private String location;

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
    this.ownerId = "227";
  }

  Event(
    Long id,
    String ownerId,
    String name,
    String details,
    String status,
    String location,
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
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.patterns = patterns;
    this.tasks = tasks;
  }

  // @Override
  // public String toString()
  // {
  //   // event pattern calls event.toString(), which calls eventPatter[].toString();
  //   return "";
  // }

  // public int hashCode()
  // {
  //   // same as prev
  //   return id.intValue();
  // }

}