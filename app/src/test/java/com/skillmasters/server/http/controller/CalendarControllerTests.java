package com.skillmasters.server.http.controller;

import biweekly.ICalendar;
import com.skillmasters.server.common.requestbuilder.event.CreateEventRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.CreatePatternRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.CreateTaskRequestBuilder;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import com.skillmasters.server.http.response.EventResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.GregorianCalendar;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CalendarControllerTests extends ControllerTests
{
  @Test
  public void testExport() throws Exception
  {
    insertEvents(20);
    ICalendar ical = export("tester");
    assertThat(ical).isNotNull();
  }

  @Test
  public void testImport() throws Exception
  {
    List<EventResponse> events = insertEvents(20);
    entityManager.flush();
    entityManager.clear();
    //add some patterns
    CreatePatternRequestBuilder createPatternRequestBuilder = new CreatePatternRequestBuilder();
    createPatternRequestBuilder.startedAt(new GregorianCalendar(2018, 2, 2).getTimeInMillis());
    createPatternRequestBuilder.endedAt(new GregorianCalendar(2019, 2, 2).getTimeInMillis());
    insertPattern(events.get(4).getData().get(0), createPatternRequestBuilder);
    entityManager.flush();
    entityManager.clear();
    CreateTaskRequestBuilder createTaskRequestBuilder = new CreateTaskRequestBuilder();
    insertTask(events.get(8).getData().get(0), createTaskRequestBuilder);
    entityManager.flush();
    entityManager.clear();

    assertThat(getAllEvents().size()).isEqualTo(20);
    assertThat(getAllTasks().size()).isEqualTo(1);
    assertThat(getAllPatterns().size()).isEqualTo(1);

    entityManager.clear();
    String icalStr = export("tester").write();
    assertThat(icalStr).isNotEmpty();

    for (EventResponse er : events) {
      assertThat(er.getData().get(0).getTasks().size()).isEqualTo(0);
      deleteEvent(er.getData().get(0));
    }
    entityManager.flush();
    entityManager.clear();

    assertThat(getAllEvents().size()).isEqualTo(0);
    assertThat(getAllTasks().size()).isEqualTo(0);
    assertThat(getAllPatterns().size()).isEqualTo(0);

    entityManager.clear();
    importCal(icalStr, "tester");
    entityManager.flush();
    entityManager.clear();
    assertThat(getAllPatterns().size()).isEqualTo(1);
    assertThat(getAllTasks().size()).isEqualTo(1);
    assertThat(getAllEvents().size()).isEqualTo(20);
  }

  @Test
  public void testInvalidImportString() throws Exception
  {
    MockHttpServletRequestBuilder rb = requestMethod(HttpMethod.POST, importEndpoint+"/raw")
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, "tester")
        .contentType(MediaType.TEXT_PLAIN).content("privet ");

    MockHttpServletResponse response = mockMvc.perform(rb).andReturn().getResponse();
    assertThat(response.getStatus()).isNotEqualTo(200);
  }
}
