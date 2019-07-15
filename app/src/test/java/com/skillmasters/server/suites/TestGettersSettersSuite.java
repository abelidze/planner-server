package com.skillmasters.server.suites;

import com.skillmasters.server.model.EventPatternTests;
import com.skillmasters.server.model.EventTests;
import com.skillmasters.server.model.PermissionTests;
import com.skillmasters.server.model.TaskTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  EventTests.class,
  EventPatternTests.class,
  PermissionTests.class,
  TaskTests.class})
public class TestGettersSettersSuite
{
}
