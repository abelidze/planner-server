package com.skillmasters.server.http.middleware.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.util.Strings;
import com.skillmasters.server.http.middleware.security.model.FirebaseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FirebaseAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter
{
  public final static String TOKEN_HEADER = "X-Firebase-Auth";

  public FirebaseAuthenticationTokenFilter()
  {
    super("/api/v1/**");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  {
    final String authToken = request.getHeader(TOKEN_HEADER);
    if (Strings.isNullOrEmpty(authToken)) {
      throw new RuntimeException("Invalid auth token");
    }

    return getAuthenticationManager().authenticate(new FirebaseAuthenticationToken(authToken));
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
}
