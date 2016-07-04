package me.wonwoo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wonwoo on 2016. 7. 4..
 */
@RestController
public class AnonymousController {

  @GetMapping("/anonymous")
  public String simple(){
    return "Anonymous";
  }
}
