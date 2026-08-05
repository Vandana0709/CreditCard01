# Credit Card Management Portal

Role-based banking application with Spring Boot 3 / Java 17 and an Angular UI. It starts with the welcome page and keeps staff and customer functionality separate.

## Quick start

1. Start the backend: `cd backend; mvn spring-boot:run`
2. Start the frontend: `cd frontend; npm install; npm start`
3. Open `http://localhost:4200`.

The default local profile uses an in-memory H2 database and creates sample data at startup. For Oracle 26ai, create a schema, run `database/schema-oracle.sql`, set credentials in `backend/src/main/resources/application-oracle.yml`, and start with `mvn spring-boot:run -Dspring-boot.run.profiles=oracle`.

## Demo credentials

| Role | Username | Password |
| --- | --- | --- |
| Staff | `staff` | `Staff@123` |
| Customer | `customer` | `Customer@123` |

## API and pages

REST endpoints are grouped under `/api/auth`, `/api/staff`, `/api/customers`, `/api/cards`, `/api/merchants`, `/api/transactions`, `/api/payments`, and `/api/statements`. Staff routes require `STAFF`; customer routes require `CUSTOMER` and always derive customer ownership from the authenticated account. UI pages: welcome, two login pages, staff/customer dashboards, management, purchase/payment, history and statements.
