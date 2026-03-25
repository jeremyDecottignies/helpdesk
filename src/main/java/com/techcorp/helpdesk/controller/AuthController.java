package com.techcorp.helpdesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String erreur,
            @RequestParam(required = false) String deconnecte,
            Model model) {

        if (erreur != null) {
            model.addAttribute("erreur", "Email ou mot de passe incorrect.");
        }
        if (deconnecte != null) {
            model.addAttribute("info", "Vous avez été déconnecté.");
        }
        return "login";
    }
}