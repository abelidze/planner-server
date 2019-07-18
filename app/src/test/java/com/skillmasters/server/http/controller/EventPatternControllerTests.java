package com.skillmasters.server.http.controller;

import com.skillmasters.server.common.requestbuilder.pattern.CreatePatternRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.ListPatternsRequestBuilder;
import com.skillmasters.server.http.response.EventPatternResponse;
import com.skillmasters.server.mock.model.EventPatternMock;
import com.skillmasters.server.mock.response.EventPatternResponseMock;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.EventPatternExrule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
//
//    for (int i = 0; i < 20; i++) {
//      CreatePatternRequestBuilder createBuilder = new CreatePatternRequestBuilder();
//      createBuilder.duration(200L);
//      insertPattern(event.getId(), createBuilder);
//
//      ListPatternsRequestBuilder b = new ListPatternsRequestBuilder();
//
//      EventPatternResponseMock response = authorizedOkResultResponse(HttpMethod.GET,
//          patternsEndpoint+"?event_id="+event.getId(), b, EventPatternResponseMock.class);
//
//      assertThat(response.getCount()).isEqualTo(i+1);
//
//    }
//  }

}
