package com.techcorp.helpdesk.service;

import com.techcorp.helpdesk.dto.UtilisateurDTO;
import com.techcorp.helpdesk.model.Utilisateur;
import com.techcorp.helpdesk.model.Utilisateur.Role;
import com.techcorp.helpdesk.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    // --- Créer un utilisateur ---
    @Transactional
    public UtilisateurDTO creerUtilisateur(UtilisateurDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + dto.getEmail());
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(dto.getNom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        utilisateur.setRole(dto.getRole());

        return toDTO(utilisateurRepository.save(utilisateur));
    }

    // --- Lister tous les utilisateurs ---
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> listerUtilisateurs() {
        return utilisateurRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Lister les techniciens ---
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> listerTechniciens() {
        return utilisateurRepository.findByRoleOrderByNomAsc(Role.TECHNICIEN)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Trouver par ID ---
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurById(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id));
        return toDTO(u);
    }

    // --- Trouver par email ---
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurByEmail(String email) {
        Utilisateur u = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));
        return toDTO(u);
    }

    // --- Requis par Spring Security pour l'authentification ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur u = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));

        return User.builder()
                .username(u.getEmail())
                .password(u.getMotDePasse())
                .roles(u.getRole().name())
                .build();
    }

    // --- Conversion Utilisateur -> UtilisateurDTO (sans mot de passe) ---
    public UtilisateurDTO toDTO(Utilisateur u) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(u.getId());
        dto.setNom(u.getNom());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
        // motDePasse volontairement non mappé
        return dto;
    }
}