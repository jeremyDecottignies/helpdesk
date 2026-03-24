package com.techcorp.helpdesk.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.techcorp.helpdesk.model.Notification;
import com.techcorp.helpdesk.model.Notification.Type;
import com.techcorp.helpdesk.model.Ticket;
import com.techcorp.helpdesk.model.Ticket.Statut;
import com.techcorp.helpdesk.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SendGrid sendGrid;
    private final NotificationRepository notificationRepository;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name}")
    private String fromName;

    // --- Notification à la création d'un ticket ---
    public void envoyerNotificationCreation(Ticket ticket) {
        String destinataire = ticket.getCreateur().getEmail();
        String sujet = "[Helpdesk] Ticket #" + ticket.getId() + " créé";
        String corps = buildEmailCreation(ticket);
        envoyerEmail(ticket, destinataire, sujet, corps, Type.TICKET_CREE);
    }

    // --- Notification lors d'un changement de statut ---
    public void envoyerNotificationChangementStatut(Ticket ticket, Statut ancienStatut) {
        String destinataire = ticket.getCreateur().getEmail();
        String sujet = "[Helpdesk] Ticket #" + ticket.getId() + " — statut mis à jour";
        String corps = buildEmailChangementStatut(ticket, ancienStatut);
        envoyerEmail(ticket, destinataire, sujet, corps, Type.STATUT_CHANGE);
    }

    // --- Notification lors de l'assignation d'un technicien ---
    public void envoyerNotificationAssignation(Ticket ticket) {
        String destinataire = ticket.getTechnicien().getEmail();
        String sujet = "[Helpdesk] Ticket #" + ticket.getId() + " vous a été assigné";
        String corps = buildEmailAssignation(ticket);
        envoyerEmail(ticket, destinataire, sujet, corps, Type.TICKET_ASSIGNE);
    }

    // --- Notification à la résolution ---
    public void envoyerNotificationResolution(Ticket ticket) {
        String destinataire = ticket.getCreateur().getEmail();
        String sujet = "[Helpdesk] Ticket #" + ticket.getId() + " résolu";
        String corps = buildEmailResolution(ticket);
        envoyerEmail(ticket, destinataire, sujet, corps, Type.TICKET_RESOLU);
    }

    // --- Envoi via l'API SendGrid ---
    private void envoyerEmail(Ticket ticket, String destinataire,
                              String sujet, String corps, Type type) {
        Notification notification = new Notification();
        notification.setTicket(ticket);
        notification.setType(type);
        notification.setDestinataire(destinataire);

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(destinataire);
            Content content = new Content("text/html", corps);
            Mail mail = new Mail(from, sujet, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            boolean succes = response.getStatusCode() >= 200 && response.getStatusCode() < 300;
            notification.setSucces(succes);

            if (!succes) {
                notification.setMessageErreur("Code HTTP : " + response.getStatusCode()
                        + " — " + response.getBody());
                log.error("Echec envoi email SendGrid : {}", response.getBody());
            } else {
                log.info("Email envoyé à {} pour ticket #{}", destinataire, ticket.getId());
            }

        } catch (IOException e) {
            notification.setSucces(false);
            notification.setMessageErreur(e.getMessage());
            log.error("Erreur SendGrid : {}", e.getMessage());
        }

        notificationRepository.save(notification);
    }

    // --- Templates HTML des emails ---

    private String buildEmailCreation(Ticket ticket) {
        return "<h2>Votre ticket a été créé</h2>"
                + "<p>Bonjour <strong>" + ticket.getCreateur().getNom() + "</strong>,</p>"
                + "<p>Votre ticket <strong>#" + ticket.getId() + " — " + ticket.getTitre() + "</strong> "
                + "a bien été enregistré.</p>"
                + "<ul>"
                + "<li>Priorité : <strong>" + ticket.getPriorite() + "</strong></li>"
                + "<li>Statut : <strong>" + ticket.getStatut() + "</strong></li>"
                + "</ul>"
                + "<p>Notre équipe technique va prendre en charge votre demande.</p>"
                + "<br><p>— Helpdesk TechCorp</p>";
    }

    private String buildEmailChangementStatut(Ticket ticket, Statut ancienStatut) {
        return "<h2>Statut de votre ticket mis à jour</h2>"
                + "<p>Bonjour <strong>" + ticket.getCreateur().getNom() + "</strong>,</p>"
                + "<p>Le statut du ticket <strong>#" + ticket.getId() + " — " + ticket.getTitre() + "</strong> "
                + "a changé.</p>"
                + "<ul>"
                + "<li>Ancien statut : <strong>" + ancienStatut + "</strong></li>"
                + "<li>Nouveau statut : <strong>" + ticket.getStatut() + "</strong></li>"
                + "</ul>"
                + "<br><p>— Helpdesk TechCorp</p>";
    }

    private String buildEmailAssignation(Ticket ticket) {
        return "<h2>Un ticket vous a été assigné</h2>"
                + "<p>Bonjour <strong>" + ticket.getTechnicien().getNom() + "</strong>,</p>"
                + "<p>Le ticket <strong>#" + ticket.getId() + " — " + ticket.getTitre() + "</strong> "
                + "vient de vous être assigné.</p>"
                + "<ul>"
                + "<li>Priorité : <strong>" + ticket.getPriorite() + "</strong></li>"
                + "<li>Description : " + ticket.getDescription() + "</li>"
                + "</ul>"
                + "<p>Merci de le traiter dans les meilleurs délais.</p>"
                + "<br><p>— Helpdesk TechCorp</p>";
    }

    private String buildEmailResolution(Ticket ticket) {
        return "<h2>Votre ticket a été résolu</h2>"
                + "<p>Bonjour <strong>" + ticket.getCreateur().getNom() + "</strong>,</p>"
                + "<p>Votre ticket <strong>#" + ticket.getId() + " — " + ticket.getTitre() + "</strong> "
                + "a été marqué comme résolu.</p>"
                + "<p>Si le problème persiste, n'hésitez pas à ouvrir un nouveau ticket.</p>"
                + "<br><p>— Helpdesk TechCorp</p>";
    }
}