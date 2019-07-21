package com.skillmasters.server.http.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModelProperty;

@Data
public class PermissionRequest
{
  public enum ActionType
  {
    READ,
    UPDATE,
    DELETE,
  }

  public enum EntityType
  {
    EVENT,
    PATTERN,
    TASK,
  }

  @ApiModelProperty(example = "0")
  private Long entityId;

  @NotNull
  @ApiModelProperty(example = "EVENT")
  private EntityType entityType;

  @NotNull
  @ApiModelProperty(example = "READ")
  private ActionType action;
}