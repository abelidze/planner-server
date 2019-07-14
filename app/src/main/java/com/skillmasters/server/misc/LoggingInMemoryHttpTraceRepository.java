// package com.skillmasters.server.misc;

// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.actuate.trace.http.HttpTrace;
// import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
// import org.springframework.stereotype.Repository;

// @Slf4j
// @Repository
// public class LoggingInMemoryHttpTraceRepository extends InMemoryHttpTraceRepository
// {
//   public void add(HttpTrace trace)
//   {
//     super.add(trace);
//     log.info("Request:" + trace.getRequest().toString());
//     log.info("Response:" + trace.getResponse().toString());
//   }
// }