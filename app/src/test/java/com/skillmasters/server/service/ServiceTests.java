package com.skillmasters.server.service;

import com.skillmasters.server.http.middleware.security.SimpleAuthenticationToken;
import com.skillmasters.server.model.QEvent;
import com.skillmasters.server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@AutoConfigureMockMvc
class ServiceTests {
  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  @Qualifier("authManager")
  protected AuthenticationManager manager;

  @Autowired
  protected PermissionService permissionService;

  protected User testUser;

  protected static String eventsTablename = "events";

  protected  QEvent qEvent = QEvent.event;

  @PostConstruct
  private void init()
  {
    Authentication auth = manager.authenticate(new SimpleAuthenticationToken("tester"));
    SecurityContextHolder.getContext().setAuthentication(auth);
    testUser = (User) auth.getPrincipal();
  }

  protected int countRowsInTable(String tableName) {
    return JdbcTestUtils.countRowsInTable(this.jdbcTemplate, tableName);
  }
}
