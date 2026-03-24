package com.techcorp.helpdesk.controller;

import com.techcorp.helpdesk.dto.CommentaireDTO;
import com.techcorp.helpdesk.model.Commentaire;
import com.techcorp.helpdesk.model.Ticket;
import com.techcorp.helpdesk.model.Utilisateur;
import com.techcorp.helpdesk.repository.CommentaireRepository;
import com.techcorp.helpdesk.repository.TicketRepository;
import com.techcorp.helpdesk.repository.UtilisateurRepository;
import com.techcorp.helpdesk.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tickets/{ticketId}/commentaires")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireRepository commentaireRepository;
    private final TicketRepository ticketRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;

    // --- Ajouter un commentaire sur un ticket ---
    @PostMapping
    public String ajouterCommentaire(
            @PathVariable Long ticketId,
            @Valid @ModelAttribute CommentaireDTO dto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erreur", "Le commentaire ne peut pas être vide.");
            return "redirect:/tickets/" + ticketId;
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable : " + ticketId));

        Utilisateur auteur = utilisateurRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(dto.getContenu());
        commentaire.setTicket(ticket);
        commentaire.setAuteur(auteur);

        commentaireRepository.save(commentaire);

        redirectAttributes.addFlashAttribute("succes", "Commentaire ajouté.");
        return "redirect:/tickets/" + ticketId;
    }
}