package com.techcorp.helpdesk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    private List<Ticket> ticketsCrees;

    @OneToMany(mappedBy = "technicien", fetch = FetchType.LAZY)
    private List<Ticket> ticketsAssignes;

    public enum Role {
        EMPLOYE,
        TECHNICIEN,
        ADMIN
    }
}