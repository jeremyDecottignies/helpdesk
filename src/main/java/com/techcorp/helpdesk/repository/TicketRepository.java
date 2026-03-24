package com.techcorp.helpdesk.repository;

import com.techcorp.helpdesk.model.Ticket;
import com.techcorp.helpdesk.model.Ticket.Statut;
import com.techcorp.helpdesk.model.Ticket.Priorite;
import com.techcorp.helpdesk.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Tous les tickets d'un employé
    List<Ticket> findByCreateur(Utilisateur createur);

    // Tous les tickets assignés à un technicien
    List<Ticket> findByTechnicien(Utilisateur technicien);

    // Tickets par statut
    List<Ticket> findByStatut(Statut statut);

    // Tickets par priorité
    List<Ticket> findByPriorite(Priorite priorite);

    // Tickets non assignés (technicien null)
    List<Ticket> findByTechnicienIsNull();

    // Tickets ouverts par priorité décroissante
    List<Ticket> findByStatutOrderByPrioriteDesc(Statut statut);

    // Recherche par mot-clé dans le titre ou la description
    @Query("SELECT t FROM Ticket t WHERE LOWER(t.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Ticket> searchByKeyword(String keyword);

    // Compte les tickets par statut (pour le tableau de bord)
    long countByStatut(Statut statut);
}