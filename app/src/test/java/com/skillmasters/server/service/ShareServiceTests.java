package com.skillmasters.server.service;

import com.skillmasters.server.model.Permission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ShareServiceTests extends ServiceTests
{
  @Autowired
  protected ShareService shareService;

  @Test
  public void testCachePermission()
  {
    String token = populate();
    assertThat(shareService.validateToken(token).size()).isEqualTo(1);

    shareService.revokeToken(token);
    assertThat(shareService.validateToken(token)).isNull();
  }

  @Test
  public void testCachePermissionList()
  {
    List<Permission> permissionList = new ArrayList<>(10);
    for (int i = 0; i < 10; i++) {
      permissionList.add(new Permission());
    }

    String token = shareService.cachePermissionList(permissionList);

    assertThat(shareService.validateToken(token).size()).isEqualTo(10);

    shareService.revokeToken(token);
    assertThat(shareService.validateToken(token)).isNull();
  }

  private String populate()
  {
    Permission perm = new Permission();
    return shareService.cachePermission(perm);
  }

}
