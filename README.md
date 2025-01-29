RealEstateInvestment-Project

Description du projet

RealEstateInvestment-Project est une plateforme permettant aux investisseurs d'acheter des parts de propriétés immobilières et de recevoir un revenu locatif mensuel. Les utilisateurs peuvent consulter des propriétés en financement, investir dessus via un portefeuille intégré, et suivre leurs investissements.

Fonctionnalités principales

 Agent immobilier

Ajouter, modifier ou supprimer des propriétés avant leur lancement.

Lister toutes les propriétés avec leurs statuts.

Gérer le financement des propriétés.

 Investisseurs

Créer un profil avec des informations personnelles requises.

Ajouter de l'argent à leur portefeuille via une passerelle de paiement (Stripe).

Investir dans une propriété (minimum 500€ par investissement).

Suivre leurs investissements et recevoir un revenu locatif chaque mois.

Être remboursé si une propriété n'est pas totalement financée en 2 mois.

 Propriétés

Chaque propriété a un prix, une date limite de financement et un statut (AVAILABLE, FUNDED, EXPIRED).

Si la propriété n'est pas entièrement financée en 2 mois, les investisseurs sont remboursés.

Si elle est financée, les investisseurs commencent à recevoir un revenu locatif.

 Technologies utilisées

Back-end : Spring Boot (Java), Spring Security, Hibernate (JPA), PostgreSQL.

Base de données : PostgreSQL

Paiement : Stripe (non réalisé)

Email : JavaMailSender (confirmation d'investissement et revenus locatifs)

 Installation et exécution

 Prérequis

Java 17+

Maven

PostgreSQL

Git

 Installation

Clonez le dépôt GitHub :

git clone https://github.com/RomainVilleneuve78/RealEstateInvestment-Project.git
cd RealEstateInvestment-Project

Configurez PostgreSQL avec un utilisateur et une base de données.

Mettez à jour le fichier application.properties dans src/main/resources/ avec vos informations de connexion.

 Exécution du projet

Compilez et exécutez le projet avec Maven :

mvn spring-boot:run

L'API sera disponible à : http://localhost:8080

 Tests API avec Postman

Vous pouvez utiliser les requêtes GET, POST, PUT, DELETE pour tester les différentes routes. Une collection Postman est fournie.

🛠️ Endpoints principaux

📌 Gestion des utilisateurs

POST /api/users → Inscription

GET /api/users/{id} → Détails d’un utilisateur

📌 Gestion des propriétés

POST /api/properties → Ajouter une propriété

GET /api/properties → Lister les propriétés (6 max pour les investisseurs)

GET /api/properties/{id} → Détails d’une propriété

DELETE /api/properties/{id} → Supprimer une propriété

📌 Gestion des investissements

POST /api/investments → Investir dans une propriété

GET /api/investments/user/{userId} → Récupérer les investissements d’un utilisateur

GET /api/investments/property/{propertyId} → Récupérer les investissements d’une propriété

 Architecture

L’architecture suit le modèle MVC (Model-View-Controller) :

Controller : Gère les requêtes API.

Service : Contient la logique métier.

Repository : Interagit avec la base de données via JPA/Hibernate.

L'application est structurée selon une architecture MVC (Model-View-Controller) :

Modèle (Model)

Gère la persistance des données avec JPA et Hibernate. Voici les entités principales :

User : Représente un utilisateur avec son portefeuille (Wallet).

Property : Représente une propriété immobilière en financement.

Investment : Relie un utilisateur à une propriété avec un montant investi.

Wallet : Stocke l’argent de l’utilisateur et reçoit les revenus locatifs.

Service (Service Layer)

Gère la logique métier :

InvestmentService : Vérifie les fonds disponibles, applique la logique d’investissement et de remboursement automatique.

PropertyService : Gère les propriétés et vérifie le statut (FUNDED, EXPIRED).

UserService : Gère les utilisateurs et leur portefeuille.

Contrôleurs (Controllers)

Exposent les endpoints REST :

PropertyController : Gestion des propriétés.

InvestmentController : Gestion des investissements.

UserController : Gestion des utilisateurs et authentification.

endpoint et requetes principal pour postman
