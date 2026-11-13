package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.dto.AnalyticsDTO;
import com.suvaathy.hrmanagement.model.Attendance;
import com.suvaathy.hrmanagement.model.Employee;
import com.suvaathy.hrmanagement.repository.AttendanceRepository;
import com.suvaathy.hrmanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HRAnalyticsService {
    private final EmployeeRepository employees;
    private final AttendanceRepository attendance;
    private final LeaveService leaves;

    public HRAnalyticsService(EmployeeRepository employees, AttendanceRepository attendance, LeaveService leaves) {
        this.employees = employees; this.attendance = attendance; this.leaves = leaves;
    }

    public List<AnalyticsDTO> analyzeAll() {
        List<Employee> allEmployees = employees.findAll();
        Map<String, SalaryBenchmark> benchmarks = buildSalaryBenchmarks(allEmployees);
        return allEmployees.stream().map(employee -> analyze(employee, benchmarks))
                .sorted(Comparator.comparingInt(AnalyticsDTO::attritionRiskScore).reversed()).toList();
    }

    public AnalyticsDTO analyze(Employee employee) {
        return analyze(employee, buildSalaryBenchmarks(employees.findAll()));
    }

    private AnalyticsDTO analyze(Employee employee, Map<String, SalaryBenchmark> benchmarks) {
        List<Attendance> history = attendance.findByEmployeeIdOrderByPeriodDesc(employee.getId());
        double attendanceRate = history.isEmpty() ? 0 : history.stream().mapToDouble(Attendance::getAttendancePercentage).average().orElse(0);
        long approvedLeaveDays = leaves.approvedDaysThisYear(employee.getId());
        long tenureYears = Math.max(0, ChronoUnit.YEARS.between(employee.getJoiningDate(), LocalDate.now()));
        int performance = employee.getPerformanceScore() == null ? 0 : employee.getPerformanceScore();
        double currentSalary = employee.getBasicSalary() == null ? 0 : employee.getBasicSalary();
        SalaryBenchmark benchmark = benchmarks.get(normalizeDesignation(employee.getDesignation()));
        boolean hasPeerBenchmark = benchmark != null && benchmark.employeeCount() >= 3;
        boolean belowPeerSalary = hasPeerBenchmark && currentSalary < benchmark.medianSalary() * 0.80;
        int productivity = history.isEmpty() ? performance : (int) Math.round(attendanceRate * 0.60 + performance * 0.40);
        productivity = (int) Math.max(0, productivity - Math.min(15, approvedLeaveDays));
        String performanceCategory = performance >= 90 ? "EXCELLENT" : performance >= 75 ? "GOOD" : performance >= 55 ? "AVERAGE" : "NEEDS IMPROVEMENT";
        String productivityCategory = productivity >= 85 ? "Excellent" : productivity >= 70 ? "Good" : productivity >= 50 ? "Monitor" : "Needs review";
        int riskScore = (history.isEmpty() ? 0 : attendanceRate < 75 ? 50 : attendanceRate < 85 ? 20 : 0)
                + (approvedLeaveDays >= 10 ? 25 : approvedLeaveDays >= 6 ? 10 : 0)
                + (tenureYears >= 5 ? 25 : 0)
                + (performance < 50 ? 25 : performance < 65 ? 10 : 0)
                + (belowPeerSalary ? 10 : 0);
        riskScore = Math.min(100, riskScore);
        String risk = riskScore >= 60 ? "HIGH" : riskScore >= 30 ? "MEDIUM" : "LOW";
            double performanceAdjustment = performance >= 90 ? 0.04 : performance >= 75 ? 0.02 : 0;
            double attendanceAdjustment = history.isEmpty() ? 0 : attendanceRate >= 90 ? 0.02 : attendanceRate >= 80 ? 0.01 : 0;
            double tenureAdjustment = tenureYears >= 5 ? 0.02 : tenureYears >= 2 ? 0.01 : 0;
            double salaryBaseline = hasPeerBenchmark ? currentSalary * 0.70 + benchmark.medianSalary() * 0.30 : currentSalary;
            double recommendedMinimum = round(salaryBaseline * (1 + performanceAdjustment + attendanceAdjustment + tenureAdjustment));
            double recommendedMaximum = round(recommendedMinimum * 1.05);
            Set<String> factorSet = new LinkedHashSet<>();
            List<String> factors = new ArrayList<>();
            factorSet.add(history.isEmpty() ? "Attendance history is unavailable; no attendance uplift is applied"
                : String.format("Average recorded attendance: %.1f%%", attendanceRate));
            factorSet.add("Employee-maintained performance input: " + performance + "/100");
            factorSet.add("Approved leave this year: " + approvedLeaveDays + " day(s)");
            factorSet.add("Tenure: " + tenureYears + " year(s)");
            factorSet.add("Current basic salary: " + round(currentSalary));
            if (hasPeerBenchmark) {
                factorSet.add("Same-designation cohort median basic salary (" + benchmark.employeeCount() + " employees): "
                    + round(benchmark.medianSalary()));
                factorSet.add(belowPeerSalary
                    ? "Salary risk signal: current salary is below 80% of the same-designation cohort median (+10 risk points)"
                    : "Salary risk signal: current salary is at least 80% of the same-designation cohort median");
            } else {
                factorSet.add("Designation salary comparison omitted because fewer than 3 matching employee records exist");
            }
            factorSet.add("Recommendation adjustments: performance " + percent(performanceAdjustment)
                + ", attendance " + percent(attendanceAdjustment) + ", tenure " + percent(tenureAdjustment));
            factors.addAll(factorSet);
        String explanation = history.isEmpty()
            ? "Attendance-based productivity and risk signals are incomplete until attendance is recorded. Productivity currently uses the employee-entered performance input. Attrition risk applies rules to leave, tenure, performance and a sufficiently sized designation salary cohort. The salary range uses current pay, performance, tenure, recorded attendance when available, and peer data only when at least three records match. This is illustrative decision support, not a guaranteed prediction or external market valuation."
                : "Productivity combines recorded attendance (60%) and employee-entered performance (40%), then accounts for approved leave. Attrition risk applies measurable attendance, leave, tenure, performance, and sufficiently sized designation salary-cohort rules. The salary range uses current pay plus transparent performance, tenure, attendance and peer-data adjustments. This is illustrative decision support, not a guaranteed prediction or external market valuation.";
        return new AnalyticsDTO(employee.getId(), employee.getName(), productivity, productivityCategory,
                performance, performanceCategory, riskScore, risk, attendanceRate,
                currentSalary, recommendedMinimum, recommendedMaximum,
                List.copyOf(factors), explanation);
    }

            private Map<String, SalaryBenchmark> buildSalaryBenchmarks(List<Employee> records) {
            Map<String, List<Double>> salaryGroups = records.stream()
                .filter(employee -> employee.getDesignation() != null && employee.getBasicSalary() != null)
                .collect(Collectors.groupingBy(employee -> normalizeDesignation(employee.getDesignation()), HashMap::new,
                    Collectors.mapping(Employee::getBasicSalary, Collectors.toCollection(ArrayList::new))));
            Map<String, SalaryBenchmark> benchmarks = new HashMap<>();
            salaryGroups.forEach((designation, salaries) -> {
                salaries.sort(Double::compareTo);
                double[] salaryArray = salaries.stream().mapToDouble(Double::doubleValue).toArray();
                int middle = salaryArray.length / 2;
                double median = salaryArray.length % 2 == 0
                    ? (salaryArray[middle - 1] + salaryArray[middle]) / 2
                    : salaryArray[middle];
                benchmarks.put(designation, new SalaryBenchmark(median, salaryArray.length));
            });
            return benchmarks;
            }

            private String normalizeDesignation(String designation) {
            return designation == null ? "" : designation.trim().toLowerCase(Locale.ROOT);
            }

            private double round(double value) { return Math.round(value * 100.0) / 100.0; }
            private String percent(double value) { return Math.round(value * 100) + "%"; }

            private record SalaryBenchmark(double medianSalary, int employeeCount) { }
}