package com.skillmasters.server.mock.model;

import com.skillmasters.server.model.Event;
import com.skillmasters.server.model.Task;
import lombok.Data;

@Data
public class TaskMock extends Task
{
  private Long event_id;
}
