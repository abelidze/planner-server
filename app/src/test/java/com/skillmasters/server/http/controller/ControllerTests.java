package com.skillmasters.server.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmasters.server.common.AppRequestBuilder;
import com.skillmasters.server.http.response.EventResponse;
import com.skillmasters.server.http.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ControllerTests
{
  @Autowired
  protected MockMvc mockMvc;

  private static String apiPrefix = "/api/v1";

  protected static String eventsEndpoint = apiPrefix + "/events";

  private MockHttpServletRequestBuilder getAuthorized(String url)
  {
    return get(url).header(FirebaseAuthenticationTokenFilter.TOKEN_HEADER, "tester")
        .accept(MediaType.APPLICATION_JSON);
  }

  protected MockHttpServletRequestBuilder getAuthorized(String url, MultiValueMap<String, String> params)
  {
    return getAuthorized(url).params(params);
  }

  protected ResultActions performReqOk(RequestBuilder req) throws Exception
  {
    return mockMvc.perform(req).andExpect(status().isOk());
  }

  protected EventResponse parseEventResponse(MockHttpServletResponse resp) throws IOException
  {
    return new ObjectMapper().readValue(resp.getContentAsString(), EventResponse.class);
  }

  protected MvcResult getAuthorizedOkResult(String url, AppRequestBuilder b) throws Exception
  {
    MockHttpServletRequestBuilder req = getAuthorized(eventsEndpoint, b.build());
    ResultActions resultActions = performReqOk(req);
    return resultActions.andReturn();
  }

  protected <R extends Response> R getAuthorizedOkResultResponse(String url, AppRequestBuilder b, Class<R> cls) throws Exception
  {
    MvcResult result = getAuthorizedOkResult(url, b);
    R response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), cls);
    return response;
  }
}
