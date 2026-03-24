package com.techcorp.helpdesk.service;

import com.techcorp.helpdesk.dto.TicketDTO;
import com.techcorp.helpdesk.model.Ticket;
import com.techcorp.helpdesk.model.Ticket.Statut;
import com.techcorp.helpdesk.model.Utilisateur;
import com.techcorp.helpdesk.repository.TicketRepository;
import com.techcorp.helpdesk.repository.UtilisateurRepository;
import com.techcorp.helpdesk.repository.CommentaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CommentaireRepository commentaireRepository;
    private final NotificationService notificationService;

    // --- Créer un ticket ---
    @Transactional
    public TicketDTO creerTicket(TicketDTO dto, Long createurId) {
        Utilisateur createur = utilisateurRepository.findById(createurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + createurId));

        Ticket ticket = new Ticket();
        ticket.setTitre(dto.getTitre());
        ticket.setDescription(dto.getDescription());
        ticket.setPriorite(dto.getPriorite());
        ticket.setStatut(Statut.OUVERT);
        ticket.setCreateur(createur);

        Ticket saved = ticketRepository.save(ticket);

        // Notification email au créateur
        notificationService.envoyerNotificationCreation(saved);

        return toDTO(saved);
    }

    // --- Lister tous les tickets ---
    @Transactional(readOnly = true)
    public List<TicketDTO> listerTousLesTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Tickets d'un employé ---
    @Transactional(readOnly = true)
    public List<TicketDTO> listerTicketsParCreateur(Long createurId) {
        Utilisateur createur = utilisateurRepository.findById(createurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + createurId));
        return ticketRepository.findByCreateur(createur)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Tickets assignés à un technicien ---
    @Transactional(readOnly = true)
    public List<TicketDTO> listerTicketsParTechnicien(Long technicienId) {
        Utilisateur technicien = utilisateurRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + technicienId));
        return ticketRepository.findByTechnicien(technicien)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Détail d'un ticket ---
    @Transactional(readOnly = true)
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable : " + id));
        return toDTO(ticket);
    }

    // --- Changer le statut d'un ticket ---
    @Transactional
    public TicketDTO changerStatut(Long ticketId, Statut nouveauStatut) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable : " + ticketId));

        Statut ancienStatut = ticket.getStatut();
        ticket.setStatut(nouveauStatut);
        Ticket saved = ticketRepository.save(ticket);

        // Notification email si statut change
        if (!ancienStatut.equals(nouveauStatut)) {
            notificationService.envoyerNotificationChangementStatut(saved, ancienStatut);
        }

        return toDTO(saved);
    }

    // --- Assigner un technicien ---
    @Transactional
    public TicketDTO assignerTechnicien(Long ticketId, Long technicienId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable : " + ticketId));
        Utilisateur technicien = utilisateurRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien introuvable : " + technicienId));

        ticket.setTechnicien(technicien);
        ticket.setStatut(Statut.EN_COURS);
        Ticket saved = ticketRepository.save(ticket);

        notificationService.envoyerNotificationAssignation(saved);

        return toDTO(saved);
    }

    // --- Recherche par mot-clé ---
    @Transactional(readOnly = true)
    public List<TicketDTO> rechercherTickets(String keyword) {
        return ticketRepository.searchByKeyword(keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Supprimer un ticket ---
    @Transactional
    public void supprimerTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket introuvable : " + id);
        }
        ticketRepository.deleteById(id);
    }

    // --- Conversion Ticket -> TicketDTO ---
    public TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitre(ticket.getTitre());
        dto.setDescription(ticket.getDescription());
        dto.setStatut(ticket.getStatut());
        dto.setPriorite(ticket.getPriorite());
        dto.setDateCreation(ticket.getDateCreation());
        dto.setDateMaj(ticket.getDateMaj());

        if (ticket.getCreateur() != null) {
            dto.setCreateurId(ticket.getCreateur().getId());
            dto.setCreateurNom(ticket.getCreateur().getNom());
            dto.setCreateurEmail(ticket.getCreateur().getEmail());
        }
        if (ticket.getTechnicien() != null) {
            dto.setTechnicienId(ticket.getTechnicien().getId());
            dto.setTechnicienNom(ticket.getTechnicien().getNom());
        }

        dto.setNombreCommentaires((int) commentaireRepository.countByTicket(ticket));

        return dto;
    }
}