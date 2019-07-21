package com.skillmasters.server.service;

import org.springframework.stereotype.Service;
import com.skillmasters.server.repository.TaskRepository;
import com.skillmasters.server.model.Task;

@Service
public class TaskService extends EntityService<TaskRepository, Task, Long>
{
  public TaskService()
  {
    super(Task.class, "TASK");
  }
}