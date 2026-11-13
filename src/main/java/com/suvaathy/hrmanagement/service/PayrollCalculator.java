package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.dto.PayrollCalculation;

@FunctionalInterface
public interface PayrollCalculator {
    PayrollCalculation calculate(double basicSalary, double bonus);
}