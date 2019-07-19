package com.skillmasters.server.http.controller;

import biweekly.Biweekly;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.ValidationWarnings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skillmasters.server.common.requestbuilder.AppRequestBuilder;
import com.skillmasters.server.common.requestbuilder.event.CreateEventRequestBuilder;
import com.skillmasters.server.common.requestbuilder.event.ListInstancesRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.CreatePatternRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.ListPatternsRequestBuilder;
import com.skillmasters.server.common.requestbuilder.pattern.UpdatePatternRequestBuilder;
import com.skillmasters.server.common.requestbuilder.permission.GrantRequestBuilder;
import com.skillmasters.server.common.requestbuilder.permission.PermissionsRequestBuilder;
import com.skillmasters.server.common.requestbuilder.permission.ShareRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.CreateTaskRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.ListTasksRequestBuilder;
import com.skillmasters.server.common.requestbuilder.task.UpdateTaskRequestBuilder;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import com.skillmasters.server.http.request.PermissionRequest;
import com.skillmasters.server.http.response.*;
import com.skillmasters.server.mock.model.EventPatternMock;
import com.skillmasters.server.mock.model.TaskMock;
import com.skillmasters.server.mock.response.EventPatternResponseMock;
import com.skillmasters.server.mock.response.TaskResponseMock;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
public class ControllerTests
{
  @Autowired
  protected MockMvc mockMvc;

  @PersistenceContext
  EntityManager entityManager;

  private static String apiPrefix = "/api/v1";

  protected static String eventsEndpoint = apiPrefix + "/events";

  protected static String tasksEndpoint = apiPrefix + "/tasks";

  protected static String patternsEndpoint = apiPrefix + "/patterns";

  protected static String permissionsEndpoint = apiPrefix + "/permissions";

  protected static String grantEndpoint = apiPrefix + "/grant";

  protected static String shareEndpoint = apiPrefix + "/share";

  protected static String userEndpoint = apiPrefix + "/user";

  protected static String exportEndpoint = apiPrefix + "/export";

  protected static String importEndpoint = apiPrefix + "/import";

  protected static String instancesEndpoint = apiPrefix + "/events/instances";

  protected String testerId = "322";

  private Gson gson = new Gson();

  protected MockHttpServletRequestBuilder requestMethod(HttpMethod method, String url)
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

  protected EventResponse insertEvent(CreateEventRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.POST, eventsEndpoint, b, EventResponse.class);
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


