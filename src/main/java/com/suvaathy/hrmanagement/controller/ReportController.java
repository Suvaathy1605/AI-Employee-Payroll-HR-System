package com.suvaathy.hrmanagement.controller;

import com.suvaathy.hrmanagement.model.*;
import com.suvaathy.hrmanagement.repository.*;
import com.suvaathy.hrmanagement.service.ReportService;
import com.suvaathy.hrmanagement.service.ReportBackupTask;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReportController {
    private final ReportService reportService;
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final LeaveRequestRepository leaves;
    private final PayrollRepository payroll;

    public ReportController(ReportService reportService, EmployeeRepository employees, DepartmentRepository departments,
                            LeaveRequestRepository leaves, PayrollRepository payroll) {
        this.reportService = reportService; this.employees = employees; this.departments = departments;
        this.leaves = leaves; this.payroll = payroll;
    }

    @GetMapping("/reports/export/{type}")
    public ResponseEntity<byte[]> export(@PathVariable String type, @RequestParam(required = false) String period) {
        String content = switch (type) {
            case "employees" -> "Employee ID,Name,Email,Department,Designation,Joining Date,Basic Salary\n" + employees.findAll().stream()
                    .map(e -> csv(e.getEmployeeCode()) + "," + csv(e.getName()) + "," + csv(e.getEmail()) + ","
                            + csv(e.getDepartment() == null ? "Unassigned" : e.getDepartment().getName()) + "," + csv(e.getDesignation()) + ","
                            + e.getJoiningDate() + "," + e.getBasicSalary()).collect(java.util.stream.Collectors.joining("\n"));
            case "departments" -> "Department ID,Name,Description\n" + departments.findAll().stream()
                    .map(d -> d.getId() + "," + csv(d.getName()) + "," + csv(d.getDescription() == null ? "" : d.getDescription())).collect(java.util.stream.Collectors.joining("\n"));
            case "attendance" -> null;
            case "leaves" -> "Leave ID,Employee ID,Employee Name,Type,Start Date,End Date,Days,Status\n" + leaves.findAll().stream()
                    .map(l -> l.getId() + "," + csv(l.getEmployee().getEmployeeCode()) + "," + csv(l.getEmployee().getName()) + ","
                            + csv(l.getLeaveType()) + "," + l.getStartDate() + "," + l.getEndDate() + "," + l.getNumberOfDays() + "," + l.getStatus())
                    .collect(java.util.stream.Collectors.joining("\n"));
            case "payroll" -> "Employee ID,Employee Name,Period,Basic,HRA,DA,Bonus,Gross,PF,Tax,Net\n" + payroll.findAll().stream()
                    .map(p -> csv(p.getEmployee().getEmployeeCode()) + "," + csv(p.getEmployee().getName()) + "," + p.getPeriod() + ","
                            + p.getBasicSalary() + "," + p.getHra() + "," + p.getDa() + "," + p.getBonus() + "," + p.getGrossSalary()
                            + "," + p.getPf() + "," + p.getTax() + "," + p.getNetSalary()).collect(java.util.stream.Collectors.joining("\n"));
            default -> throw new IllegalArgumentException("Unknown report type.");
        };
        byte[] bytes = type.equals("attendance") ? reportService.attendanceCsv(period) : content.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + type + "-report.csv\"").body(bytes);
    }

        @PostMapping("/reports/backup")
        public String backup(RedirectAttributes flash) {
                Thread backupThread = new Thread(new ReportBackupTask(reportService), "attendance-report-backup");
                backupThread.start();
                flash.addFlashAttribute("message", "Attendance backup started in the background. Check the application log for completion.");
                return "redirect:/reports";
        }

    private String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}