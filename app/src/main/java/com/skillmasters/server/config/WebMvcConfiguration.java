package com.skillmasters.server;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
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

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
  {
    ObjectMapper objectMapper = null;
    for (HttpMessageConverter converter : converters) {
      if (converter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter jacksonConverter = (MappingJackson2HttpMessageConverter) converter;

        if (objectMapper == null) {
          objectMapper = jacksonConverter.getObjectMapper();
        } else {
          jacksonConverter.setObjectMapper(objectMapper);
        }
      }
    }
  }
}