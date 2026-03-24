package com.techcorp.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commentaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Utilisateur auteur;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @PrePersist
    public void prePersist() {
        this.date = LocalDateTime.now();
    }
}