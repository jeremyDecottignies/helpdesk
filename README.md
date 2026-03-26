# Helpdesk IT — TechCorp

Application web de gestion de tickets IT développée avec Spring Boot dans le cadre du BTS SIO — Épreuve E6.

---

## Contexte métier

TechCorp est une entreprise fictive d'une cinquantaine de collaborateurs. Le service informatique reçoit des demandes d'assistance sans outil centralisé. Ce projet apporte une solution complète de ticketing avec suivi, notifications email et gestion des rôles.

---

## Stack technologique

| Couche | Technologie |
|---|---|
| Backend | Spring Boot 3.2 |
| Persistance | Spring Data JPA |
| Base de données | PostgreSQL 16 |
| Sécurité | Spring Security + BCrypt |
| Frontend | Thymeleaf |
| Email | SendGrid API |
| Tests | JUnit 5 + Mockito + MockMvc |
| Build | Maven |

---

## Architecture

```
src/main/java/com/techcorp/helpdesk/
├── config/         SecurityConfig, PasswordEncoderConfig, SendGridConfig
├── controller/     TicketController, UtilisateurController, AuthController
├── service/        TicketService, UtilisateurService, NotificationService
├── repository/     TicketRepository, UtilisateurRepository, ...
├── model/          Ticket, Utilisateur, Commentaire, Notification
├── dto/            TicketDTO, UtilisateurDTO, CommentaireDTO
└── exception/      GlobalExceptionHandler

src/main/resources/
├── application.properties
└── templates/
    ├── login.html
    └── tickets/
        ├── liste.html
        ├── detail.html
        └── formulaire.html

src/test/java/com/techcorp/helpdesk/
├── service/        TicketServiceTest
└── controller/     TicketControllerTest
```

---

## Modèle de données

```
utilisateurs    tickets             commentaires    notifications
────────────    ───────────────     ────────────    ─────────────
id              id                  id              id
nom             titre               contenu         ticket_id (FK)
email           description         ticket_id (FK)  type
mot_de_passe    statut              auteur_id (FK)  destinataire
role            priorite            date            date_envoi
                createur_id (FK)                    succes
                technicien_id (FK)
                date_creation
                date_maj
```

### Cycle de vie d'un ticket

```
OUVERT ──► EN_COURS ──► RESOLU ──► FERME
               │
               ▼
           EN_ATTENTE
               │
               └──► EN_COURS
```

---

## Installation

### Prérequis

- Java 17+
- Maven 3.8+
- PostgreSQL 16
- Compte SendGrid (clé API gratuite sur sendgrid.com)

### 1. Créer la base de données

Dans DBeaver ou psql :

```sql
CREATE DATABASE helpdesk_db WITH OWNER = postgres ENCODING = 'UTF8';
```

### 2. Configurer application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk_db
spring.datasource.username=postgres
spring.datasource.password=VOTRE_MOT_DE_PASSE

