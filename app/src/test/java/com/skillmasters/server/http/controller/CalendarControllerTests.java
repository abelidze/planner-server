package com.skillmasters.server.http.controller;

import biweekly.ICalendar;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import com.skillmasters.server.http.response.EventResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

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
    ICalendar ical = export("tester");
    assertThat(ical).isNotNull();

    for (EventResponse er : events) {
      deleteEvent(er.getData().get(0));
    }

    assertThat(getAllEvents().size()).isEqualTo(0);
    assertThat(getAllTasks().size()).isEqualTo(0);
    assertThat(getAllPatterns().size()).isEqualTo(0);

    importCal(ical, "tester");
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
