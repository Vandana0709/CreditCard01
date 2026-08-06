# Mosaic Vault — Backend

Mosaic Vault is a role-based Credit Card Management System built with Spring Boot. The backend exposes secure REST APIs for bank staff and customers, while the Angular frontend provides separate staff and customer portals.

## Project purpose

The project demonstrates a realistic banking workflow:

```text
Account request → Staff approval → Customer registration → Customer login
→ Card request → Staff approval → Manual card issue
→ Purchase / bill payment → Statements / transaction history
```

The system uses internal database IDs for relationships and keeps card numbers unique. Staff dashboards contain only bank-wide aggregates; individual balances appear only in the relevant customer/card records.

## Technology stack

| Area | Technology |
|---|---|
| Language | Java 17 |
| Backend | Spring Boot 3.4.x |
| REST APIs | Spring Web |
| Persistence | Spring Data JPA / Hibernate |
| Security | Spring Security with HTTP Basic Authentication and roles |
| Validation | Jakarta Bean Validation |
| Database | Oracle Database 26ai |
| Local development database | H2 in-memory database |
| Build tool | Maven |
| API documentation | springdoc OpenAPI / Swagger UI |
| Frontend consumer | Angular standalone application |

## Backend package structure

```text
com.bank.portal
├── config/        Security, CORS, seed data
├── domain/        JPA entities and enums
├── repository/    Spring Data JPA repositories
├── service/       Transactional banking and workflow rules
├── web/            REST controllers, DTOs, error handling
└── CreditCardPortalApplication.java
```

This is a layered architecture:

```text
Angular page → Controller → Service → Repository → Oracle tables
```

- Controllers accept requests and enforce endpoint role access.
- Services contain business rules and `@Transactional` balance updates.
- Repositories query entities using Spring Data JPA.
- DTOs prevent direct exposure of JPA entities.

## Roles and access rules

### Bank Staff (`ROLE_STAFF`)

- Staff dashboard with aggregate metrics only
- Customer, merchant, and credit-card management
- Approve/reject account requests
- Approve/reject card requests
- Issue a card manually after approving a request
- View all transactions and statements-related activity
- Run late-fee processing
- Review credit-limit increase requests

### Customer (`ROLE_CUSTOMER`)

- Request an account before becoming a customer
- Register a username and password after staff approval
- Sign in with username **or email** and password
- Request a Silver, Gold, or Platinum card
- View only their own cards, transactions, payments, statements, score, and requests
- Make purchases from the read-only merchant catalog
- Pay only their own card bills

## Database model

Base schema file:

```text
../database/schema-oracle.sql
```

Core tables:

| Table | Purpose |
|---|---|
| `customers` | Customer personal/KYC data and score/reward balances |
| `staff_users` | Staff login credentials |
| `customer_users` | Customer login credentials linked 1:1 to `customers` |
| `credit_cards` | Customer cards, limits, balances, status |
| `merchants` | Merchant administration/catalog data |
| `transactions` | Purchase, payment, cashback, and late-fee activity |
| `monthly_statements` | Monthly card statement aggregates and due-date fields |
| `customer_onboarding_requests` | Public account requests awaiting staff approval |
| `customer_card_requests` | Customer card-type requests awaiting staff action |
| `credit_limit_requests` | Customer credit-limit increase requests |
| `credit_score_events` | Auditable credit-health score changes |

### Oracle migration order

Run these in order **after** the base schema, using the same Oracle schema configured in `application-oracle.yml`:

```text
1. ../database/schema-oracle.sql
2. ../database/migrations/V2_rewards_score_limits.sql
3. ../database/migrations/V3_customer_onboarding_requests.sql
4. ../database/migrations/V4_customer_card_requests.sql
```

The application uses `ddl-auto: validate` for Oracle. This is intentional: Hibernate validates that all expected tables/columns exist, but does not silently change the production database.

## Setup and run

### 1. Configure Oracle

Edit `src/main/resources/application-oracle.yml`:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/FREEPDB1
    username: credit_card_app
    password: change-me
