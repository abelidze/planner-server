package com.skillmasters.server.http.controller;

import com.skillmasters.server.common.requestbuilder.event.ListEventsRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.CreatePatternRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.ListPatternsRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.UpdatePatternRequestBuilder;
import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.mock.model.EventPatternMock;
import com.skillmasters.server.mock.response.EventPatternResponseMock;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.EventPatternExrule;
import com.skillmasters.server.service.EventPatternService;
import com.skillmasters.server.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventPatternControllerTests extends ControllerTests
{
  @Test
  public void testReadCreate() throws Exception
  {
    for (int i = 0; i < 8; i++) {
      insertPattern();
      List<EventPatternMock> patterns = getAllPatterns();
      assertThat(patterns.size()).isEqualTo(i+1);
    }
  }

  @Test
  public void testReadCreateWithExrules() throws Exception
  {
    Event event = insertEvent().getData().get(0);
    CreatePatternRequestBuilder b = new CreatePatternRequestBuilder();
    List<EventPatternExrule> exrules = new ArrayList<>();

    for (int j = 0; j < 20; j++) {
      EventPatternExrule exr = new EventPatternExrule();
      exr.setRule("FREQ=DAILY;INTERVAL=1");
      exrules.add(exr);
    }
    b.exrules(exrules);
    insertPattern(event.getId(), b);

    List<EventPatternMock> patterns = getAllPatterns();
    assertThat(patterns.size()).isEqualTo(1);

    EventPatternMock epFromDb = patterns.get(0);
    assertThat(epFromDb.getExrules().size()).isEqualTo(20);
  }

  @Test
  public void testCreateWithoutRrule() throws Exception
  {
    CreatePatternRequestBuilder b = new CreatePatternRequestBuilder();
    Date start = new Date();
    Long duration = 20000L;
    b.duration(duration);
    b.startedAt(start.getTime());

    insertPattern(b);
    EventPatternMock pattern = getAllPatterns().get(0);
    // should calculate
    assertThat(pattern.getEndedAt().getTime()).isEqualTo(start.getTime() + duration);
  }

  @Test
  public void testCreateWithNotPositiveDuration() throws Exception
  {
    // duration = ended - started
    CreatePatternRequestBuilder b = new CreatePatternRequestBuilder();
    Date start = new Date(1563165747);
    Date end = new Date(1563165747);;

    Long duration = 0L;
    b.duration(duration);

    b.startedAt(start.getTime());
    b.endedAt(end.getTime());

    insertPattern(b);
    EventPatternMock pattern = getAllPatterns().get(0);
    // sholud calculate
    assertThat(pattern.getDuration()).isEqualTo(end.getTime() - start.getTime());

  }

  @Test
  public void testCreate404() throws Exception
  {
    Long notExistingEventId = 2222L;
    CreatePatternRequestBuilder b = new CreatePatternRequestBuilder();
    performReq404(authorizedRequest(HttpMethod.POST, patternsEndpoint+"?event_id="+notExistingEventId, b));
  }

//  @Test
//  public void testGetPatternsForEventBug() throws Exception
//  {
//    Event event = insertEvent().getData().get(0);
//    for (int i = 0; i < 20; i++) {
//      CreatePatternRequestBuilder createBuilder = new CreatePatternRequestBuilder();
//      createBuilder.duration(200L);
//      insertPattern(event.getId(), createBuilder);
//      ListPatternsRequestBuilder b = new ListPatternsRequestBuilder();
//
//      EventPatternResponseMock response = authorizedOkResultResponse(HttpMethod.GET,
//          patternsEndpoint+"?event_id="+event.getId(), b, EventPatternResponseMock.class);
//
//      assertThat(response.getCount()).isEqualTo(i+1);
//
//    }
//  }

  @Test
  public void testGetPatternsSeveralIds() throws Exception
  {
    Event event = insertEvent().getData().get(0);
    List<EventPatternMock> createPatterns = insertPatterns(event, 14);
    Map<Long, Boolean> idsSubset = new HashMap<>();

    idsSubset.put(createPatterns.get(1).getId(), false);
    idsSubset.put(createPatterns.get(3).getId(), false);
    idsSubset.put(createPatterns.get(7).getId(), false);

    ListPatternsRequestBuilder b = new ListPatternsRequestBuilder();
    b.id(new ArrayList<>(idsSubset.keySet()));

    EventPatternResponseMock getTasksResponse = getPatterns(b);
    assertThat(getTasksResponse.getCount()).isEqualTo(3);
    assertThat(getTasksResponse.getData().size()).isEqualTo(3);

    for (EventPatternMock t : getTasksResponse.getData()) {
      idsSubset.put(t.getId(), true);
    }

    for (Boolean v : idsSubset.values()) {
      assertThat(v).isTrue();
    }
  }

  @Test
  public void testGetPatternsByEventIds() throws Exception
  {
    Event event1 = insertEvent().getData().get(0);
    Event event2 = insertEvent().getData().get(0);

    List<EventPatternMock> patterns1 = insertPatterns(event1, 13);
    List<EventPatternMock> patterns2 = insertPatterns(event2, 7);

    ListPatternsRequestBuilder b = new ListPatternsRequestBuilder();
    b.events(Arrays.asList(event1.getId(), event2.getId()));

    EventPatternResponseMock response = getPatterns(b);
    assertThat(response.getCount()).isEqualTo(20);

    List<EventPatternMock> result = response.getData();
    assertThat(result.size()).isEqualTo(20);
  }

  @Test
  public void testGetPatternById() throws Exception
  {
    insertPattern().getData().get(0);
    EventPatternMock pattern = insertPattern().getData().get(0);

    EventPatternResponseMock response = authorizedOkResultResponse(
        HttpMethod.GET, patternsEndpoint+"/"+pattern.getId(), EventPatternResponseMock.class);

    assertThat(response.getCount()).isEqualTo(1);
    EventPatternMock ep = response.getData().get(0);
    assertThat(ep).isEqualTo(pattern);
  }

  @Test
  public void testGetPatternById404() throws Exception
  {
    Long notExistingEventId = 2222L;
    performReq404(authorizedRequest(HttpMethod.GET, patternsEndpoint+"/"+notExistingEventId));
  }

  @Test
  public void testUpdatePatternById404() throws Exception
  {
    Long notExistingEventId = 2222L;
    performReq404(authorizedRequest(HttpMethod.PATCH, patternsEndpoint+"/"+notExistingEventId, new UpdatePatternRequestBuilder()));
  }

  @Test
  public void testDeletePatternById404() throws Exception
  {
    Long notExistingEventId = 2222L;
    performReq404(authorizedRequest(HttpMethod.DELETE, patternsEndpoint+"/"+notExistingEventId));
  }

  @Test
  public void testUpdate() throws Exception
  {
    EventPatternMock pattern = insertPattern().getData().get(0);
    UpdatePatternRequestBuilder b = new UpdatePatternRequestBuilder();

    b.duration(300000L);
    b.rrule("FREQ=YEARLY;BYMONTH=1");

    List<EventPatternMock> allPatterns = getAllPatterns();
    assertThat(allPatterns.size()).isEqualTo(1);
    EventPatternMock patternMock = allPatterns.get(0);

    EventPatternMock updatedPattern = updatePattern(pattern, b);
    assertThat(updatedPattern.getDuration()).isEqualTo(300000L);
    assertThat(updatedPattern.getRrule()).isEqualTo("FREQ=YEARLY;BYMONTH=1");
  }

}
