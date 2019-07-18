package com.skillmasters.server.suites;

import com.skillmasters.server.http.controller.EventControllerTests;
import com.skillmasters.server.http.controller.EventPatternController;
import com.skillmasters.server.http.controller.EventPatternControllerTests;
import com.skillmasters.server.http.controller.TaskControllerTests;
import com.skillmasters.server.model.EventPatternTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    EventControllerTests.class,
    TaskControllerTests.class,
    EventPatternControllerTests.class
})
public class TestControllersSuite
{
}