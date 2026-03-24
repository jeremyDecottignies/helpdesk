package com.techcorp.helpdesk.dto;

import com.techcorp.helpdesk.model.Utilisateur.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    // Mot de passe uniquement à la création / modification
    // Jamais renvoyé dans les réponses
    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String motDePasse;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}