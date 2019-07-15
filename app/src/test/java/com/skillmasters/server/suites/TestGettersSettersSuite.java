package com.skillmasters.server.suites;

import com.skillmasters.server.model.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  EventTests.class,
  EventPatternTests.class,
  PermissionTests.class,
  TaskTests.class,
  UserTests.class})
public class TestGettersSettersSuite
{
}
