package com.skillmasters.server.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.Gson;
import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.common.requestbuilder.event.CreateEventRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.CreatePatternRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.ListPatternsRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.CreateTaskRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.ListTasksRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.UpdateTaskRequestBuilder;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import com.skillmasters.server.http.response.EventResponse;
import com.skillmasters.server.http.response.Response;
import com.skillmasters.server.http.response.TaskResponse;
import com.skillmasters.server.mock.model.EventPatternMock;
import com.skillmasters.server.mock.model.TaskMock;
import com.skillmasters.server.mock.response.EventPatternResponseMock;
import com.skillmasters.server.mock.response.TaskResponseMock;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
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
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
public class ControllerTests
{
  @Autowired
  protected MockMvc mockMvc;

  private static String apiPrefix = "/api/v1";

  protected static String eventsEndpoint = apiPrefix + "/events";

  protected static String tasksEndpoint = apiPrefix + "/tasks";

  protected static String patternsEndpoint = apiPrefix + "/patterns";

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

  protected MockHttpServletRequestBuilder authorizedRequest(HttpMethod method, String url)
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

  protected <R extends Response> R authorizedOkResultResponse(HttpMethod method, String url, Class<R> cls
  ) throws Exception
  {
    return authorizedOkResultResponse(method, url, new AppRequestBuilder(), cls);
  }


  /*
  SECTION OF HELPERS
   */

  // EVENT
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

  protected List<Event> getAllEvents() throws Exception
  {
    EventResponse resp = authorizedOkResultResponse(HttpMethod.GET, eventsEndpoint,
        new AppRequestBuilder(), EventResponse.class);
    return resp.getData();
  }

  protected EventResponse deleteEvent(Event event) throws Exception
  {
    Long id = event.getId();
    return authorizedOkResultResponse(
        HttpMethod.DELETE, eventsEndpoint+"/"+id, new AppRequestBuilder(), EventResponse.class);
  }


  // TASK
  protected TaskResponseMock insertTask(Event event, AppRequestBuilder b) throws Exception
  {
    Long eventId = event.getId();
    return insertTask(eventId, b);
  }

  protected TaskResponseMock insertTask(Long eventId, AppRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.POST, tasksEndpoint+"?event_id="+eventId, b, TaskResponseMock.class);
  }

  protected TaskResponseMock insertTask(Long eventId) throws Exception
  {
    return insertTask(eventId, new AppRequestBuilder());
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

  protected TaskResponseMock insertTask(CreateTaskRequestBuilder b) throws Exception
  {
    EventResponse eventResponse = insertEvent();
    return insertTask(eventResponse.getData().get(0), b);
  }

  protected List<TaskMock> insertTasks(int amount) throws Exception
  {
    List<TaskMock> result = new ArrayList<>(amount);
    for (int i = 0; i < amount; i++) {
      TaskResponseMock insertOneResponse = insertTask();
      assertThat(insertOneResponse.getData().size()).isEqualTo(1);
      TaskMock t = insertOneResponse.getData().get(0);
      result.add(t);
    }
    return result;
  }

  protected TaskResponseMock insertTaskWithStatus(String status) throws Exception
  {
    CreateTaskRequestBuilder b = new CreateTaskRequestBuilder();
    b.status(status);
    return insertTask(b);
  }

  protected TaskResponseMock insertTaskWithDeadline(Long deadline) throws Exception
  {
    CreateTaskRequestBuilder b = new CreateTaskRequestBuilder();
    b.deadlineAt(deadline);
    return insertTask(b);
  }

  protected List<TaskMock> getAllTasks() throws Exception
  {
    TaskResponseMock resp = authorizedOkResultResponse(HttpMethod.GET, tasksEndpoint,
        new ListTasksRequestBuilder(), TaskResponseMock.class);

    return resp.getData();
  }

  protected TaskResponseMock getTaskOkById(Long id) throws Exception
  {
    return authorizedOkResultResponse(
        HttpMethod.GET, tasksEndpoint+"/"+id, new AppRequestBuilder(), TaskResponseMock.class);
  }

  protected TaskResponseMock getTasks(ListTasksRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.GET, tasksEndpoint, b, TaskResponseMock.class);
  }

  protected EventResponse deleteTask(TaskMock taskMock) throws Exception
  {
    Long id = taskMock.getId();
    return authorizedOkResultResponse(
        HttpMethod.DELETE, tasksEndpoint+"/"+id, new AppRequestBuilder(), EventResponse.class);
  }

  protected TaskMock updateTask(TaskMock task, UpdateTaskRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.PATCH, tasksEndpoint+"/"+ task.getId(),
        b, TaskResponseMock.class).getData().get(0);
  }

  // PATTERN
  protected EventPatternResponseMock insertPattern() throws Exception
  {
    EventResponse eventResponse = insertEvent();
    return insertPattern(eventResponse.getData().get(0), new AppRequestBuilder());
  }

  protected EventPatternResponseMock insertPattern(Event event, AppRequestBuilder b) throws Exception
  {
    return insertPattern(event.getId(), b);
  }

  protected EventPatternResponseMock insertPattern(Long eventId, AppRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.POST, patternsEndpoint+"?event_id="+eventId, b, EventPatternResponseMock.class);
  }
  protected EventPatternResponseMock insertPattern(CreatePatternRequestBuilder b) throws Exception
  {
    return insertPattern(insertEvent().getData().get(0), b);
  }

  protected List<EventPatternMock> insertPatterns(Event event, int amount) throws Exception
  {
    List<EventPatternMock> patternsList = new ArrayList<>(amount);
    for (int i = 0; i < amount; i++) {
      patternsList.add(insertPattern(event.getId(), new AppRequestBuilder()).getData().get(0));
    }
    return patternsList;
  }

  protected List<EventPatternMock> getAllPatterns() throws Exception
  {
    EventPatternResponseMock resp = authorizedOkResultResponse(HttpMethod.GET, patternsEndpoint,
        new AppRequestBuilder(), EventPatternResponseMock.class);
    return resp.getData();
  }

  protected EventPatternResponseMock getPatterns(ListPatternsRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.GET, patternsEndpoint, b, EventPatternResponseMock.class);
  }

  protected EventPatternResponseMock getPatterns(ListTasksRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.GET, patternsEndpoint, b, EventPatternResponseMock.class);
  }
}
