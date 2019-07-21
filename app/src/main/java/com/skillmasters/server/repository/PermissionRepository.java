package com.skillmasters.server.repository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.skillmasters.server.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, QuerydslPredicateExecutor<Permission>
{
  @Transactional
  @Modifying
  @Query("DELETE FROM Permission p WHERE p.entityId = ?1 AND p.ownerId = ?2 AND p.name LIKE %?3")
  public void deleteEntityPermissions(String id, String owner, String type);
}