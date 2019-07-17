package com.skillmasters.server.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.Gson;
import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.common.requestbuilder.event.CreateEventRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.CreateTaskRequestBuilder;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import com.skillmasters.server.http.response.EventResponse;
import com.skillmasters.server.http.response.Response;
import com.skillmasters.server.http.response.TaskResponse;
import com.skillmasters.server.mock.TaskMock;
import com.skillmasters.server.mock.TaskResponseMock;
import com.skillmasters.server.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ControllerTests
{
  @Autowired
  protected MockMvc mockMvc;

  private static String apiPrefix = "/api/v1";

  protected static String eventsEndpoint = apiPrefix + "/events";

  protected static String tasksEndpoint = apiPrefix + "/tasks";

  protected String testerId = "322";

  private Gson gson = new Gson();

  private MockHttpServletRequestBuilder requestMethod(HttpMethod method, String url)
  {
    switch (method) {
      case GET:
        return get(url);
      case POST:
        return post(url);
      case PUT:
        return put(url);
      case PATCH:
        return patch(url);
      case DELETE:
        return delete(url);
      case OPTIONS:
        return options(url);
      case HEAD:
        return head(url);
    }
    assert false;
    return null;
  }

  private MockHttpServletRequestBuilder authorizedRequest(HttpMethod method, String url)
  {
    return requestMethod(method, url)
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, "tester")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);
  }

  private MockHttpServletRequestBuilder setParams(HttpMethod method, MockHttpServletRequestBuilder rb, AppRequestBuilder b)
  {
    if (method == HttpMethod.GET) {
      return rb.params(b.buildGet());
    }

    return rb.content(gson.toJson(b.buildPost()));
  }

  protected MockHttpServletRequestBuilder authorizedRequest(HttpMethod method, String url, AppRequestBuilder b)
  {
    //
    return setParams(method, authorizedRequest(method, url), b);
  }

  protected ResultActions performReqOk(RequestBuilder req) throws Exception
  {
    return mockMvc.perform(req).andExpect(status().isOk());
  }

  protected ResultActions performReq404(RequestBuilder req) throws Exception
  {
    return mockMvc.perform(req).andExpect(status().isNotFound());
  }

//  protected EventResponse parseEventResponse(MockHttpServletResponse resp) throws IOException
//  {
//    return new ObjectMapper().readValue(resp.getContentAsString(), EventResponse.class);
//  }

  protected MvcResult authorizedOkResult(HttpMethod method, String url, AppRequestBuilder b) throws Exception
  {
    MockHttpServletRequestBuilder req = authorizedRequest(method, url, b);
    ResultActions resultActions = performReqOk(req);
    return resultActions.andReturn();
  }

  protected <R extends Response> R authorizedOkResultResponse(HttpMethod method,
                                                                 String url,
                                                                 AppRequestBuilder b, Class<R> cls
  ) throws Exception
  {
    MvcResult result = authorizedOkResult(method, url, b);
    R response = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .readValue(result.getResponse().getContentAsString(), cls);
    return response;
  }


  /*
  SECTION OF INSERT HELPERS
   */
  protected EventResponse insertEvent() throws Exception
  {
    CreateEventRequestBuilder createBuilder = new CreateEventRequestBuilder();
    createBuilder.details("details").location("under the bridge").name("chilling").status("mega free");
    return authorizedOkResultResponse(HttpMethod.POST, eventsEndpoint, createBuilder, EventResponse.class);
  }

  protected List<EventResponse> insertEvents(int amount) throws Exception
  {
    List<EventResponse> eventsList = new ArrayList<EventResponse>(amount);
    for (int i = 0; i < amount; i++) {
      eventsList.add(insertEvent());
    }

    return eventsList;
  }

  protected TaskResponseMock insertTask(Event event, AppRequestBuilder b) throws Exception
  {
    Long eventId = event.getId();
    return authorizedOkResultResponse(HttpMethod.POST, tasksEndpoint+"?event_id="+eventId, b, TaskResponseMock.class);
  }

  protected TaskResponseMock insertTask(Event event) throws Exception
  {
    return insertTask(event, new AppRequestBuilder());
  }

  protected TaskResponseMock insertTask() throws Exception
  {
    EventResponse eventResponse = insertEvent();
    return insertTask(eventResponse.getData().get(0), new AppRequestBuilder());
  }

  protected EventResponse deleteEvent(Event event) throws Exception
  {
    Long id = event.getId();
    return authorizedOkResultResponse(
        HttpMethod.DELETE, eventsEndpoint+"/"+id, new AppRequestBuilder(), EventResponse.class);
  }

  protected EventResponse deleteTask(TaskMock taskMock) throws Exception
  {
    Long id = taskMock.getId();
    return authorizedOkResultResponse(
        HttpMethod.DELETE, tasksEndpoint+"/"+id, new AppRequestBuilder(), EventResponse.class);
  }

}
