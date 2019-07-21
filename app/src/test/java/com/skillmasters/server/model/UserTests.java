package com.skillmasters.server.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTests extends ModelTests
{
  @Test
  public void testGettersSetters()
  {
    User u = new User();

    u.setId(testString);
    assertThat(u.getId()).isEqualTo(testString);

    u.setUsername(testString);
    assertThat(u.getUsername()).isEqualTo(testString);

    u.setPhoto(testString);
    assertThat(u.getPhoto()).isEqualTo(testString);
  }
}
