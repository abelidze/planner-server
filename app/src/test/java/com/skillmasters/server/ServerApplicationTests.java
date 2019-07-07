 package com.skillmasters.server;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.springframework.test.annotation.DirtiesContext.ClassMode.*;

 import com.querydsl.core.types.dsl.BooleanExpression;
 import com.querydsl.jpa.impl.JPAQuery;
 import com.skillmasters.server.http.controller.EventController;
 import com.skillmasters.server.model.Event;
 import com.skillmasters.server.model.QEvent;
 import com.skillmasters.server.service.EventService;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.test.annotation.DirtiesContext;
 import org.springframework.test.annotation.DirtiesContext.ClassMode;
 import org.springframework.test.context.junit4.SpringRunner;
 import org.springframework.beans.factory.annotation.Autowired;
 import com.skillmasters.server.http.controller.HomeController;
 import org.springframework.test.util.ReflectionTestUtils;
 import org.springframework.transaction.annotation.Transactional;

 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContext;

 @RunWith(SpringRunner.class)
 @SpringBootTest
 @Transactional
 public class ServerApplicationTests
 {
   @Autowired
   private HomeController controller;

   @Autowired
   private EventController eventController;

   @Autowired
   private EventService eventService;

   @PersistenceContext
   EntityManager entityManager;

   @Test
   public void dummyTest() throws Exception
   {
     assertThat(controller).isNotNull();
   }

   @Test
   public void testConnection()
   {
     QEvent qEvent = QEvent.event;
     JPAQuery query = new JPAQuery(entityManager);
     query.from(qEvent);
     BooleanExpression where = qEvent.isNotNull();
     assertThat(eventService.count(where)).isEqualTo(0);
     Event e = new Event();

     eventService.save(e);
     assertThat(eventService.count(where)).isEqualTo(1);
   }

   @Test
   public void testConnection2()
   {
     QEvent qEvent = QEvent.event;
     JPAQuery query = new JPAQuery(entityManager);
     query.from(qEvent);
     BooleanExpression where = qEvent.isNotNull();
     assertThat(eventService.count(where)).isEqualTo(0);
     Event e = new Event();

     eventService.save(e);
     assertThat(eventService.count(where)).isEqualTo(1);
   }
 }