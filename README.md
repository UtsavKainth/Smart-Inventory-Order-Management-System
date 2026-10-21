# Smart Inventory & Order Management System

## Advanced Database Management System Mini Project

A web-based inventory and order management application developed using Java Servlets, JDBC, Oracle Database, HTML, CSS, and JavaScript.

## Project Features

- Real-time inventory dashboard
- Product inventory with category and supplier details
- Customer order placement
- Automatic stock deduction
- Order history and sales tracking
- Stock audit history
- Low-stock identification
- Database transaction management

## Technology Stack

| Component | Technology |
|-----------|------------|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java 17, Jakarta Servlets |
| Database | Oracle Database |
| Connectivity | JDBC |
| Server | Apache Tomcat 10 |
| Build Tool | Apache Maven |
| IDE | Visual Studio Code |

## Database Design

The project uses seven primary tables:

1. ADBMS_CATEGORIES
2. ADBMS_SUPPLIERS
3. ADBMS_PRODUCTS
4. ADBMS_CUSTOMERS
5. ADBMS_ORDERS
6. ADBMS_ORDER_ITEMS
7. ADBMS_STOCK_AUDIT

## Advanced Database Concepts

### Stored Procedure
ADBMS_PLACE_ORDER validates stock availability, creates orders, inserts order items, and updates inventory.

### Database Trigger
TRG_ADBMS_STOCK_AUDIT automatically records stock changes.

### Views
- ADBMS_INVENTORY_VIEW
- ADBMS_SALES_VIEW

### Indexes
Indexes support product category lookups and order queries.

### Transaction Management
JDBC transactions use COMMIT and ROLLBACK to maintain consistency.

## Application APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/products | Retrieve inventory |
| GET | /api/orders | Retrieve order history |
| POST | /api/orders | Place a new order |
| GET | /api/audit | Retrieve stock audit history |

## Local Setup

1. Install Java 17, Maven, Oracle Database, and Tomcat 10.
2. Create the Oracle database schema and required objects.
3. Configure the ADBMS_DB_USER and ADBMS_DB_PASSWORD environment variables for Tomcat.
4. Ensure the Oracle service FREEPDB1 is available on localhost:1521.
5. Build the application using `mvn clean package`.
6. Deploy `target/ADBMS_PROJECT.war` to Tomcat's webapps directory.
7. Open http://localhost:8080/ADBMS_PROJECT/

Database passwords and other credentials must not be committed to GitHub.

## Project Status

Core inventory, order placement, order history, and stock audit modules implemented and tested.

## Academic Purpose

Developed as an Advanced Database Management System mini-project.
