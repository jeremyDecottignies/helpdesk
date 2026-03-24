package com.techcorp.helpdesk.repository;

import com.techcorp.helpdesk.model.Commentaire;
import com.techcorp.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    // Tous les commentaires d'un ticket, triés par date croissante
    List<Commentaire> findByTicketOrderByDateAsc(Ticket ticket);

    // Nombre de commentaires sur un ticket
    long countByTicket(Ticket ticket);
}