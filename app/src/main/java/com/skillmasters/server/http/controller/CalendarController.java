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
import biweekly.ICalVersion;
import biweekly.component.VEvent;
import biweekly.component.VTodo;
import biweekly.property.Status;
import biweekly.property.DateStart;
import biweekly.property.RelatedTo;
import biweekly.parameter.ICalParameters;
import biweekly.util.Frequency;
import biweekly.util.Recurrence;
import biweekly.util.Duration;
import biweekly.io.ParseContext;
import biweekly.io.WriteContext;
import biweekly.io.TimezoneInfo;
import biweekly.io.TimezoneAssignment;
import biweekly.io.scribe.property.RecurrenceRuleScribe;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.misc.BadRequestException;
import com.skillmasters.server.http.response.ObjectResponse;
import com.skillmasters.server.service.EventPatternService;
import com.skillmasters.server.service.EventService;
import com.skillmasters.server.service.TaskService;
import com.skillmasters.server.model.*;

@RestController
@RequestMapping("/api/v1")
@Api(tags="Calendar", description="utils for iCalendar")
public class CalendarController
{
  @Autowired
  EventService eventService;

  @Autowired
  TaskService taskService;

  @Autowired
  EventPatternService patternService;

  @Autowired
  RecurrenceRuleScribe scribe;

  @Autowired
  ParseContext context;

  @ApiOperation(value = "Import iCal calendar for current user from string", response = ObjectResponse.class)
  @PostMapping("/import/raw")
  public ObjectResponse importFromString(@AuthenticationPrincipal User user, @RequestBody String str) throws IOException
  {
    ICalendar ical = Biweekly.parse(str).first();
    calendarImport(user, ical);
    return new ObjectResponse().success();
  }

  @ApiOperation(value = "Import iCal calendar for current user from file", response = ObjectResponse.class)
  @PostMapping("/import")
  public ObjectResponse importFromFile(@AuthenticationPrincipal User user, @RequestParam MultipartFile file) throws IOException
  {
    ICalendar ical = Biweekly.parse(file.getInputStream()).first();
    calendarImport(user, ical);
    return new ObjectResponse().success();
  }

  public void calendarImport(User user, ICalendar ical)
  {
    if (ical == null) {
      throw new BadRequestException("Invalid iCalendar data, can't import");
    }

    // Deal with timezones
    TimezoneInfo tzInfo = ical.getTimezoneInfo();
    WriteContext ctx = new WriteContext(ICalVersion.V2_0, tzInfo, tzInfo.getDefaultTimezone());

    // Import Events and Patterns
    Map<String, Event> cache = new HashMap<>();
    for (VEvent e : ical.getEvents()) {
      String uid = e.getUid().getValue();

      // Import event's data
      Event event = cache.get(uid);
      if (event == null) {
        event = new Event();
        event.setOwnerId(user.getId());

        if (e.getSummary() != null) {
          event.setName(e.getSummary().getValue());
        }

        if (e.getStatus() != null) {
          event.setStatus(e.getStatus().getValue());
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
      if (dstart == null) {
        continue;
      }
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
        pattern.setEndedAt(new Date(EventPattern.MAX_TIME));
      } else {
        pattern.setEndedAt(e.getDateEnd().getValue());
      }

      if (e.getRecurrenceRule() != null) {
        pattern.setRrule(scribe.writeText(e.getRecurrenceRule(), ctx));
      }

      pattern.setEvent(event);
      patternService.save(pattern);
    }

    // Import tasks
    for (VTodo todo : ical.getTodos()) {
      Task task = new Task();

      // Attach task to event
      List<RelatedTo> related = todo.getRelatedTo();
      if (related != null && !related.isEmpty()) {
        String uid = related.get(0).getValue();
        if (cache.containsKey(uid)) {
          task.setEvent(cache.get(uid));
        }
      }

      // todo.getOrganizer()
      // todo.getLocation()

      if (todo.getSummary() != null) {
        task.setName(todo.getSummary().getValue());
      }

      if (todo.getDescription() != null) {
        task.setDetails(todo.getDescription().getValue());
      }

      if (todo.getStatus() != null) {
        task.setStatus(todo.getStatus().getValue());
      }

      if (todo.getDateDue() != null) {
        task.setDeadlineAt(todo.getDateDue().getValue());
      }

      if (todo.getCreated() != null) {
        task.setCreatedAt(todo.getCreated().getValue());
      }

      if (todo.getLastModified() != null) {
        task.setUpdatedAt(todo.getLastModified().getValue());
      }

      taskService.save(task);
    }
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

      if (!Strings.isNullOrEmpty(e.getName())) {
        event.setSummary(e.getName());
      }

      if (!Strings.isNullOrEmpty(e.getDetails())) {
        event.setDescription(e.getDetails());
      }

      Status eventStatus = new Status(e.getStatus());
      if (!Strings.isNullOrEmpty(eventStatus.getValue())) {
        event.setStatus(eventStatus);
      }

      if (!Strings.isNullOrEmpty(e.getLocation())) {
        event.setLocation(e.getLocation());
      }

      if (e.getCreatedAt() != null) {
        event.setCreated(e.getCreatedAt());
      }

      if (e.getUpdatedAt() != null) {
        event.setLastModified(e.getUpdatedAt());
      }

      // Export Patterns
      List<EventPattern> patterns = e.getPatterns();
      if (patterns != null && patterns.size() > 0) {
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
          if (!Strings.isNullOrEmpty(rrule)) {
            eventCopy.setRecurrenceRule( scribe.parseText(rrule, null, new ICalParameters(), context) );
          }
          ical.addEvent(eventCopy);
        }
      } else {
        ical.addEvent(event);
      }

      // Export Tasks, TODO: contacts and attendee support
      List<Task> tasks = e.getTasks();
      if (tasks != null && tasks.size() > 0) {
        for (Task task : tasks) {
          VTodo todo = new VTodo();
          todo.addRelatedTo(uid);
          todo.setOrganizer(user.getUsername());

          if (!Strings.isNullOrEmpty(task.getName())) {
            todo.setSummary(task.getName());
          }

          if (!Strings.isNullOrEmpty(task.getDetails())) {
            todo.setDescription(task.getDetails());
          }

          if (!Strings.isNullOrEmpty(e.getLocation())) {
            todo.setLocation(e.getLocation());
          }

          Status taskStatus = new Status(task.getStatus());
          if (!Strings.isNullOrEmpty(taskStatus.getValue())) {
            todo.setStatus(taskStatus);
          }

          if (task.getDeadlineAt() != null) {
            todo.setDateDue(task.getDeadlineAt());
          }

          if (task.getCreatedAt() != null) {
            todo.setCreated(task.getCreatedAt());
          }

          if (task.getUpdatedAt() != null) {
            todo.setLastModified(task.getUpdatedAt());
          }

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