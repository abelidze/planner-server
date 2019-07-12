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
@SequenceGenerator(name = "eventId", sequenceName = "event_seq", allocationSize = 1)
public class Event implements IEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventId")
  @ApiModelProperty(value = "Event's unique id", readOnly = true)
  private Long id;

  @NotNull("Field owner_id can't be null")
  @Column(nullable = false)
  @ApiModelProperty(value = "Owner's unique id", readOnly = true, example = "0")
  private String ownerId;

  @ApiModelProperty(value = "Event's name", example = "eventname")
  private String name;

  @ApiModelProperty(value = "Description for event", example = "-")
  private String details;

  @ApiModelProperty(value = "Event's status", example = "busy")
  private String status;

  @ApiModelProperty(value = "Event's location", example = "unknown")
  private String location;

  @CreationTimestamp
  @ApiModelProperty(value = "Creation timestamp", readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(value = "Update timestamp", readOnly = true, example = "1556712345000")
  private Date updatedAt;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<EventPattern> patterns;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<Task> tasks;

  public Event()
  {
    this.ownerId = "227";
  }

  @JsonIgnore
  public String getEntityName()
  {
    return "EVENT";
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