**RealEstateInvestment-Project**
**Project Description**


RealEstateInvestment-Project is a platform that allows investors to buy shares in real estate properties and receive monthly rental income. Users can browse properties under financing, invest through an integrated wallet, and track their investments.


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

Delete a user:
=> DELETE /api/users/{userId}


**Wallet Management**
Get a user's wallet:
=> GET /api/wallets/{userId}

Add funds to wallet:
=> POST /api/wallets/deposit

json : {
  "userId": 1,
  "amount": 1000.00
}

**Property Management**

Add a property:
=> POST /api/properties
json : {
    "name": "Villa Leo",
    "price": 25000.00,
    "rentalIncomePercentage": 7.5,
    "status": "AVAILABLE",
    "location": "Villejuif, Paris",
    "fundingDeadline": "2025-03-29"
}

Get all properties:
=> GET /api/properties

Get properties by ID:
=> GET /api/properties/{propertyId}

Delete a property:
=> DELETE /api/properties/{propertyId}


**Investment Mangement**

Invest in a property:
=> POST /api/investments
json : {
  "userId": 1,
  "propertyId": 5,
  "amount": 500,
  "date": "2025-02-01"
}

Get a user's investments:
=> GET /api/investments/user/{userId}

Get invesments for a property:
+> GET /api/investments/property/{propertyId}

![image](https://github.com/user-attachments/assets/a532c096-434b-4d17-ad37-d0986f2a521c)