```

Replace the username, password, host, port, and service name for your Oracle installation. Do not commit a real password.

### 2. Run database scripts

Use SQL Developer or SQL*Plus to execute the base schema and migrations listed above.

### 3. Start backend

```powershell
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=oracle"
```

Backend URL:

```text
http://localhost:8080
```

### 4. Local H2 mode

For a quick local demo without Oracle:

```powershell
mvn spring-boot:run
```

H2 is configured as the default profile and creates temporary tables at startup.

## Demo credentials

| Role | Login | Password |
|---|---|---|
| Bank staff | `staff` | `Staff@123` |
| Existing demo customer | `customer` | `Customer@123` |

New customers should normally use the onboarding and signup workflow instead of the demo account.

## API documentation

After backend startup, open:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Swagger prompts for HTTP Basic credentials when calling protected endpoints.

## API groups

| API group | Main purpose |
|---|---|
| `/api/auth` | Login and approved-customer signup |
| `/api/account-requests` | Public account request; staff approval/rejection |
| `/api/staff` | Staff aggregate dashboard |
| `/api/customers` | Staff customer CRUD |
| `/api/cards` | Card issue/manage; customer own cards and blocking |
| `/api/card-requests` | Customer card applications; staff review/issue workflow |
| `/api/merchants` | Staff merchant CRUD |
| `/api/purchase-merchants` | Customer read-only merchant catalog for purchases |
| `/api/transactions` | Purchases, history, filters, and customer recent activity |
| `/api/payments` | Customer bill payments |
| `/api/statements` | Statement generation, customer statements, and secure PDF downloads |
| `/api/features` | Credit score, credit-limit requests, and late-fee task endpoint |

## Frontend page-to-backend mapping

| Angular page | Main backend APIs |
|---|---|
| Welcome | Navigation only |
| Request an account | `POST /api/account-requests` |
| Staff login / Customer login | `GET /api/auth/login` with Basic Authentication |
| Create customer account | `POST /api/auth/signup` |
| Staff Dashboard | `GET /api/staff/dashboard` |
| Customer Dashboard | `GET /api/dashboard`, `GET /api/features/score` |
| Customer Management | `/api/customers` |
| Credit Card Management | `/api/cards` |
| Card Requests | `/api/card-requests` |
| Merchant Management | `/api/merchants` |
| Make Purchase | `/api/purchase-merchants`, `POST /api/transactions/purchase` |
| Pay Bill | `POST /api/payments` |
| Transaction History | `GET /api/transactions` |
| Monthly Statements | `POST /api/statements/card/{cardId}`, `GET /api/statements/mine`, `GET /api/statements/{statementId}/pdf` |

## Important implementation details

### Authentication and authorization

`SecurityConfig` configures Spring Security, HTTP Basic Authentication, CORS for the Angular development server, and role checks. Customer authentication resolves either a username or the customer email; staff authentication uses username.

### Account onboarding

`OnboardingService` validates a public request's email/PAN, creates a `PENDING` request, and prevents duplicate requests. Staff approval creates the actual `Customer` row. Only then can `AuthController` signup create `CustomerUser` credentials tied to that customer email.

### Card application and issue reminder

`CardApplicationService` stores card-type requests. Staff approval changes the request to `APPROVED` and redirects to manual card issue with the customer/type prefilled. Approved requests stay in the staff queue as a reminder until `BankingService.issue()` creates the matching card, then the request becomes `ISSUED`.

### Purchase and payment balance rules

`BankingService` uses transactional methods:

```text
Successful purchase:
available credit -= purchase amount
outstanding amount += purchase amount

Successful payment:
outstanding amount -= payment amount
available credit += payment amount
```

Blocked cards and insufficient available credit create failed transaction records without changing balances.

### Statements and due dates

Statements calculate opening balance, purchases, payments, and closing balance for a selected month. A statement due date is set to 15 days after the statement period end. `FeatureService.applyLateFees()` applies one late fee per overdue unpaid statement:

```text
late fee = max(2% of unpaid closing balance, ₹250)
```

### PDF statement download

Every generated statement shown on the customer **Monthly Statements** page has a **Download PDF** button. The frontend sends an authenticated request to:

```text
GET /api/statements/{statementId}/pdf
```

`StatementController` verifies that the caller has the customer role, and `BankingService` verifies that the requested statement belongs to that authenticated customer. `StatementPdfGenerator` then creates the PDF response in memory. The PDF contains the card, period, due date, opening balance, purchases, payments, closing balance, and the period's card activity. The browser saves the returned file to the user's device; no PDF is stored permanently by the backend.
### Credit Health Score

Customers start at `700`; score updates are clamped to `300–900`. `CreditHealthService` stores every change in `credit_score_events` so the score is explainable rather than opaque.

Current automatic score rules include:

| Event | Score change |
|---|---:|
| Payment recorded on time | +5 |
| Full balance payment | +10 |
| Utilization below 10% | +15 |
| Utilization below 30% | +8 |
| Utilization above 80% | -25 |
| Utilization above 95% | -40 |
| Staff-approved limit increase | +10 |
| Late-fee processing | -20 |

### Rewards and cashback

Successful purchases add one reward point for every full ₹100 spent. Successful payments add cashback balance at 1% of payment. The intended policy is a ₹500 monthly cashback maximum; monthly aggregation should be completed before treating the cashback module as production-ready.

## Validation and error handling

- DTO validation uses `@NotBlank`, `@Email`, `@Pattern`, `@DecimalMin`, `@Future`, and `@NotNull`.
- `ApiExceptionHandler` / `GlobalErrors` return friendly request errors.
- Services throw business-rule errors for invalid ownership, duplicate requests, excessive payments, invalid limits, and workflow conflicts.
- Every customer-facing card/payment action verifies authenticated ownership server-side.

## Verification commands

```powershell
# Backend compile/tests
cd backend
mvn test

# Frontend TypeScript validation
cd ../frontend
npx.cmd tsc --noEmit -p tsconfig.app.json
```

## Current work status

The core role-based portal, onboarding, signup, card-request workflow, score event foundation, credit-limit APIs, and late-fee endpoint are implemented. The fuller rewards/cashback/score roadmap is documented above; the monthly cashback cap and remaining advanced score factors should be completed before production use.