sendgrid.api-key=SG.VOTRE_CLE_API
sendgrid.from-email=noreply@techcorp.com
sendgrid.from-name=Helpdesk TechCorp
```

### 3. Lancer l'application

Dans IntelliJ IDEA : **Shift + F10**

Ou en ligne de commande :

```bash
mvn spring-boot:run
```

### 4. Insérer le jeu d'essai

Exécuter le fichier `jeu_essai.sql` dans DBeaver sur la base `helpdesk_db`.

### 5. Ouvrir l'application

```
http://localhost:8080
```

---

## Comptes de test

| Email | Mot de passe | Rôle |
|---|---|---|
| admin@techcorp.com | admin123 | ADMIN |
| jean.dupont@techcorp.com | admin123 | TECHNICIEN |
| sophie.lambert@techcorp.com | admin123 | TECHNICIEN |
| marie.martin@techcorp.com | admin123 | EMPLOYE |
| pierre.bernard@techcorp.com | admin123 | EMPLOYE |

---

## Fonctionnalités

### Employé
- Créer un ticket (titre, description, priorité)
- Suivre l'avancement de ses tickets
- Ajouter des commentaires
- Recevoir des notifications email à chaque changement de statut

### Technicien
- Voir tous les tickets
- Changer le statut d'un ticket
- S'assigner un ticket
- Ajouter des commentaires

### Administrateur
- Tout ce que le technicien peut faire
- Gérer les utilisateurs (créer des comptes)
- Supprimer des tickets

---

## Sécurité

- Authentification par email/mot de passe via Spring Security
- Mots de passe hachés avec BCrypt (jamais stockés en clair)
- Accès aux URLs protégé par rôle :
  - `/utilisateurs/**` → ADMIN uniquement
  - `/tickets/*/supprimer` → ADMIN uniquement
  - `/tickets/*/assigner` → TECHNICIEN + ADMIN
  - Reste → tout utilisateur connecté

---

## Service web externe — SendGrid

L'application envoie des emails automatiques via l'API SendGrid dans 4 cas :

| Événement | Destinataire |
|---|---|
| Ticket créé | Employé créateur |
| Statut changé | Employé créateur |
| Technicien assigné | Technicien assigné |
| Ticket résolu | Employé créateur |

Chaque envoi est tracé en base de données (table `notifications`) avec le statut succès/échec.

---

## Tests

### Lancer les tests

Dans IntelliJ : clic droit sur `src/test/` → **Run All Tests**

Ou :

```bash
mvn test
```

### TicketServiceTest (JUnit 5 + Mockito)

| Test | Description |
|---|---|
| `creerTicket_avecDonneesValides_retourneDTO` | Création avec données valides |
| `creerTicket_utilisateurInexistant_leveException` | Exception si créateur inconnu |
| `changerStatut_ticketExistant_retourneDTOMisAJour` | Changement de statut |
| `changerStatut_ticketInexistant_leveException` | Exception si ticket inconnu |
| `assignerTechnicien_ticketEtTechnicienValides_retourneDTO` | Assignation technicien |
| `listerTousLesTickets_retourneListe` | Liste non vide |
| `supprimerTicket_ticketExistant_supprimeSansErreur` | Suppression valide |
| `supprimerTicket_ticketInexistant_leveException` | Exception si ticket inconnu |

### TicketControllerTest (MockMvc + Spring Security)

| Test | Description |
|---|---|
| `listerTickets_utilisateurConnecte_retourne200` | Liste accessible connecté |
| `listerTickets_sansConnexion_redirigeLLogin` | Redirection si non connecté |
| `detailTicket_ticketExistant_retourne200` | Détail accessible |
| `formulaireCreation_retourne200` | Formulaire accessible |
| `creerTicket_donneesValides_redirige` | Création et redirection |
| `creerTicket_titreVide_retourneFormulaire` | Validation titre obligatoire |
| `changerStatut_technicien_redirige` | Technicien peut changer statut |

---

## Évolutions possibles

- Tableau de bord avec statistiques (tickets par statut, par technicien)
- Export des tickets en PDF ou CSV
- API REST complète pour une future application mobile
- Authentification OAuth2 / SSO
- Système de SLA (délais de résolution selon priorité)
- Notifications par SMS via Twilio

---

## Structure Git recommandée

```
feat: init projet Spring Boot
feat: ajout entités JPA (Ticket, Utilisateur, Commentaire)
feat: ajout repositories Spring Data
feat: ajout services métier (TicketService, UtilisateurService)
feat: intégration SendGrid (NotificationService)
feat: ajout controllers REST
feat: configuration Spring Security
feat: ajout vues Thymeleaf
feat: ajout jeu d'essai SQL
test: ajout tests JUnit et MockMvc
docs: ajout documentation
```

---

*Projet réalisé dans le cadre du BTS SIO — Épreuve E6 — Situation professionnelle n°2*
