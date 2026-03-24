package com.techcorp.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private Utilisateur createur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id")
    private Utilisateur technicien;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column
    private LocalDateTime dateMaj;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.dateMaj = LocalDateTime.now();
        if (this.statut == null) this.statut = Statut.OUVERT;
    }

    @PreUpdate
    public void preUpdate() {
        this.dateMaj = LocalDateTime.now();
    }

    public enum Statut {
        OUVERT,
        EN_COURS,
        EN_ATTENTE,
        RESOLU,
        FERME
    }

    public enum Priorite {
        FAIBLE,
        NORMALE,
        HAUTE,
        URGENTE
    }
}