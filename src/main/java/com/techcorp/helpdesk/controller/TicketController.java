package com.techcorp.helpdesk.controller;

import com.techcorp.helpdesk.dto.TicketDTO;
import com.techcorp.helpdesk.model.Ticket.Statut;
import com.techcorp.helpdesk.service.TicketService;
import com.techcorp.helpdesk.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UtilisateurService utilisateurService;

    // --- Liste de tous les tickets ---
    @GetMapping
    public String listerTickets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Statut statut,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TicketDTO> tickets;

        if (keyword != null && !keyword.isBlank()) {
            tickets = ticketService.rechercherTickets(keyword);
        } else {
            tickets = ticketService.listerTousLesTickets();
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statuts", Statut.values());
        model.addAttribute("currentUser", utilisateurService.getUtilisateurByEmail(userDetails.getUsername()));
        return "tickets/liste";
    }

    // --- Détail d'un ticket ---
    @GetMapping("/{id}")
    public String detailTicket(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("ticket", ticketService.getTicketById(id));
        model.addAttribute("techniciens", utilisateurService.listerTechniciens());
        model.addAttribute("statuts", Statut.values());
        model.addAttribute("currentUser", utilisateurService.getUtilisateurByEmail(userDetails.getUsername()));
        return "tickets/detail";
    }

    // --- Formulaire de création ---
    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("ticketDTO", new TicketDTO());
        model.addAttribute("priorites", com.techcorp.helpdesk.model.Ticket.Priorite.values());
        return "tickets/formulaire";
    }

    // --- Soumettre un nouveau ticket ---
    @PostMapping("/nouveau")
    public String creerTicket(
            @Valid @ModelAttribute("ticketDTO") TicketDTO dto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("priorites", com.techcorp.helpdesk.model.Ticket.Priorite.values());
            return "tickets/formulaire";
        }

        var createur = utilisateurService.getUtilisateurByEmail(userDetails.getUsername());
        ticketService.creerTicket(dto, createur.getId());

        redirectAttributes.addFlashAttribute("succes", "Ticket créé avec succès !");
        return "redirect:/tickets";
    }

    // --- Changer le statut d'un ticket ---
    @PostMapping("/{id}/statut")
    public String changerStatut(
            @PathVariable Long id,
            @RequestParam Statut statut,
            RedirectAttributes redirectAttributes) {

        ticketService.changerStatut(id, statut);
        redirectAttributes.addFlashAttribute("succes", "Statut mis à jour.");
        return "redirect:/tickets/" + id;
    }

    // --- Assigner un technicien ---
    @PostMapping("/{id}/assigner")
    public String assignerTechnicien(
            @PathVariable Long id,
            @RequestParam Long technicienId,
            RedirectAttributes redirectAttributes) {

        ticketService.assignerTechnicien(id, technicienId);
        redirectAttributes.addFlashAttribute("succes", "Technicien assigné.");
        return "redirect:/tickets/" + id;
    }

    // --- Supprimer un ticket (ADMIN seulement) ---
    @PostMapping("/{id}/supprimer")
    public String supprimerTicket(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        ticketService.supprimerTicket(id);
        redirectAttributes.addFlashAttribute("succes", "Ticket supprimé.");
        return "redirect:/tickets";
    }
}