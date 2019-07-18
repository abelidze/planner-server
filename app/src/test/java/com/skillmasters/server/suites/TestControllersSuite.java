package com.skillmasters.server.suites;

import com.skillmasters.server.http.controller.*;
import com.skillmasters.server.model.EventPatternTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    EventControllerTests.class,
    TaskControllerTests.class,
    EventPatternControllerTests.class,
    PermissionControllerTests.class
})
public class TestControllersSuite
{
}