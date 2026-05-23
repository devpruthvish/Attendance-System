package com.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for public pages: home, login.
 */
@Controller
public class HomeController {

    /**
     * Root URL redirects to login page.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    /**
     * Show the login form.
     * Thymeleaf template: templates/auth/login.html
     */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}
