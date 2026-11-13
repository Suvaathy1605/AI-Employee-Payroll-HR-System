# Employee Payroll & HR Management System

A Java Programming Project Based Learning application for employee, attendance, leave, payroll, and HR analytics workflows.

## Requirements

- JDK 17
- Maven 3.9 or later
- MySQL 8 or later

Spring Boot 4.1.1, Spring Data JPA, JDBC, Thymeleaf, Spring Security, and Jakarta Validation are managed by Maven in `pom.xml`.

## Database and Admin Setup

Create the database and tables with MySQL:

```powershell
Get-Content .\database\schema.sql -Raw | mysql -u root -p
```

Configure the connection and first admin account in PowerShell before starting the application. The demo defaults are `admin` / `admin123`; set a private password for your local use.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:DB_URL = 'jdbc:mysql://localhost:3306/employee_hr_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'your-mysql-password'
$env:ADMIN_USERNAME = 'admin'
$env:ADMIN_PASSWORD = 'your-admin-password'
```

On first startup, the application stores the configured admin password as a BCrypt hash in the `admin` table. Existing admin accounts are not overwritten. The application user needs permission to create/use the database and its tables; alternatively, run `database/schema.sql` first.

## Build and Run

```powershell
mvn clean test
mvn clean package
mvn spring-boot:run
```

Open `http://localhost:8080/login`. HR records, dashboard figures, analytics inputs, and reports are read from MySQL. The attendance CSV report uses JDBC with a prepared statement; the backup action writes `backups/attendance-backup.csv` on a background thread.

## Project Concepts

The implementation demonstrates entity and service object composition, exception inheritance, an abstract business-exception base class, the `PayrollCalculator` interface and its implementation, collections, lambdas, streams, comparators, the Java Date and Time API, file handling, `Runnable`, background threading, and synchronized backup writing.

HR analytics is explainable rule-based decision support using attendance, employee-entered performance, approved leave, and tenure. It is not an ML model and does not claim to predict outcomes.