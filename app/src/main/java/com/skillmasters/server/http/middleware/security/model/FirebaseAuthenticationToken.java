package com.skillmasters.server.http.middleware.security.model;

import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Data
public class FirebaseAuthenticationToken extends UsernamePasswordAuthenticationToken {
  private static final long serialVersionUID = 1L;
  private final String token;

  public FirebaseAuthenticationToken(final String token) {
    super(null, null);
    this.token = token;
  }

//  public String getToken() {
//    return token;
//  }

}