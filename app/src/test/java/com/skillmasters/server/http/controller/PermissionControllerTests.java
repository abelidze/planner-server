package com.skillmasters.server.http.controller;

import com.skillmasters.server.common.requestbuilder.permission.GrantRequestBuilder;
import com.skillmasters.server.common.requestbuilder.permission.PermissionsRequestBuilder;
import com.skillmasters.server.common.requestbuilder.permission.ShareRequestBuilder;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import com.skillmasters.server.http.request.PermissionRequest;
import com.skillmasters.server.http.response.PermissionResponse;
import com.skillmasters.server.model.IEntity;
import com.skillmasters.server.model.Permission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PermissionControllerTests extends ControllerTests
{
  private static String ownerToken = "tester";
  private static String grantedUserToken = "tester1";
  private static String notGrantedUserToken = "tester2";


  private static String ownerId = "322";
  private static String grantedUserId = "323";
  private static String notGrantedUserId = "324";

  @Test
  public void testCreateList() throws Exception
  {
    for (PermissionRequest.EntityType entityType : PermissionRequest.EntityType.values()) {
      for (PermissionRequest.ActionType actionType : PermissionRequest.ActionType.values()) {
        IEntity entity = insertEntity(entityType);

        grantPermission(grantedUserId, entity.getId(), entityType, actionType);
        //owner
        assertThat(permissionIsInList(ownerToken, entityType, actionType, entity.getId().toString(), true)).isTrue();
        assertThat(permissionIsInList(grantedUserToken, entityType, actionType, entity.getId().toString(), false)).isTrue();
        assertThat(permissionIsInList(notGrantedUserToken, entityType, actionType, entity.getId().toString(), false)).isFalse();
      }
    }
  }

  private IEntity insertEntity(PermissionRequest.EntityType type) throws Exception
  {
    switch (type) {
      case EVENT:
        return insertEvent().getData().get(0);
      case PATTERN:
        return insertPattern().getData().get(0);
      case TASK:
        return insertTask().getData().get(0);
    }
    assert false;
    return null;
  }

  private Boolean permissionIsInList(String userToken,
                                     PermissionRequest.EntityType entityType,
                                     PermissionRequest.ActionType actionType,
                                     String entityId, Boolean mine) throws Exception
  {
    PermissionResponse response = listPermissions(userToken, entityType, mine, 200);
    String name = actionType.name() + "_" + entityType.toString().toUpperCase();
    int metCounter = 0;
    for (Permission perm : response.getData()) {
      if (perm.getEntityId().equals(entityId) && perm.getName().equals(name)) {
        metCounter += 1;
      }
    }
    assertThat(metCounter).isIn(0, 1);
    return metCounter == 1;
  }

  @Test
  public void testDupicateGrantPermissions() throws Exception
  {
    for (PermissionRequest.EntityType entityType : PermissionRequest.EntityType.values()) {
      for (PermissionRequest.ActionType actionType : PermissionRequest.ActionType.values()) {
        IEntity entity = insertEntity(entityType);

        grantPermission(grantedUserId, entity.getId(), entityType, actionType);

        GrantRequestBuilder b = new GrantRequestBuilder();
        b.userId(grantedUserId).entityId(entity.getId()).entityType(entityType).action(actionType);
        mockMvc.perform(authorizedRequest(HttpMethod.GET, grantEndpoint).params(b.buildGet())).andExpect(status().is(204));
      }
    }
  }

  @Test
  public void testSharePermissions() throws Exception
  {
    for (PermissionRequest.EntityType entityType : PermissionRequest.EntityType.values()) {
      for (PermissionRequest.ActionType actionType : PermissionRequest.ActionType.values()) {
        IEntity entity = insertEntity(entityType);

        ShareRequestBuilder b = new ShareRequestBuilder();
        b.action(actionType).entityId(entity.getId()).entityType(entityType);
        String shareLink = getShareLink(b, 200);
        activateShareLink(shareLink, grantedUserToken, grantedUserId, 200);

        assertThat(permissionIsInList(ownerToken, entityType, actionType, entity.getId().toString(), true)).isTrue();
        assertThat(permissionIsInList(grantedUserToken, entityType, actionType, entity.getId().toString(), false)).isTrue();
        assertThat(permissionIsInList(notGrantedUserToken, entityType, actionType, entity.getId().toString(), false)).isFalse();
      }
    }
  }

  @Test
  public void testShareMultiplePermissions() throws Exception
  {
    for (PermissionRequest.EntityType entityType : PermissionRequest.EntityType.values()) {
      for (PermissionRequest.ActionType actionType : PermissionRequest.ActionType.values()) {
        List<IEntity> entities = new ArrayList<>(10);
        List<ShareRequestBuilder> permissions = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
          IEntity entity = insertEntity(entityType);
          entities.add(entity);

          ShareRequestBuilder p = new ShareRequestBuilder();
          p.entityType(entityType).action(actionType).entityId(entity.getId());

          permissions.add(p);
        }

        String shareLink = getMultipleShareLink(permissions, 200);
        activateShareLink(shareLink, grantedUserToken, grantedUserId, 200);

        for (IEntity entity : entities) {
          assertThat(permissionIsInList(ownerToken, entityType, actionType, entity.getId().toString(), true)).isTrue();
          assertThat(permissionIsInList(grantedUserToken, entityType, actionType, entity.getId().toString(), false)).isTrue();
          assertThat(permissionIsInList(notGrantedUserToken, entityType, actionType, entity.getId().toString(), false)).isFalse();
        }
      }
    }
  }
}
