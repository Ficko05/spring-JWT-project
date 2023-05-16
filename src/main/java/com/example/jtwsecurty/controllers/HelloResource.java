package com.example.jtwsecurty.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloResource {

    //Request mapping for the home page
    @RequestMapping("/")
    public String home() {
        return "home";

    }
}