  protected EventInstanceResponse getInstances(ListInstancesRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.GET, instancesEndpoint, b, EventInstanceResponse.class);
  }

  protected EventResponse deleteEvent(Event event) throws Exception
  {
    Long id = event.getId();
    return authorizedOkResultResponse(
        HttpMethod.DELETE, eventsEndpoint + "/" + id, new AppRequestBuilder(), EventResponse.class);
  }


  // TASK
  protected TaskResponseMock insertTask(Event event, AppRequestBuilder b) throws Exception
  {
    Long eventId = event.getId();
    return insertTask(eventId, b);
  }

  protected TaskResponseMock insertTask(Long eventId, AppRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.POST, tasksEndpoint + "?event_id=" + eventId, b, TaskResponseMock.class);
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
        HttpMethod.GET, tasksEndpoint + "/" + id, new AppRequestBuilder(), TaskResponseMock.class);
  }

  protected TaskResponseMock getTasks(ListTasksRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.GET, tasksEndpoint, b, TaskResponseMock.class);
  }

  protected EventResponse deleteTask(TaskMock taskMock) throws Exception
  {
    Long id = taskMock.getId();
    return authorizedOkResultResponse(
        HttpMethod.DELETE, tasksEndpoint + "/" + id, new AppRequestBuilder(), EventResponse.class);
  }

  protected TaskMock updateTask(TaskMock task, UpdateTaskRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.PATCH, tasksEndpoint + "/" + task.getId(),
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
    return authorizedOkResultResponse(HttpMethod.POST, patternsEndpoint + "?event_id=" + eventId, b, EventPatternResponseMock.class);
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

  protected EventPatternMock updatePattern(EventPatternMock pattern, UpdatePatternRequestBuilder b) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.PATCH, patternsEndpoint + "/" + pattern.getId(),
        b, EventPatternResponseMock.class).getData().get(0);
  }

  // PERMISSIONS
  protected PermissionResponse grantPermission(String userId, Long entityId, PermissionRequest.EntityType entityType,
                                               PermissionRequest.ActionType action) throws Exception
  {
    GrantRequestBuilder b = new GrantRequestBuilder();

    b.userId(userId).entityId(entityId).entityType(entityType).action(action);
    return authorizedOkResultResponse(HttpMethod.GET, grantEndpoint, b, PermissionResponse.class);
  }

  protected PermissionResponse listPermissions(String userToken, PermissionRequest.EntityType entityType,
                                               Boolean mine, int status) throws Exception
  {
    PermissionsRequestBuilder b = new PermissionsRequestBuilder();
    b.entityType(entityType).mine(mine);
    MockHttpServletRequestBuilder rb = requestMethod(HttpMethod.GET, permissionsEndpoint)
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, userToken)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON).params(b.buildGet());

    MockHttpServletResponse response = mockMvc.perform(rb).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(status);

    return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .readValue(response.getContentAsString(), PermissionResponse.class);
  }

  protected String getShareLink(ShareRequestBuilder b, int status) throws Exception
  {
    return mockMvc.perform(requestMethod(HttpMethod.GET, shareEndpoint)
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, "tester")
        .accept(MediaType.TEXT_PLAIN).params(b.buildGet())).andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
  }

  protected String getMultipleShareLink(List<ShareRequestBuilder> permissions, int status) throws Exception
  {
    List<Map<String, Object>> permissionsForQuery = new ArrayList<>();
    for (ShareRequestBuilder rb : permissions) {
      permissionsForQuery.add(rb.buildPost());
    }
    return mockMvc.perform(requestMethod(HttpMethod.POST, shareEndpoint)
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, "tester")
        .accept(MediaType.TEXT_PLAIN)
        .contentType(MediaType.APPLICATION_JSON)
        .content(gson.toJson(permissionsForQuery)))
        .andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
  }

  protected PermissionResponse activateShareLink(String link, String userToken, String userId, int status) throws Exception
  {
    MockHttpServletRequestBuilder rb = requestMethod(HttpMethod.GET, link)
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, userToken)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);
    MockHttpServletResponse response = mockMvc.perform(rb).andExpect(status().is(status)).andReturn().getResponse();

    return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .readValue(response.getContentAsString(), PermissionResponse.class);

  }

  protected PermissionResponse revokePermission(Long id) throws Exception
  {
    return authorizedOkResultResponse(HttpMethod.DELETE, permissionsEndpoint+"/"+id, PermissionResponse.class);
  }

  // iCal
  protected ICalendar export(String userToken) throws Exception
  {
    MockHttpServletRequestBuilder rb = requestMethod(HttpMethod.GET, exportEndpoint)
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, userToken)
        .accept("text/calendar");

    MockHttpServletResponse response = mockMvc.perform(rb).andReturn().getResponse();

    ICalendar ical = Biweekly.parse(response.getContentAsString()).first();
    ValidationWarnings warnings = ical.validate(ICalVersion.V2_0);
//    assertThat(warnings.isEmpty()).isTrue();

    assertThat(ical).isNotNull();
    return  ical;
  }

  protected void importCal(String icalStr, String userToken) throws Exception
  {
    MockHttpServletRequestBuilder rb = requestMethod(HttpMethod.POST, importEndpoint+"/raw")
        .header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, userToken)
        .contentType(MediaType.TEXT_PLAIN).content(icalStr);

    MockHttpServletResponse response = mockMvc.perform(rb).andExpect(status().isOk()).andReturn().getResponse();
  }
}
