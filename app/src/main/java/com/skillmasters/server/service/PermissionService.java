package com.skillmasters.server.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Service;

import com.skillmasters.server.repository.PermissionRepository;
import com.skillmasters.server.model.User;
import com.skillmasters.server.model.IEntity;
import com.skillmasters.server.model.Permission;
import com.skillmasters.server.model.QPermission;

@Service
public class PermissionService extends EntityService<PermissionRepository, Permission, Long>
{
  public PermissionService()
  {
    super(Permission.class, "PERMISSION");
  }

  public Permission generatePermission(String userId, String action, IEntity entity)
  {
    Permission permission = new Permission();
    permission.setName(action + "_" + entity.getEntityName());
    permission.setUserId(userId);
    permission.setOwnerId(getCurrentUser().getId());
    permission.setEntityId(entity.getId().toString());
    return permission;
  }

  public void grantPermission(String userId, String action, IEntity entity)
  {
    repository.save( generatePermission(userId, action, entity) );
  }

  public void grantPermission(Permission permission)
  {
    repository.save(permission);
  }

  public boolean hasPermission(User user, String action, IEntity entity)
  {
    action += "_" + entity.getEntityName();
    return repository.exists(
        getHasPermissionQuery(user.getId(), action)
        .and(QPermission.permission.entityId.in(entity.getOwnerId(), entity.getId().toString()))
      );
  }

  public BooleanExpression getHasPermissionQuery(String userId, String perm)
  {
    QPermission qPermission = QPermission.permission;
    return qPermission.userId.eq(userId).and(qPermission.name.eq(perm));
  }
}