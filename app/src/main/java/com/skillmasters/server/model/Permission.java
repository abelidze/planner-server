package com.skillmasters.server.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "permissions")
@SequenceGenerator(name = "permissionId", sequenceName = "permission_seq", allocationSize = 1)
public class Permission
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permissionId")
  private Long id;

  private String userId;
  private String entityId;
  private String name;

  @CreationTimestamp
  private Date createdAt;

  @UpdateTimestamp
  private Date updatedAt;

  Permission()
  {
    // default
  }
}