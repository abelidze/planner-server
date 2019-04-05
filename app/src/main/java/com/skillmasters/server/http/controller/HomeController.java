package com.skillmasters.server.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController
{
  @RequestMapping("/")
  public String home()
  {
    return "redirect:/swagger-ui.html";
  }

  @RequestMapping("/docs")
  public String swagger()
  {
    return "redirect:/swagger-ui.html";
  }
}
