package com.techcorp.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireDTO {

    private Long id;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private Long ticketId;

    private Long auteurId;
    private String auteurNom;

    private LocalDateTime date;
}