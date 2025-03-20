package com.eryxis.eryxis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false)  String msg) {
        model.addAttribute("msg", msg);
        return "index";
    }
}
