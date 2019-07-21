package com.skillmasters.server.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventPatternExruleTests extends ModelTests
{
  @Test
  public void testGettersSetters()
  {
    EventPatternExrule epe = new EventPatternExrule();

    epe.setId(testLong);
    assertThat(epe.getId()).isEqualTo(testLong);

    EventPattern p = new EventPattern();
    epe.setPattern(p);
    assertThat(epe.getPattern()).isEqualTo(p);

    epe.setRule(testString);
    assertThat(epe.getRule()).isEqualTo(testString);

    epe.setCreatedAt(testDateStart);
    assertThat(epe.getCreatedAt()).isEqualTo(testDateStart);

    epe.setUpdatedAt(testDateEnd);
    assertThat(epe.getUpdatedAt()).isEqualTo(testDateEnd);
  }
}
