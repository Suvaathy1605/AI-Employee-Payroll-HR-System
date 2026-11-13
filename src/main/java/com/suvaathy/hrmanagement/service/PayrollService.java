package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.dto.PayrollCalculation;
import com.suvaathy.hrmanagement.model.Employee;
import com.suvaathy.hrmanagement.model.Payroll;
import com.suvaathy.hrmanagement.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PayrollService {
    private final PayrollRepository payroll;
    private final PayrollCalculator calculator;
    public PayrollService(PayrollRepository payroll, PayrollCalculator calculator) { this.payroll = payroll; this.calculator = calculator; }
    public List<Payroll> findAll() { return payroll.findByOrderByPeriodDesc(); }
    public Payroll generate(Employee employee, String period, double bonus) {
        if (period == null || !period.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new IllegalArgumentException("Pay period must be a valid month in YYYY-MM format.");
        }
        if (payroll.findByEmployeeIdOrderByPeriodDesc(employee.getId()).stream().anyMatch(item -> item.getPeriod().equals(period))) {
            throw new IllegalArgumentException("Payroll already exists for this employee and period.");
        }
        PayrollCalculation amounts = calculator.calculate(employee.getBasicSalary(), bonus);
        Payroll result = new Payroll();
        result.setEmployee(employee); result.setPeriod(period); result.setBasicSalary(amounts.basicSalary());
        result.setHra(amounts.hra()); result.setDa(amounts.da()); result.setBonus(amounts.bonus());
        result.setGrossSalary(amounts.grossSalary()); result.setPf(amounts.pf()); result.setTax(amounts.tax());
        result.setNetSalary(amounts.netSalary());
        return payroll.save(result);
    }
    public Payroll findById(Long id) { return payroll.findById(id).orElseThrow(() -> new IllegalArgumentException("Salary slip was not found.")); }
}