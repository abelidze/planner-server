package com.skillmasters.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.skillmasters.server.model.Task;

@Repository
public class TaskRepository
{
  private List<Task> tasks = new ArrayList<Task>();
  
  public Task add(Task task)
  {
    task.setId((long) (tasks.size()+1));
    tasks.add(task);
    return task;
  }
  
  public Task update(Task task)
  {
    tasks.set(task.getId().intValue() - 1, task);
    return task;
  }
  
  public Task update(Long id, Task task)
  {
    tasks.set(id.intValue() - 1, task);
    return task;
  }
  
  public Task findById(Long id)
  {
    Optional<Task> task = tasks.stream().filter(a -> a.getId().equals(id)).findFirst();
    if (task.isPresent()) {
      return task.get();
    } else {
      return null;
    }
  }
  
  public void delete(Long id)
  {
    tasks.remove(id.intValue());
  }
}