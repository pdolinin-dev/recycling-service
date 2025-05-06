package com.example.recycling_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index"; // Это имя HTML-файла, который будет отображаться при переходе на корневой URL
    }
}