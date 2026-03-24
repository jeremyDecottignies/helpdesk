package com.techcorp.helpdesk.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // --- Ressource introuvable (ticket, utilisateur...) ---
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        model.addAttribute("erreur", ex.getMessage());
        return "error/erreur";
    }

    // --- Accès refusé ---
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public String handleAccessDenied(Model model) {
        model.addAttribute("erreur", "Vous n'avez pas les droits pour accéder à cette page.");
        return "error/acces-refuse";
    }
}