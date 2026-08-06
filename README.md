# Mosaic Vault

Mosaic Vault is a role-based credit-card management portal. It gives bank staff a single place to manage customers, cards, merchants and customer requests, while giving customers a secure portal to manage their own cards, purchases, payments and monthly statements.

The project is designed as a full-stack banking case-study application. It separates staff and customer responsibilities, enforces ownership checks in the backend, and can run either with a fast in-memory demo database or with a local Oracle database for persistent data.


## What the application does

### Staff portal

- Review customer onboarding requests and approve or reject them.
- Create, view, update and manage customer records.
- Issue credit cards and set card type, limit, expiry date and status.
- Block or reactivate cards.
- Manage the merchant catalogue used during purchases.
- Review customer card requests and continue to card issuance.
- View portfolio dashboard information and transaction history.

### Customer portal

- Sign up for online access after bank/customer verification.
- View personal dashboard, card balances and available credit.
- Submit a new card request.
- Block an owned card immediately.
- Make purchases at registered merchants.
- Pay a credit-card bill.
- View personal transaction history.
- Generate monthly statements and download statement PDFs.
- View customer-oriented credit-health information, where enabled.

## Application flow

```text
Visitor
  -> submits onboarding request
  -> staff reviews the request
  -> staff creates/approves the customer record
  -> customer creates online account and signs in
  -> customer requests a card
  -> staff reviews request and issues card
  -> customer makes purchases or payments
  -> card balances, transaction history and monthly statements are updated
```

### Purchase and payment rules

1. A customer can use only a card that belongs to their account.
2. A purchase is approved only when the card is active and has enough available credit.
3. An approved purchase reduces available credit and increases the outstanding balance.
4. A payment cannot be greater than the card's outstanding balance.
5. A successful payment reduces the outstanding balance and restores available credit.
6. Failed purchases and payments are retained as transactions with a failure reason.

## Technology stack

| Layer | Technology |
| --- | --- |
| Frontend | Angular 19, TypeScript, HTML and CSS |
| Backend | Spring Boot 3.4, Java 17 |
| Data access | Spring Data JPA / Hibernate |
| Security | Spring Security, HTTP Basic authentication, BCrypt password hashing |
| Databases | H2 for local demo mode; Oracle Free/Oracle Database for persistent mode |
| Build tools | Maven and npm |

## Architecture and implementation approach

```text
Angular SPA (localhost:4200)
        |
        | HTTP REST API + Basic Authentication
        v
Spring Boot API (localhost:8080)
        |
        +-- Controllers: receive and validate API requests
        +-- Services: apply banking rules and transactions
        +-- Spring Security: authorize STAFF and CUSTOMER actions
        +-- Repositories: persist domain data with JPA
        v
H2 demo database or Oracle database
```

The backend follows a layered approach:

- **Controllers** expose REST endpoints under `/api`.
- **Services** contain the business logic for card issue, purchase, payment, statements, onboarding and card applications.
- **Repositories** provide database access through JPA entities.
- **Security configuration** assigns `STAFF` and `CUSTOMER` roles and prevents a customer from accessing another customer's card or transaction data.
- **Angular API service** sends authenticated requests to `http://localhost:8080/api` and stores the current browser session locally.

## Main API areas

| Area | Base path | Purpose |
| --- | --- | --- |
| Authentication | `/api/auth` | Sign-in and customer online-account creation |
| Staff dashboard | `/api/staff` | Staff dashboard information |
| Customers | `/api/customers` | Customer management |
| Cards | `/api/cards` | Card issue, status management and customer card access |
| Merchants | `/api/merchants` | Merchant catalogue management |
| Purchases | `/api/transactions/purchase` | Customer card purchases |
| Transactions | `/api/transactions` | Search and transaction history |
| Payments | `/api/payments` | Customer bill payments |
| Statements | `/api/statements` | Monthly statements and PDF download |
| Onboarding | `/api/account-requests` | Customer onboarding requests |
| Card requests | `/api/card-requests` | Customer card-application workflow |

