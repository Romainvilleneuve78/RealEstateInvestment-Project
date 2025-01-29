**RealEstateInvestment-Project
Project Description**
RealEstateInvestment-Project is a platform that allows investors to buy shares in real estate properties and receive monthly rental income. Users can browse properties under financing, invest through an integrated wallet, and track their investments.

**Main Features:**
**Real Estate Agent:**
- Add, modify, or delete properties before their launch.
- List all properties with their statuses.
- Manage the financing of properties.


**Investors:**
- Create a profile with the required personal information.
- Add money to their wallet via a payment gateway (Stripe).
- Invest in a property (minimum €500 per investment).
- Track their investments and receive monthly rental income.
- Get refunded if a property is not fully funded within 2 months.

- 
**Properties:**
- Each property has a price, a financing deadline, and a status (AVAILABLE, FUNDED, EXPIRED).
- If a property is not fully funded within 2 months, investors are refunded.
- If it is funded, investors start receiving rental income.


**Technologies Used : **
**Back-end :** Spring Boot (Java), Spring Security, Hibernate (JPA), PostgreSQL.
**Database :** PostgreSQL
**Payment Integration :** Stripe (non réalisé)
**Email service :** JavaMailSender (confirmation d'investissement et revenus locatifs)


**Installation & Execution :**
- Requirements:
- Java 17+
- Maven
- PostgreSQL
- Git


**Installation**
git clone https://github.com/RomainVilleneuve78/RealEstateInvestment-Project.git
cd RealEstateInvestment-Project

Configurez PostgreSQL avec un utilisateur et une base de données.

Mettez à jour le fichier application.properties dans src/main/resources/ avec vos informations de connexion.


**Running the project :**
mvn spring-boot:run

L'API sera disponible à : http://localhost:8080

**Api testing with Postman**

You can use GET, POST, PUT, and DELETE requests to test various routes. A Postman collection is provided.

**Key Endpoints :**

**User management**

Register :
 => POST /api/users
 json : {
     "firstName": "John",
     "lastName": "Doe",
     "email": "johndoe@example.com",
     "role": "INVESTOR",
     "wallet": {
        "balance": 10000.00
     }
 }

Get user by Id:
=> GET /api/users/{userId}

Get all users:
=> GET /api/users


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

 User (Utilisateur)
 
 Créer un utilisateur
 

POST /api/users

Body: 
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "johndoe@example.com",
    "role": "INVESTOR",
    "wallet": {
        "balance": 10000.00
    }
}

 Récupérer un utilisateur par ID

GET /api/users/{userId}

 Récupérer tous les utilisateurs


GET /api/users

 Supprimer un utilisateur
 

DELETE /api/users/{userId}

 Wallet (Portefeuille)
 
 Récupérer le portefeuille d'un utilisateur

GET /api/wallets/{userId}

 Ajouter des fonds au portefeuille

POST /api/wallets/deposit

Body:
{
  "userId": 1,
  "amount": 1000.00
}

 Property (Propriété)
 
 Ajouter une propriété

POST /api/properties

Body: 
{
    "name": "Villa Leo",
    "price": 25000.00,
    "rentalIncomePercentage": 7.5,
    "status": "AVAILABLE",
    "location": "Villejuif, Paris",
    "fundingDeadline": "2025-03-29"
}

 Récupérer toutes les propriétés

GET /api/properties

 Récupérer les propriétés ouvertes au financement 

GET /api/properties/open

 Récupérer une propriété par ID

GET /api/properties/{propertyId}

 Supprimer une propriété

DELETE /api/properties/{propertyId}

 Investment (Investissement)
 
 Investir dans une propriété

POST /api/investments
Body:
{
  "userId": 1,
  "propertyId": 5,
  "amount": 500
  "date": "2025-02-01"
}

 Récupérer les investissements d'un utilisateur

GET /api/investments/user/{userId}

 Récupérer les investissements pour une propriété

GET /api/investments/property/{propertyId}

 Rent Income (Revenus locatifs)
 
 Créditer les revenus locatifs mensuels (tâche planifiée)

POST /api/investments/credit-rent-income
(c'est possible de tester avec @Scheduled qui s'exécute chaque mois)

 Refund (Remboursement automatique)
 
 Vérifier et rembourser les investissements des propriétés non financées après 2 mois (tâche planifiée)

POST /api/properties/check-expired

 Notes
 
Toutes les requêtes POST ont un body JSON

Les endpoints GET récupèrent les données

Il y a des tâches planifiées pour les revenus locatifs et les remboursements

Les utilisateurs doivent avoir un portefeuille avant d’investir

Le montant minimum d’investissement est 500€

On ne peut pas investir plus que le montant total de la propriété

Une propriété "FUNDED" ne peut plus recevoir d’investissement

Une propriété non financée sous 2 mois entraîne un remboursement

![image](https://github.com/user-attachments/assets/a532c096-434b-4d17-ad37-d0986f2a521c)


