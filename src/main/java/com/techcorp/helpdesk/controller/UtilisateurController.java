package com.techcorp.helpdesk.controller;

import com.techcorp.helpdesk.dto.UtilisateurDTO;
import com.techcorp.helpdesk.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // --- Liste des utilisateurs ---
    @GetMapping
    public String listerUtilisateurs(Model model) {
        model.addAttribute("utilisateurs", utilisateurService.listerUtilisateurs());
        return "utilisateurs/liste";
    }

    // --- Formulaire de création ---
    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("utilisateurDTO", new UtilisateurDTO());
        model.addAttribute("roles", com.techcorp.helpdesk.model.Utilisateur.Role.values());
        return "utilisateurs/formulaire";
    }

    // --- Créer un utilisateur ---
    @PostMapping("/nouveau")
    public String creerUtilisateur(
            @Valid @ModelAttribute("utilisateurDTO") UtilisateurDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", com.techcorp.helpdesk.model.Utilisateur.Role.values());
            return "utilisateurs/formulaire";
        }

        try {
            utilisateurService.creerUtilisateur(dto);
            redirectAttributes.addFlashAttribute("succes", "Utilisateur créé avec succès !");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/utilisateurs";
    }
}