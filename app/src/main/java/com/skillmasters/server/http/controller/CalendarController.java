package com.skillmasters.server.http.controller;

import java.util.List;
import java.util.TimeZone;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.component.VTodo;
import biweekly.property.Status;
import biweekly.parameter.ICalParameters;
import biweekly.util.Frequency;
import biweekly.util.Recurrence;
import biweekly.util.Duration;
import biweekly.io.ParseContext;
import biweekly.io.TimezoneInfo;
import biweekly.io.TimezoneAssignment;
import biweekly.io.scribe.property.RecurrenceRuleScribe;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.User;
import com.skillmasters.server.model.Task;
import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.EventPattern;
import com.skillmasters.server.model.QEvent;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Calendar", description="utils for iCalendar")
public class CalendarController
{
  @Autowired
  EventRepository eventRepository;

  @Autowired
  RecurrenceRuleScribe scribe;

  @Autowired
  ParseContext context;

  @ApiOperation(value = "Export current user calendar to iCal / .ics", produces = "text/calendar")
  @GetMapping("/export")
  public String export(@AuthenticationPrincipal User user, HttpServletResponse response) throws IOException
  {
    // Configure ical
    ICalendar ical = new ICalendar();
    ical.setProductId("-//SkillMasters//PlannerApiServer 1.0//RU");

    // Cache timezone reference
    TimezoneInfo tzInfo = ical.getTimezoneInfo();
    tzInfo.setDefaultTimezone(TimezoneAssignment.download(TimeZone.getTimeZone("UTC"), false));

    // Export Events + Patterns + Tasks
    Iterable<Event> events = eventRepository.findAll( QEvent.event.ownerId.eq(user.getId()) );
    for (Event e : events) {
      String uid = e.getId().toString();
      VEvent event = new VEvent();
      event.setUid(uid); // <-- dangerous thing due to `eventCopy`, needed for todos, but...
      event.setSummary(e.getName());
      event.setDescription(e.getDetails());
      event.setLocation(e.getLocation());
      event.setCreated(e.getCreatedAt());
      event.setLastModified(e.getUpdatedAt());

      List<EventPattern> patterns = e.getPatterns();
      List<Task> tasks = e.getTasks();
      boolean hasPatterns = (patterns != null && patterns.size() > 0);
      boolean hasTasks = (tasks != null && tasks.size() > 0);
      if (!hasPatterns && !hasTasks) {
        ical.addEvent(event);
        continue;
      }

      // Export Patterns
      if (hasPatterns) {
        for (EventPattern pattern : patterns) {
          VEvent eventCopy = new VEvent(event);
          eventCopy.setDateStart(pattern.getStartedAt());
          eventCopy.setDateEnd(pattern.getEndedAt());
          eventCopy.setDuration( Duration.fromMillis(pattern.getDuration()) );

          TimeZone tz = TimeZone.getTimeZone(pattern.getTimezone());
          TimezoneAssignment tza = new TimezoneAssignment(tz, tz.getID());
          tzInfo.setTimezone(eventCopy.getDateStart(), tza);
          tzInfo.setTimezone(eventCopy.getDateEnd(), tza);

          String rrule = pattern.getRrule();
          if (rrule != null) {
            eventCopy.setRecurrenceRule( scribe.parseText(rrule, null, new ICalParameters(), context) );
          }
          ical.addEvent(eventCopy);
        }
      }

      // Export Tasks, TODO: contacts and attendee support
      if (hasTasks) {
        for (Task task : tasks) {
          VTodo todo = new VTodo();
          todo.addRelatedTo(uid);
          todo.setOrganizer(user.getUsername());
          todo.setSummary(task.getName());
          todo.setDescription(task.getDetails());
          todo.setLocation(e.getLocation());
          todo.setStatus(new Status(task.getStatus()));
          todo.setDateDue(task.getDeadlineAt());
          todo.setCreated(task.getCreatedAt());
          todo.setLastModified(task.getUpdatedAt());
          ical.addTodo(todo);
        }
      }
    }

    // Generate .ics
    String exported = Biweekly.write(ical).go();
    response.setContentType("text/calendar");
    response.setContentLength(exported.getBytes("UTF-8").length);
    response.setHeader("Content-Disposition", "attachment;filename=ical.ics");

    // Return ical.ics file...
    ServletOutputStream out = response.getOutputStream();
    out.println(exported);
    out.flush();
    out.close();

    // ...and ical's string for ajax users
    return exported;
  }
}