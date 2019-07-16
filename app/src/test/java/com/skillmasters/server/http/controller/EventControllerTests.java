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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

  @Test
  public void testGetEventsByIds() throws Exception
  {
    List<EventResponse> events = insertEvents(10);
    ListEventsRequestBuilder b = new ListEventsRequestBuilder();
    List<Long> ids = new ArrayList(10);

    for (EventResponse er : events) {
      for (Event e : er.getData()) {
        ids.add(e.getId());
      }
    }
    Map<Long, Boolean> idsSubset = new HashMap<>();
    idsSubset.put(ids.get(1), false);
    idsSubset.put(ids.get(3), false);
    idsSubset.put(ids.get(7), false);

    b.id(new ArrayList(idsSubset.keySet()));
    EventResponse response = authorizedOkResultResponse(HttpMethod.GET, eventsEndpoint, b, EventResponse.class);
    assertThat(response.getCount()).isEqualTo(3);
    assertThat(response.getData().size()).isEqualTo(3);

    for (Event e : response.getData()) {
      idsSubset.put(e.getId(), true);
    }

    // met all values in subset
    for (Boolean v : idsSubset.values()) {
      assertThat(v).isTrue();
    }

  }

  @Test
  public void testGetEventsByCreatedFromTo() throws Exception
  {
    Date start = new Date();
    insertEvents(2);
    Date end = new Date();

    testGetEventsByFromToHelper(0L, start.getTime(), 0);
    testGetEventsByFromToHelper(end.getTime(), Long.MAX_VALUE, 0);
    testGetEventsByFromToHelper(start.getTime()-10000, end.getTime()+10000, 2);
  }

  private void testGetEventsByFromToHelper(Long start, Long end, int expectedEventsAmount) throws Exception
  {
    ListEventsRequestBuilder b = new ListEventsRequestBuilder();
    b.createdFrom(start);
    b.createdTo(end);
    EventResponse response = authorizedOkResultResponse(HttpMethod.GET, eventsEndpoint, b, EventResponse.class);
    assertThat(response.getCount()).isEqualTo(expectedEventsAmount);
    assertThat(response.getData().size()).isEqualTo(expectedEventsAmount);
  }
}

