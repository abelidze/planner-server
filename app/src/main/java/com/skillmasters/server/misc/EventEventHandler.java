// package com.skillmasters.server.misc;

// import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
// import org.springframework.stereotype.Component;
// import org.springframework.data.rest.core.annotation.*;
// import com.skillmasters.server.model.Event;

// public class EventEventHandler extends AbstractRepositoryEventListener
// {
//   @Override
//   public void onBeforeSave(Object entity)
//   {
//     System.out.println("SAVE");
//   }

//   @Override
//   public void onAfterDelete(Object entity)
//   {
//     System.out.println("DELETE");
//   }
// }

// @Component
// @RepositoryEventHandler
// public class EventEventHandler
// { 
//   @HandleBeforeCreate
//   public void handleEventBeforeCreate(Event event)
//   {
//     System.out.println("CREATE " + event.getName());
//   }

//   @HandleBeforeSave
//   public void handleEventBeforeSave(Event event)
//   {
//     System.out.println("SAVE " + event.getName());
//   }

//   @HandleBeforeDelete
//   public void handleEventBeforeDelete(Event event)
//   {
//     System.out.println("DELETE " + event.getName());
//   }
// }