## Project structure

```text
CreditCard01/
├── frontend/                    # Angular user interface
│   └── src/app/
│       ├── app.component.ts     # Portal pages and UI interactions
│       └── api.service.ts       # Calls to the Spring Boot REST API
├── backend/                     # Spring Boot application
│   └── src/main/
│       ├── java/com/bank/portal/
│       │   ├── config/          # Security and startup configuration
│       │   ├── domain/          # JPA entities and enums
│       │   ├── repository/      # JPA repositories
│       │   ├── service/         # Business logic
│       │   └── web/             # REST controllers and API DTOs
│       └── resources/
│           ├── application.yml          # H2 demo profile
│           └── application-oracle.yml   # Oracle profile
└── database/
    └── schema-oracle.sql        # Oracle database schema script
```

## Run locally with H2 demo mode

This is the easiest option for demonstrations. Data is created in memory and is removed when the backend stops.

### Prerequisites

- Java 17
- Maven 3.9 or later
- Node.js and npm

### Start the backend

```powershell
cd backend
mvn spring-boot:run
```

### Start the frontend

Open a second terminal:

```powershell
cd frontend
npm.cmd install
npm.cmd start
```

Open `http://localhost:4200` in the browser.

`npm.cmd` is useful on Windows when PowerShell blocks `npm.ps1` through its execution policy.

### Demo accounts

| Role | Username | Password |
| --- | --- | --- |
| Staff | `staff` | `Staff@123` |
| Customer | `customer` | `Customer@123` |

## Run locally with Oracle

Use Oracle mode when data should persist between backend restarts.

1. Ensure Oracle is running and the listener is reachable.
2. Create a dedicated application user, for example `credit_card_app`.
3. Connect as that application user and run `database/schema-oracle.sql`.
4. Set the database password only for the current terminal session:

   ```powershell
   $securePassword = Read-Host "Enter Oracle password" -AsSecureString
   $env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $securePassword).Password
   ```

5. Start the Oracle Spring profile:

   ```powershell
   cd backend
   $env:SPRING_PROFILES_ACTIVE = "oracle"
   mvn spring-boot:run
   ```

The Oracle profile is configured in `backend/src/main/resources/application-oracle.yml`. It uses the `DB_PASSWORD` environment variable so a password is not committed to the project.

> The schema must be executed while connected as `credit_card_app`, not as `SYSTEM`; tables belong to the user that executes the script.

## Security notes

- Passwords are stored using BCrypt hashes, not as plain text.
- Customer endpoints derive customer ownership from the authenticated account.
- Staff-only actions are protected by the `STAFF` role.
- The backend permits local frontend origins (`http://localhost:*`) through CORS configuration.
- This is a learning/case-study application. Before production use, replace HTTP Basic authentication with token-based authentication, use HTTPS, use a secrets manager, enforce audit logging, and add production-grade database migrations and testing.

## Methodology

The project is implemented incrementally around core banking workflows:

1. **Model the domain** — customers, users, cards, merchants, transactions and statements are represented as entities.
2. **Create secure roles** — staff and customer privileges are separated at the API layer.
3. **Build staff operations** — onboarding, customer management, merchant maintenance, card issue and request review.
4. **Build customer operations** — login, card visibility, self-service blocking, purchases, payments and statements.
5. **Enforce business rules** — validate card status, available credit, payment amount and ownership before changing balances.
6. **Connect the UI** — Angular pages call the backend REST API and refresh data after each action.
7. **Support local persistence** — H2 supports quick demonstrations; Oracle supports a persistent local database.

## Future enhancements

- JWT or OAuth2 authentication with refresh tokens.
- Email/SMS notifications for card status changes, payments and statements.
- Payment-gateway integration.
- Automated unit, integration and end-to-end tests.
- Flyway or Liquibase database migrations.
- Audit trails, rate limiting, monitoring and role-management screens.

