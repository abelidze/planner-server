package com.skillmasters.server.model;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PermissionTests extends ModelTests
{
  @Test
  public void testGettersSetters()
  {
    Permission perm = new Permission();

    perm.setId(testLong);
    assertThat(perm.getId()).isEqualTo(testLong);

    perm.setUserId(testString);
    assertThat(perm.getUserId()).isEqualTo(testString);

    perm.setEntityId(testString);
    assertThat(perm.getEntityId()).isEqualTo(testString);

    perm.setName(testString);
    assertThat(perm.getName()).isEqualTo(testString);

    perm.setCreatedAt(testDateStart);
    assertThat(perm.getCreatedAt()).isEqualTo(testDateStart);

    perm.setUpdatedAt(testDateEnd);
    assertThat(perm.getUpdatedAt()).isEqualTo(testDateEnd);
  }

}
