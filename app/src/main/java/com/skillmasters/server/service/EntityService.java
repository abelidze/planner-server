package com.skillmasters.server.service;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.lang.reflect.Field;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.PathBuilder;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.domain.PageImpl;

import com.skillmasters.server.model.User;
import com.skillmasters.server.model.IEntity;

public class EntityService<R extends JpaRepository<T, ID> & QuerydslPredicateExecutor<T>, T extends IEntity, ID>
{
  @Autowired
  private PermissionService permissionService;

  @Autowired
  protected R repository;

  @PersistenceContext
  protected EntityManager entityManager;

  protected final Class<T> entityClass;
  protected final String entityName;

  protected EntityService(final Class<T> entityClass, String entityName)
  {
    this.entityClass = entityClass;
    this.entityName = entityName;
  }

  public R getRepository()
  {
    return repository;
  }

  public T save(T entity)
  {
    return repository.save(entity);
  }

  public T updateById(ID id, Map<String, Object> updates)
  {
    return this.updateAndSave( this.getById(id), updates );
  }

  public T updateAndSave(T entity, Map<String, Object> updates)
  {
    return repository.save( this.update(entity, updates) );
  }

  @PreAuthorize("principal.can('UPDATE', #entity)")
  public T update(T entity, Map<String, Object> updates)
  {
    updates.forEach((k, v) -> {
      String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, k);
      Field field = ReflectionUtils.findField(entityClass, fieldName);
      if (field != null) {
        ReflectionUtils.makeAccessible(field);
        final Class<?> type = field.getType();
        if (v != null) {
          if (type.equals(Long.class)) {
            ReflectionUtils.setField(field, entity, ((Number) v).longValue());
            return;
          } else if (type.equals(Date.class)) {
            ReflectionUtils.setField(field, entity, new Date( ((Number) v).longValue() ));
            return;
          } else if (type.equals(List.class)) {
            // ReflectionUtils.setField(field, entity, new ArrayList<>( ((Number) v).longValue() ));
            return;
          }
        }
        ReflectionUtils.setField(field, entity, v);
      }
    });
    return entity;
  }

  @PreAuthorize("principal.can('DELETE', #entity)")
  public void delete(T entity)
  {
    permissionService.deleteByEntity(entity);
    repository.delete(entity);
  }

  @PostAuthorize("principal.can('READ', returnObject)")
  public T getById(ID id)
  {
    // TODO: returning null is dangerous. Replace it with exception.
    return repository.findById(id).orElse(null);
  }

  @PostFilter("principal.can('READ', filterObject)")
  public Iterable<T> getByQuery(BooleanExpression query)
  {
    return repository.findAll(query);
  }

  @PostFilter("principal.can('READ', filterObject)")
  public Iterable<T> getByQuery(JPQLQuery query)
  {
    return query.fetch();
  }

  public Page<T> getByQuery(BooleanExpression query, Pageable pageable)
  {
    return repository.findAll(query, pageable);
  }

  public Page<T> getByQuery(JPAQuery query, Pageable pageable)
  {
    PathBuilder<?> builder = new PathBuilderFactory().create(this.entityClass);
    Querydsl querydsl = new Querydsl(entityManager, builder);
    JPQLQuery pagedQuery = querydsl.applyPagination(pageable, query);
    return new PageImpl<>(pagedQuery.fetch(), pageable, query.fetchCount());
  }

  public long count(BooleanExpression query)
  {
    return repository.count(query);
  }

  public boolean exists(BooleanExpression query)
  {
    return repository.exists(query);
  }

  protected User getCurrentUser()
  {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      return new User();
    }
    return (User) auth.getPrincipal();
  }
}