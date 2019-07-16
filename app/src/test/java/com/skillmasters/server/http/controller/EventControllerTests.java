package com.skillmasters.server.http.controller;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    CreateEventRequestBuilder createBuilder = new CreateEventRequestBuilder();
    createBuilder.details("some details").location("FEFU").name("my name").status("ultra busy");

//    EventResponse createResponse = authorizedOkResultResponse(HttpMethod.POST, eventsEndpoint, createBuilder, EventResponse.class);
    authorizedOkResultResponse(HttpMethod.POST, eventsEndpoint, createBuilder, EventResponse.class);

    ListEventsRequestBuilder listBuilder = new ListEventsRequestBuilder();
    EventResponse response = authorizedOkResultResponse(HttpMethod.GET, eventsEndpoint, listBuilder, EventResponse.class);
    List<Event> events = response.getData();
    assertThat(events.size()).isEqualTo(1);
    assertThat(events.get(0).getDetails()).isEqualTo("some details");
    assertThat(events.get(0).getLocation()).isEqualTo("FEFU");
    assertThat(events.get(0).getName()).isEqualTo("my name");
    assertThat(events.get(0).getStatus()).isEqualTo("ultra busy");

    assertThat(events.get(0).getId()).isNotNull().isInstanceOf(Long.class);
    assertThat(events.get(0).getOwnerId()).isEqualTo(testerId);
    assertThat(events.get(0).getCreatedAt()).isCloseTo(new Date(), 10000);
  }
}
