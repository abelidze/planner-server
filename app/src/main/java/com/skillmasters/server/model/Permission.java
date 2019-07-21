package com.skillmasters.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

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
  @ApiModelProperty(value = "Permission's unique id", readOnly = true)
  private Long id;

  @ApiModelProperty(value = "Id of the user that is granted for action", example = "odZqu_9pJ0JvP6-omCa_pXC")
  private String userId;

  @ApiModelProperty(value = "Id of the entity's owner", example = "KV_sG5hz-iRrwH3zA_Tr3Ly")
  private String ownerId;

  @ApiModelProperty(value = "Entity's id", example = "1")
  private String entityId;

  @ApiModelProperty(value = "Name of granted permission")
  private String name;

  @CreationTimestamp
  @ApiModelProperty(value = "Creation timestamp", readOnly = true, example = "1556712345000")
  private Date createdAt;

  @UpdateTimestamp
  @ApiModelProperty(value = "Update timestamp", readOnly = true, example = "1556712345000")
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