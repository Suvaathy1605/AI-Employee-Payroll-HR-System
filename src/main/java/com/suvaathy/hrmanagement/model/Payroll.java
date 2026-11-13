package com.suvaathy.hrmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "period"}))
public class Payroll {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    @Column(nullable = false, length = 7)
    private String period;
    @Column(name = "basic_salary", nullable = false)
    private double basicSalary;
    @Column(nullable = false)
    private double hra;
    @Column(nullable = false)
    private double da;
    @Column(nullable = false)
    private double bonus;
    @Column(nullable = false)
    private double pf;
    @Column(nullable = false)
    private double tax;
    @Column(name = "gross_salary", nullable = false)
    private double grossSalary;
    @Column(name = "net_salary", nullable = false)
    private double netSalary;
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    public Payroll() { }
    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public double getHra() { return hra; }
    public void setHra(double hra) { this.hra = hra; }
    public double getDa() { return da; }
    public void setDa(double da) { this.da = da; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
    public double getPf() { return pf; }
    public void setPf(double pf) { this.pf = pf; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }
    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}