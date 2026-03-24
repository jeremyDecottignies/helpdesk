package com.techcorp.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String destinataire;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(nullable = false)
    private Boolean succes;

    @Column(columnDefinition = "TEXT")
    private String messageErreur;

    @PrePersist
    public void prePersist() {
        this.dateEnvoi = LocalDateTime.now();
    }

    public enum Type {
        TICKET_CREE,
        STATUT_CHANGE,
        COMMENTAIRE_AJOUTE,
        TICKET_ASSIGNE,
        TICKET_RESOLU
    }
}