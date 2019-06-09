package com.skillmasters.server.http.middleware.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.skillmasters.server.http.middleware.security.SimpleAuthenticationToken;

public class FirebaseAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter
{
  public final static String TOKEN_HEADER = "X-Firebase-Auth";

  public FirebaseAuthenticationTokenFilter()
  {
    super("/api/v1/**");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    final String authToken = request.getHeader(TOKEN_HEADER);
    if (Strings.isNullOrEmpty(authToken)) {
      throw new AuthenticationCredentialsNotFoundException("Invalid auth token");
    }
    return getAuthenticationManager().authenticate(new SimpleAuthenticationToken(authToken));
  }

  /**
   * Make sure the rest of the filterchain is satisfied
   *
   * @param request
   * @param response
   * @param chain
   * @param authResult
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void successfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain,
    Authentication authResult
  ) throws IOException, ServletException
  {
    super.successfulAuthentication(request, response, chain, authResult);

    // As this authentication is in HTTP header, after success we need to continue the request normally
    // and return the response as if the resource was not secured at all
    chain.doFilter(request, response);
  }

  @Override
  protected void unsuccessfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException error
  ) throws IOException, ServletException
  {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, error.getMessage());
  }
}
