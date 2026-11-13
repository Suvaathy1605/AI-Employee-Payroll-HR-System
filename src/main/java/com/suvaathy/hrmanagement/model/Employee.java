package com.suvaathy.hrmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(columnNames = "employee_code"), @UniqueConstraint(columnNames = "email")
})
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(name = "employee_code", nullable = false, unique = true, length = 24)
    private String employeeCode;
    @NotBlank @Size(max = 100) @Column(nullable = false, length = 100)
    private String name;
    @NotNull @Min(18) @Max(75) @Column(nullable = false)
    private Integer age;
    @NotBlank @Column(nullable = false, length = 24)
    private String gender;
    @NotBlank @Email @Column(nullable = false, unique = true, length = 150)
    private String email;
    @NotBlank @Column(nullable = false, length = 24)
    private String phone;
    @NotBlank @Column(nullable = false, length = 300)
    private String address;
    @NotBlank @Column(nullable = false, length = 100)
    private String designation;
    @NotNull @PastOrPresent @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;
    @NotNull @PositiveOrZero @Column(name = "basic_salary", nullable = false)
    private Double basicSalary;
    @NotNull @Min(0) @Max(100) @Column(name = "performance_score", nullable = false)
    private Integer performanceScore = 70;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id")
    private Department department;

    public Employee() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }
    public Integer getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Integer performanceScore) { this.performanceScore = performanceScore; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}