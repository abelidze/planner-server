package com.skillmasters.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "permissions")
@SequenceGenerator(name = "permissionId", sequenceName = "permission_seq", allocationSize = 1)
public class Permission implements IEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permissionId")
  private Long id;

  private String userId;
  private String ownerId;
  private String entityId;
  private String name;

  @CreationTimestamp
  private Date createdAt;

  @UpdateTimestamp
  private Date updatedAt;

  public Permission()
  {
    // default
  }

  // @JsonIgnore
  // public String getOwnerId()
  // {
  //   return this.userId;
  // }

  @JsonIgnore
  public String getEntityName()
  {
    return "PERMISSION";
  }
}