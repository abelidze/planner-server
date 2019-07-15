package com.skillmasters.server.service;

import com.skillmasters.server.common.EventGenerator;
import com.skillmasters.server.http.request.PermissionRequest;
import com.skillmasters.server.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PermissionServiceTests extends ServiceTests
{
  @Autowired
  protected PermissionService permissionService;

  @Autowired
  protected EventService eventService;

  @Autowired
  protected EventPatternService eventPatternService;

  @Autowired
  protected TaskService taskService;

  @Test
  public void testCRUD()
  {
    assertThat(countRowsInTable(permissionsTablename)).isEqualTo(0);
    Permission perm = new Permission();
    perm = permissionService.save(perm);
    perm.setOwnerId(testUser.getId());

    permissionService.getRepository().flush();
    assertThat(countRowsInTable(permissionsTablename)).isEqualTo(1);
    Map<String, Object> updates= new HashMap<>();
    updates.put("name","new name");

    perm = permissionService.update(perm, updates);
    permissionService.getRepository().flush();
    assertThat(countRowsInTable(permissionsTablename)).isEqualTo(1);

    assertThat(permissionService.getById(perm.getId()).getName()).isEqualTo("new name");

    permissionService.delete(perm);
    permissionService.getRepository().flush();
    assertThat(countRowsInTable(permissionsTablename)).isEqualTo(0);
  }

  @Test
  public void testPermissions() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    authOwningUser();
    String owningUserId = testUser.getId();

    authGrantedUser();
    String grantedUserId = testUser.getId();

    authNotGrantedUser();
    String notGrantedUserId = testUser.getId();

    List<Class<? extends IEntity>> entities = getShareableEntities();

    for (Class<? extends IEntity> entityClass : entities) {
      IEntity entity = entityClass.getDeclaredConstructor().newInstance();
      assertThat(entity).isNotNull();
      entity = saveEntity(entity);

      for (PermissionRequest.ActionType action : PermissionRequest.ActionType.values()) {
        authOwningUser();
        permissionService.grantPermission(grantedUserId, action.name(), entity);
        usersCheckPermission(action, entity);
      }

    }

    authOwningUser();
  }

  @Test
  public void testGrantPermissionsForAllEntities() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    authOwningUser();
    String owningUserId = testUser.getId();

    authGrantedUser();
    String grantedUserId = testUser.getId();

    authNotGrantedUser();
    String notGrantedUserId = testUser.getId();

    List<Class<? extends IEntity>> entityClasses = getShareableEntities();

    for (Class<? extends IEntity> entityClass : entityClasses) {
      List<IEntity> entities = new ArrayList<>(10);
      for (int i = 0; i < 10; i++) {
        entities.add(saveEntity(entityClass.getDeclaredConstructor().newInstance()));
      }

      for (PermissionRequest.ActionType action : PermissionRequest.ActionType.values()) {
        authOwningUser();

        PermissionRequest.EntityType entityType = null;
        switch (entities.get(0).getEntityName()) {
          case "EVENT":
            entityType = PermissionRequest.EntityType.EVENT;
            break;
          case "PATTERN":
            entityType = PermissionRequest.EntityType.PATTERN;
            break;
          case "TASK":
            entityType = PermissionRequest.EntityType.TASK;
            break;
        }

        assert entityType != null;
        permissionService.grantPermission(grantedUserId, action.name(), entityType.name());
        for (IEntity entity : entities) {
          usersCheckPermission(action, entity);
        }
      }
    }
    authOwningUser();
  }

  // test for bug#2
  // https://github.com/abelidze/planner-server/issues/2
  @Test
  public void testRemoveDanglingEntities() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    authGrantedUser();
    String grantedUserId = testUser.getId();

    List<Class<? extends IEntity>> entities = getShareableEntities();

    for (Class<? extends IEntity> entityClass : entities) {
      for (PermissionRequest.ActionType action : PermissionRequest.ActionType.values()) {
        IEntity entity = entityClass.getDeclaredConstructor().newInstance();
        entity = saveEntity(entity);

        authOwningUser();
        assertThat(countRowsInTable(permissionsTablename)).isEqualTo(0);
        permissionService.grantPermission(grantedUserId, action.name(), entity);
        flushAll();
        assertThat(countRowsInTable(permissionsTablename)).isEqualTo(1);

        deleteEntity(entity);
        assertThat(countRowsInTable(permissionsTablename)).isEqualTo(0);
      }
    }
  }

  // test for bug#3
  // https://github.com/abelidze/planner-server/issues/3
  @Test
  public void testDuplicateSharedEntities() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
  {
    authGrantedUser();
    String grantedUserId = testUser.getId();

    List<Class<? extends IEntity>> entities = getShareableEntities();

    for (Class<? extends IEntity> entityClass : entities) {
      for (PermissionRequest.ActionType action : PermissionRequest.ActionType.values()) {
        IEntity entity = entityClass.getDeclaredConstructor().newInstance();
        entity = saveEntity(entity);

        authOwningUser();
        assertThat(countRowsInTable(permissionsTablename)).isEqualTo(0);
        for (int i = 0; i < 20; i ++) {
          permissionService.grantPermission(grantedUserId, action.name(), entity);
        }
        flushAll();
        assertThat(countRowsInTable(permissionsTablename)).isEqualTo(1);

        deleteEntity(entity);
        assertThat(countRowsInTable(permissionsTablename)).isEqualTo(0);
      }
    }
  }

  private void usersCheckPermission(PermissionRequest.ActionType grantedAction, IEntity entity)
  {
    // check that granted user can use permission
    authGrantedUser();
    try {
      executeAction(grantedAction, entity);
    } catch (AccessDeniedException e) {
      fail("Action " + grantedAction.name() + " was granted but could not be executed " + entity.getEntityName());
    }

    authNotGrantedUser();
    try {
      executeAction(grantedAction, entity);
      fail("Action " + grantedAction.name() + " was not granted but could be executed " + entity.getEntityName());
    } catch (AccessDeniedException e) {
      //
    }
  }

  private void executeAction(PermissionRequest.ActionType grantedAction, IEntity entity)
  {
    if (grantedAction.equals(PermissionRequest.ActionType.READ)) {
      switch (entity.getEntityName()) {
        case "EVENT":
          eventService.getById(((Event) entity).getId());
          break;

        case "PATTERN":
          eventPatternService.getById(((EventPattern) entity).getId());
          break;

        case "TASK":
          taskService.getById(((Task) entity).getId());
          break;
      }
    }
    else if (grantedAction.equals(PermissionRequest.ActionType.UPDATE)) {
      Map<String, Object> updates = new HashMap<>();

      switch (entity.getEntityName()) {
        case "EVENT":
          Event event = (Event) entity;
          updates.put("details", "some rnd details");
          Event updatedEvent = eventService.update(event, updates);
          break;

        case "PATTERN":
          EventPattern eventPattern = (EventPattern) entity;
          updates.put("duration", 111);
          EventPattern updatedEventPattern = eventPatternService.update(eventPattern, updates);
          break;

        case "TASK":
          Task task = (Task) entity;
          updates.put("name", "new name");
          Task updatedTask = taskService.update(task, updates);
          break;
      }
    }
    else if (grantedAction.equals(PermissionRequest.ActionType.DELETE)) {
      switch (entity.getEntityName()) {
        case "EVENT":
          Event event = (Event) entity;
          eventService.delete(event);
          break;

        case "PATTERN":
          EventPattern eventPattern = (EventPattern) entity;
          eventPatternService.delete(eventPattern);
          break;

        case "TASK":
          Task task = (Task) entity;
          taskService.delete(task);
          break;
      }
    }
    flushAll();
  }

  private IEntity saveEntity(IEntity entity)
  {
    authOwningUser();
    IEntity savedEntity = null;

    Event event = EventGenerator.genEventWithOwner(100, testUser.getId());
    event = eventService.save(event);
    switch (entity.getEntityName()) {
      case "EVENT":
        savedEntity = event;
        break;

      case "PATTERN":
        EventPattern eventPattern = (EventPattern) entity;
        eventPattern.setEvent(event);
        Date curDate = new Date();
        eventPattern.setStartedAt(curDate);
        eventPattern.setEndedAt(new Date(curDate.getTime() + 1000));
        savedEntity = eventPatternService.save(eventPattern);
        break;

      case "TASK":
        Task task = (Task) entity;
        task.setEvent(event);
        savedEntity = taskService.save(task);
        break;
    }

    flushAll();

    return savedEntity;
  }

  private void deleteEntity(IEntity entity)
  {
    authOwningUser();

    switch (entity.getEntityName()) {
      case "EVENT":
        eventService.delete((Event) entity);
        break;

      case "PATTERN":
        eventPatternService.delete((EventPattern) entity);
        break;

      case "TASK":
        taskService.delete((Task) entity);
        break;
    }

    flushAll();
  }

  private List<Class<? extends IEntity>> getShareableEntities()
  {
    List<Class<? extends IEntity>> entities = new ArrayList<>();
    entities.add(Event.class);
    entities.add(EventPattern.class);
    entities.add(Task.class);
    return entities;
  }

  private void flushAll()
  {
    eventService.getRepository().flush();
    eventPatternService.getRepository().flush();
    taskService.getRepository().flush();

  }
}
