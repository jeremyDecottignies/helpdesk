package com.techcorp.helpdesk.dto;

import com.techcorp.helpdesk.model.Ticket.Statut;
import com.techcorp.helpdesk.model.Ticket.Priorite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private Statut statut;

    @NotNull(message = "La priorité est obligatoire")
    private Priorite priorite;

    // Infos createur (on n'expose pas le mot de passe)
    private Long createurId;
    private String createurNom;
    private String createurEmail;

    // Infos technicien assigné
    private Long technicienId;
    private String technicienNom;

    private LocalDateTime dateCreation;
    private LocalDateTime dateMaj;

    private int nombreCommentaires;
}