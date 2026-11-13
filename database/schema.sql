CREATE DATABASE IF NOT EXISTS employee_hr_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE employee_hr_db;

CREATE TABLE IF NOT EXISTS admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(24) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL CHECK (age BETWEEN 18 AND 75),
    gender VARCHAR(24) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(24) NOT NULL,
    address VARCHAR(300) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    joining_date DATE NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (basic_salary >= 0),
    performance_score INT NOT NULL DEFAULT 70 CHECK (performance_score BETWEEN 0 AND 100),
    department_id BIGINT,
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    period CHAR(7) NOT NULL,
    working_days INT NOT NULL CHECK (working_days BETWEEN 1 AND 31),
    present_days INT NOT NULL CHECK (present_days >= 0),
    absent_days INT NOT NULL DEFAULT 0 CHECK (absent_days >= 0),
    leave_days INT NOT NULL DEFAULT 0 CHECK (leave_days >= 0),
    attendance_percentage DECIMAL(5,2) NOT NULL DEFAULT 0 CHECK (attendance_percentage BETWEEN 0 AND 100),
    UNIQUE (employee_id, period),
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT chk_attendance_days CHECK (present_days + absent_days + leave_days = working_days)
);

CREATE TABLE IF NOT EXISTS leaves (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    leave_type VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT chk_leave_dates CHECK (end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS payroll (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    period CHAR(7) NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL CHECK (basic_salary >= 0),
    hra DECIMAL(12,2) NOT NULL DEFAULT 0,
    da DECIMAL(12,2) NOT NULL DEFAULT 0,
    bonus DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (bonus >= 0),
    pf DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax DECIMAL(12,2) NOT NULL DEFAULT 0,
    gross_salary DECIMAL(12,2) NOT NULL,
    net_salary DECIMAL(12,2) NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, period),
    CONSTRAINT fk_payroll_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT chk_payroll_net CHECK (net_salary >= 0)
);