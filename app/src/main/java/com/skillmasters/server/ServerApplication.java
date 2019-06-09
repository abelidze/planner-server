package com.skillmasters.server;

import biweekly.ICalVersion;
import biweekly.io.ParseContext;
import biweekly.io.scribe.property.RecurrenceRuleScribe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
@ComponentScan(basePackages = { "com.skillmasters.server" })
public class ServerApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(ServerApplication.class, args);
  }

  @Bean
  public RecurrenceRuleScribe rruleScribe()
  {
    return new RecurrenceRuleScribe();
  }

  @Bean
  public ParseContext beweeklyContext()
  {
    ParseContext ctx = new ParseContext();
    ctx.setVersion(ICalVersion.V2_0);
    return ctx;
  }
}