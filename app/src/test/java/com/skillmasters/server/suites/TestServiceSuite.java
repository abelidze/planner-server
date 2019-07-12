package com.skillmasters.server.suites;

import com.skillmasters.server.service.EventPatternServiceTests;
import com.skillmasters.server.service.EventServiceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
    EventServiceTests.class,
    EventPatternServiceTests.class})
public class TestServiceSuite
{
}
