package com.skillmasters.server.model;

import lombok.Data;

import java.util.Set;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.CascadeType;
// import org.springframework.data.annotation.CreatedDate;
// import org.springframework.data.annotation.LastModifiedDate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
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

  @ApiModelProperty(readOnly = true)
  @CreationTimestamp
  private Date createdAt;
  @ApiModelProperty(readOnly = true)
  @UpdateTimestamp
  private Date updatedAt;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
  private Set<EventPattern> patterns;

  Event()
  {
    // default
  }

  Event(Long ownerId, String name, String details, String status, String location, Set<EventPattern> patterns)
  {
    this.ownerId = ownerId;
    this.name = name;
    this.details = details;
    this.status = status;
    this.location = location;
    this.patterns = patterns;
  }

  @Override
  public String toString() {
    // event pattern calls event.toString(), which calls eventPatter[].toString();
    return "";
  }

  public int hashCode() {
    // same as prev
    return id.intValue();
  }

}