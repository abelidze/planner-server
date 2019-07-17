package com.skillmasters.server.suites;

import com.skillmasters.server.http.controller.EventControllerTests;
import com.skillmasters.server.http.controller.TaskControllerTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    EventControllerTests.class,
    TaskControllerTests.class
})
public class TestControllersSuite
{
}