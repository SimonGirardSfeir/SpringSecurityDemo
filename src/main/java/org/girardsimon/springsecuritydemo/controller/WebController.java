package org.girardsimon.springsecuritydemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/")
    public String publicPage() {
        return "Hello Demo!";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "Hello Private! 🐯";
    }
}
