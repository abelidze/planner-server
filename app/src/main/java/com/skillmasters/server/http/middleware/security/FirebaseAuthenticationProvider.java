package com.skillmasters.server.http.middleware.security;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import com.skillmasters.server.model.User;
import com.skillmasters.server.service.PermissionService;
import com.skillmasters.server.http.middleware.security.SimpleAuthenticationToken;

import java.util.concurrent.ExecutionException;

@Component
public class FirebaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
{
  @Autowired
  private FirebaseAuth firebaseAuth;

  @Autowired
  private PermissionService permissionService;

  @Override
  public boolean supports(Class<?> authentication)
  {
    return SimpleAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  protected void additionalAuthenticationChecks(
    UserDetails userDetails,
    UsernamePasswordAuthenticationToken authentication
  ) throws AuthenticationException { }

  @Override
  protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException
  {
    final SimpleAuthenticationToken authenticationToken = (SimpleAuthenticationToken) authentication;

    // backdoor. Request by Sergey
    if (authenticationToken.getToken().equals("serega_mem")) {
      return new User("sergo@zink.ovic", User.DEFAULT_USER_ID, permissionService);
    }
    // pls don't remove. tests depend on this token.
    if (authenticationToken.getToken().equals("tester")) {
      return new User("test@test.com", "322", permissionService);
    }
    if (authenticationToken.getToken().equals("tester1")) {
      return new User("test1@test.com", "323", permissionService);
    }
    if (authenticationToken.getToken().equals("tester2")) {
      return new User("test2@test.com", "324", permissionService);
    }

    ApiFuture<FirebaseToken> task = firebaseAuth.verifyIdTokenAsync(authenticationToken.getToken());
    try {
      FirebaseToken token = task.get();
      return new User(token.getName(), token.getUid(), permissionService);
    } catch (InterruptedException | ExecutionException e) {
      throw new SessionAuthenticationException(e.getMessage());
    }
  }
}
