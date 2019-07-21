package com.skillmasters.server.service;

import com.skillmasters.server.common.EventGenerator;
import com.skillmasters.server.common.EventPatternGenerator;
import com.skillmasters.server.http.middleware.security.SimpleAuthenticationToken;
import com.skillmasters.server.model.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

//@AutoConfigureMockMvc
public class ServiceTests {
  @PersistenceContext
  EntityManager entityManager;

//  @Autowired
//  protected MockMvc mockMvc;

  @Autowired
  protected EventService eventService;

  @Autowired
  protected TaskService taskService;

  @Autowired
  protected EventPatternService eventPatternService;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  @Qualifier("authManager")
  protected AuthenticationManager manager;

  @Autowired
  protected PermissionService permissionService;

  protected User testUser;

  protected static String eventsTablename = "events";

  protected static String eventPatternsTablename = "patterns";

  protected static String taskTablename = "tasks";

  protected static String permissionsTablename = "permissions";

  protected  QEvent qEvent = QEvent.event;

  protected QEventPattern qEventPattern = QEventPattern.eventPattern;

  protected QTask qTask= QTask.task;

  protected EventPatternGenerator epg = new EventPatternGenerator();

  @PostConstruct
  private void init()
  {
    authUser("tester");
  }

  protected void authUser(String token) {
    Authentication auth = manager.authenticate(new SimpleAuthenticationToken(token));
    SecurityContextHolder.getContext().setAuthentication(auth);
    testUser = (User) auth.getPrincipal();
  }

  protected void authOwningUser()
  {
    authUser("tester");
  }

  protected void authGrantedUser()
  {
    authUser("tester1");
  }

  protected void authNotGrantedUser()
  {
    authUser("tester2");
  }

  protected int countRowsInTable(String tableName) {
    return JdbcTestUtils.countRowsInTable(this.jdbcTemplate, tableName);
  }

  protected void flushAll()
  {
    eventService.getRepository().flush();
    eventPatternService.getRepository().flush();
    taskService.getRepository().flush();
  }

  protected ArrayList<Event> populateWithEvents()
  {
    ArrayList<Event> events = new ArrayList<>(10);
    for (int i = 0; i < 10; i++) {
      Event e = EventGenerator.genEventWithOwner(i, testUser.getId());
      e = eventService.save(e);

      Task t = new Task();
      t.setEvent(e);
      t = taskService.save(t);

      EventPattern ep = epg.genEventPattern();
      ep.setEvent(e);
      ep = eventPatternService.save(ep);

      // hack because of bug
      Map<String, Object> updates = new HashMap<>();
      updates.put("patterns", Arrays.asList(ep));
      updates.put("tasks", Arrays.asList(t));
//      e.setPatterns(Arrays.asList(ep));
//      e.setTasks(Arrays.asList(t));
      e = eventService.update(e, updates);
//      assertThat(e.getPatterns()).isNotNull();
//      assertThat(e.getTasks()).isNotNull();
      events.add(e);
    }
    flushAll();

    return events;
  }

}
