package com.skillmasters.server.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmasters.server.common.CreateEventRequestBuilder;
import com.skillmasters.server.common.ListEventsRequestBuilder;
import com.skillmasters.server.http.response.EventResponse;
import com.skillmasters.server.model.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventControllerTests extends ControllerTests
{
  @Test
  public void testGetEmptyEvents() throws Exception
  {
    ListEventsRequestBuilder b = new ListEventsRequestBuilder();
    EventResponse response = authorizedOkResultResponse(HttpMethod.GET, eventsEndpoint, b, EventResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getCount()).isEqualTo(0);

    List<Event> events = response.getData();
    assertThat(events.size()).isEqualTo(0);
  }

  @Test
  public void testCreateEvent() throws Exception
  {
    CreateEventRequestBuilder b = new CreateEventRequestBuilder();
    b.details("some details").location("FEFU").name("my name").status("retarded");

    EventResponse createResponse = authorizedOkResultResponse(HttpMethod.POST, eventsEndpoint, b, EventResponse.class);

    ListEventsRequestBuilder b1 = new ListEventsRequestBuilder();
    EventResponse response = authorizedOkResultResponse(HttpMethod.GET, eventsEndpoint, b1, EventResponse.class);
    List<Event> events = response.getData();
    assertThat(events.size()).isEqualTo(1);
    assertThat(events.get(0).getDetails()).isEqualTo("some details");
  }
}
