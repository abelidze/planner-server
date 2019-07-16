package com.skillmasters.server.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmasters.server.common.ListEventsRequestBuilder;
import com.skillmasters.server.http.response.EventResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

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
  public void testConnection() throws Exception
  {
    ListEventsRequestBuilder b = new ListEventsRequestBuilder();
    MvcResult result = getAuthorizedOkResult(eventsEndpoint, b);
    EventResponse response = getAuthorizedOkResultResponse(eventsEndpoint, b, EventResponse.class);
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getCount()).isEqualTo(0);
  }
}
