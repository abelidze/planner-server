package com.skillmasters.server.model;

public interface IEntity
{
  public Long getId();
  public String getOwnerId();
  public abstract String getEntityName();
}