package com.skillmasters.server.suites;

import com.skillmasters.server.service.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
  EventServiceTests.class,
  EventPatternServiceTests.class,
  TaskServiceTests.class,
  PermissionServiceTests.class,
  ShareServiceTests.class,
  CascadeOperationsTests.class})
public class TestServiceSuite
{
}
