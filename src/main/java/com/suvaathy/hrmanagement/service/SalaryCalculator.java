package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.dto.PayrollCalculation;
import com.suvaathy.hrmanagement.exception.InvalidSalaryException;
import org.springframework.stereotype.Service;

@Service
public class SalaryCalculator implements PayrollCalculator {
    @Override
    public PayrollCalculation calculate(double basicSalary, double bonus) {
        if (!Double.isFinite(basicSalary) || basicSalary < 0 || !Double.isFinite(bonus) || bonus < 0) {
            throw new InvalidSalaryException("Salary and bonus must be valid non-negative amounts.");
        }
        double hra = round(basicSalary * 0.20);
        double da = round(basicSalary * 0.10);
        double gross = round(basicSalary + hra + da + bonus);
        double pf = round(basicSalary * 0.12);
        double tax = round(gross * 0.05);
        return new PayrollCalculation(round(basicSalary), hra, da, round(bonus), gross, pf, tax,
                round(gross - pf - tax));
    }

    public PayrollCalculation calculate(double basicSalary) { return calculate(basicSalary, 0); }
    private double round(double value) { return Math.round(value * 100.0) / 100.0; }
}