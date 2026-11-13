package com.suvaathy.hrmanagement.dto;

import java.util.List;

public record AnalyticsDTO(Long employeeId, String employeeName, int productivityScore,
                           String productivityCategory, int performanceScore, String performanceCategory,
                           int attritionRiskScore, String attritionRisk, double attendancePercentage,
                           double currentSalary, double recommendedMinimumSalary, double recommendedMaximumSalary,
                           List<String> factors, String explanation) { }