package com.suvaathy.hrmanagement.dto;

public record PayrollCalculation(double basicSalary, double hra, double da, double bonus,
                                 double grossSalary, double pf, double tax, double netSalary) { }