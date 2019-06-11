package com.skillmasters.server.http.controller;

import java.util.List;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.parameter.ICalParameters;
import biweekly.util.Frequency;
import biweekly.util.Recurrence;
import biweekly.util.Duration;
import biweekly.io.ParseContext;
import biweekly.io.scribe.property.RecurrenceRuleScribe;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.skillmasters.server.repository.EventRepository;
import com.skillmasters.server.model.User;
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
    Iterable<Event> events = eventRepository.findAll( QEvent.event.ownerId.eq(user.getId()) );

    ICalendar ical = new ICalendar();
    for (Event e : events) {
      VEvent event = new VEvent();
      // event.setUid(e.getId().toString());
      event.setSummary(e.getName());
      event.setDescription(e.getDetails());
      event.setLocation(e.getLocation());
      event.setCreated(e.getCreatedAt());
      event.setLastModified(e.getUpdatedAt());

      List<EventPattern> patterns = e.getPatterns();
      if (patterns == null || patterns.size() == 0) {
        ical.addEvent(event);
        continue;
      }

      for (EventPattern pattern : patterns) {
        VEvent eventCopy = new VEvent(event);
        eventCopy.setDateStart(pattern.getStartedAt());
        eventCopy.setDateEnd(pattern.getEndedAt());
        eventCopy.setDuration( Duration.fromMillis(pattern.getDuration()) );

        String rrule = pattern.getRrule();
        if (rrule != null) {
          eventCopy.setRecurrenceRule( scribe.parseText(rrule, null, new ICalParameters(), context) );
        }
        ical.addEvent(eventCopy);
      }
    }

    String exported = Biweekly.write(ical).go();
    response.setContentType("text/calendar");
    response.setContentLength(exported.getBytes("UTF-8").length);
    response.setHeader("Content-Disposition", "attachment;filename=ical.ics");

    ServletOutputStream out = response.getOutputStream();
    out.println(exported);
    out.flush();
    out.close();

    return exported;
  }
}