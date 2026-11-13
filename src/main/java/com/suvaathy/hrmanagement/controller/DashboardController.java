package com.suvaathy.hrmanagement.controller;

import com.suvaathy.hrmanagement.repository.*;
import com.suvaathy.hrmanagement.model.LeaveRequest;
import com.suvaathy.hrmanagement.service.HRAnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final AttendanceRepository attendance;
    private final LeaveRequestRepository leaves;
    private final HRAnalyticsService analytics;
    public DashboardController(EmployeeRepository employees, DepartmentRepository departments,
                               AttendanceRepository attendance, LeaveRequestRepository leaves, HRAnalyticsService analytics) {
        this.employees = employees; this.departments = departments; this.attendance = attendance;
        this.leaves = leaves; this.analytics = analytics;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        var employeeList = employees.findAll();
        var attendanceList = attendance.findAll();
        model.addAttribute("totalEmployees", employeeList.size());
        model.addAttribute("totalDepartments", departments.count());
        model.addAttribute("averageSalary", employeeList.stream().mapToDouble(employee -> employee.getBasicSalary()).average().orElse(0));
        model.addAttribute("averageAttendance", attendanceList.stream().mapToDouble(item -> item.getAttendancePercentage()).average().orElse(0));
        model.addAttribute("pendingLeaves", leaves.countByStatus(LeaveRequest.Status.PENDING));
        model.addAttribute("highRiskCount", analytics.analyzeAll().stream().filter(item -> item.attritionRisk().equals("HIGH")).count());
        model.addAttribute("recentEmployees", employeeList.stream().sorted((a, b) -> b.getJoiningDate().compareTo(a.getJoiningDate())).limit(5).toList());
        return "dashboard";
    }
}