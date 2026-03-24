package com.techcorp.helpdesk.repository;

import com.techcorp.helpdesk.model.Utilisateur;
import com.techcorp.helpdesk.model.Utilisateur.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Trouver un utilisateur par email (pour l'authentification)
    Optional<Utilisateur> findByEmail(String email);

    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Tous les utilisateurs d'un rôle donné
    List<Utilisateur> findByRole(Role role);

    // Tous les techniciens disponibles
    List<Utilisateur> findByRoleOrderByNomAsc(Role role);
}