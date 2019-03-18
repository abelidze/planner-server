package com.skillmasters.server;

import org.springframework.http.MediaType;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

@Configuration
class WebMvcConfiguration implements WebMvcConfigurer
{
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
  {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }
}