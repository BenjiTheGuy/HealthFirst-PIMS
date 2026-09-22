### HealthFirst Pharmacy – Pharmacy Inventory Management System

HealthFirst PIMS is a desktop-based Pharmacy Inventory Management System developed using Java Swing, JDBC, and MySQL. The system is designed to assist pharmacy staff with managing medicines, suppliers, users, sales, stock levels, and inventory reports.

## Features
# Admin
- Secure admin login 
- Manage medicines 
- Manage suppliers 
- Manage cashier accounts 
- Update and reset cashier passwords 
- Delete cashier accounts 
- View sales reports 
- View item-wise sales reports 
- View low-stock medicines 
- View medicines nearing expiry 
- Monitor pharmacy inventory

# Cashier
- Secure cashier login 
- Search and select medicines 
- Add medicines to a sale 
- Calculate sale totals 
- Complete sales 
- Automatically update medicine stock 
- View stock availability

## Technologies Used
- Java 
- Java Swing / AWT 
- JDBC 
- MySQL 
- MySQL Workbench 
- IntelliJ IDEA

## Project Structure
HealthFirst-PIMS/
│
├── database/
│   └── pharmacy_db.sql
│
├── resources/
│   └── logo.png
│
├── src/
│   ├── AdminDashboard.java
│   ├── CashierDashboard.java
│   ├── LoginForm.java
│   ├── MedicinePanel.java
│   ├── POSPanel.java
│   ├── Pharmacy_Application.java
│   ├── ReportPanel.java
│   ├── StockCheckPanel.java
│   ├── SupplierPanel.java
│   ├── UserPanel.java
│   └── database/
│       └── DatabaseConnection.java
│
├── .gitignore
└── README.md

## Database Setup
1. Open MySQL Workbench.
2. Create the database:
 CREATE DATABASE pharmacy_db;
3. Open database/pharmacy_db.sql.
4. Run the SQL script to create and populate the required tables. 

## Database Configuration

The application uses environment variables for the database credentials rather than storing the username and password directly in the source code.

Configure the following environment variables:

DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password

The database connection uses:

Host: localhost
Port: 3306
Database: pharmacy_db

## Running the Application
1. Clone this repository.
2. Open the project in IntelliJ IDEA.
3. Make sure MySQL is running.
4. Create and configure the pharmacy_db database.
5. Configure DB_USERNAME and DB_PASSWORD.
6. Make sure the MySQL Connector/J dependency is available to the project.
7. Run:
 Pharmacy_Application.java

## Login Credentials

The SQL database contains default accounts for testing.

# Admin
Username: admin
Password: admin123

# Cashier
Username: cashier
Password: cashier123

These credentials are provided for demonstration and assignment testing purposes.

## Database
The database script is included in:
    database/pharmacy_db.sql

The database contains the tables required for:
- users 
- suppliers 
- medicines
- sales
- sale_items

## Author

# Benjamin Montague

# HealthFirst Pharmacy – Pharmacy Inventory Management System