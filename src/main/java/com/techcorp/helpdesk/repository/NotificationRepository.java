package com.techcorp.helpdesk.repository;

import com.techcorp.helpdesk.model.Notification;
import com.techcorp.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Toutes les notifications d'un ticket
    List<Notification> findByTicketOrderByDateEnvoiDesc(Ticket ticket);

    // Notifications en échec (pour debug / relance)
    List<Notification> findBySuccesFalse();
}