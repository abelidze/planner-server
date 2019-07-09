package com.skillmasters.server.http.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.component.VTodo;
import biweekly.property.Status;
import biweekly.property.DateStart;
import biweekly.parameter.ICalParameters;
import biweekly.util.Frequency;
import biweekly.util.Recurrence;
import biweekly.util.Duration;
import biweekly.io.ParseContext;
import biweekly.io.TimezoneInfo;
import biweekly.io.TimezoneAssignment;
import biweekly.io.scribe.property.RecurrenceRuleScribe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.http.response.ObjectResponse;
import com.skillmasters.server.service.EventPatternService;
import com.skillmasters.server.service.EventService;
import com.skillmasters.server.model.*;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Calendar", description="utils for iCalendar")
public class CalendarController
{
  @Autowired
  EventService eventService;

  @Autowired
  EventPatternService patternService;

  @Autowired
  RecurrenceRuleScribe scribe;

  @Autowired
  ParseContext context;

  @ApiOperation(value = "Import iCal calendar for current user", response = ObjectResponse.class)
  @PostMapping("/import")
  public ObjectResponse calendarImport(@AuthenticationPrincipal User user, @RequestParam MultipartFile file) throws IOException
  {
    // Configure ical
    ICalendar ical = Biweekly.parse(file.getInputStream()).first();

    // Deal with timezones
    TimezoneInfo tzInfo = ical.getTimezoneInfo();

    // Import events and patterns
    Map<String, Event> cache = new HashMap<>();
    for (VEvent e : ical.getEvents()) {
      String uid = e.getUid().toString();

      // Import event's data
      Event event = cache.get(uid);
      if (event == null) {
        event = new Event();
        event.setOwnerId(user.getId());

        if (e.getSummary() != null) {
          event.setName(e.getSummary().getValue());
        }

        if (e.getDescription() != null) {
          event.setDetails(e.getDescription().getValue());
        }

        if (e.getLocation() != null) {
          event.setLocation(e.getLocation().getValue());
        }

        if (e.getCreated() != null) {
          event.setCreatedAt(e.getCreated().getValue());
        }

        if (e.getLastModified() != null) {
          event.setUpdatedAt(e.getLastModified().getValue());
        }

        cache.put(uid, event);
        eventService.save(event);
      }

      // Import pattern's data
      EventPattern pattern = new EventPattern();

      DateStart dstart = e.getDateStart();
      pattern.setStartedAt(dstart.getValue());

      TimeZone tz;
      if (tzInfo.isFloating(dstart)) {
        tz = TimeZone.getDefault();
      } else {
        TimezoneAssignment tza = tzInfo.getTimezone(dstart);
        tz = (tza == null) ? TimeZone.getTimeZone("UTC") : tza.getTimeZone();
      }
      pattern.setTimezone(tz.getID());

      if (e.getDuration() == null) {
        pattern.setDuration(pattern.getEndedAt().getTime() - pattern.getStartedAt().getTime());
      } else {
        pattern.setDuration(e.getDuration().getValue().toMillis());
      }

      if (e.getDateEnd() == null) {
        pattern.setEndedAt(new Date(Long.MAX_VALUE));
      } else {
        pattern.setEndedAt(e.getDateEnd().getValue());
      }

      if (e.getRecurrenceRule() != null) {
        pattern.setRrule(e.getRecurrenceRule().toString());
      }

      pattern.setEvent(event);
      patternService.save(pattern);
    }

    // TODO: import tasks

    return new ObjectResponse().success();
  }

  @ApiOperation(value = "Export current user calendar to iCal / .ics", produces = "text/calendar")
  @GetMapping("/export")
  public String calendarExport(@AuthenticationPrincipal User user, HttpServletResponse response) throws IOException
  {
    // Configure ical
    ICalendar ical = new ICalendar();
    ical.setProductId("-//SkillMasters//PlannerApiServer 1.0//RU");

    // Cache timezone reference
    TimezoneInfo tzInfo = ical.getTimezoneInfo();
    tzInfo.setDefaultTimezone(TimezoneAssignment.download(TimeZone.getTimeZone("UTC"), false));

    // Export Events + Patterns + Tasks
    Iterable<Event> events = eventService.getByQuery( QEvent.event.ownerId.eq(user.getId()) );
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
    response.setContentType("text/calendar; charset=UTF-8");
    response.setContentLength(exported.getBytes("UTF-8").length);
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-Disposition", "attachment;filename=ical.ics");

    // Return ical.ics file...
    // PrintWriter out = response.getWriter();
    // out.println(exported);
    ServletOutputStream out = response.getOutputStream();
    out.write(exported.getBytes("UTF-8")); 
    out.flush();
    out.close();

    // ...and ical's string for ajax users
    return exported;
  }
}