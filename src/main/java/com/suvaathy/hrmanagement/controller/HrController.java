package com.suvaathy.hrmanagement.controller;

import com.suvaathy.hrmanagement.model.*;
import com.suvaathy.hrmanagement.repository.*;
import com.suvaathy.hrmanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
public class HrController {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final PayrollService payrollService;
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final AttendanceRepository attendance;
    private final LeaveRequestRepository leaves;
    private final HRAnalyticsService analytics;

    public HrController(EmployeeService employeeService, DepartmentService departmentService,
                        AttendanceService attendanceService, LeaveService leaveService, PayrollService payrollService,
                        EmployeeRepository employees, DepartmentRepository departments, AttendanceRepository attendance,
                        LeaveRequestRepository leaves, HRAnalyticsService analytics) {
        this.employeeService = employeeService; this.departmentService = departmentService;
        this.attendanceService = attendanceService; this.leaveService = leaveService; this.payrollService = payrollService;
        this.employees = employees; this.departments = departments; this.attendance = attendance;
        this.leaves = leaves; this.analytics = analytics;
    }

    @GetMapping("/employees")
    public String employees(@RequestParam(defaultValue = "") String q,
                            @RequestParam(required = false) Long departmentId,
                            @RequestParam(defaultValue = "name") String sort,
                            @RequestParam(defaultValue = "asc") String direction, Model model) {
        model.addAttribute("employees", employeeService.search(q, departmentId, sort, direction));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("q", q); model.addAttribute("departmentId", departmentId);
        model.addAttribute("sort", sort); model.addAttribute("direction", direction);
        return "employees";
    }
    @GetMapping("/employees/new")
    public String newEmployee(Model model) { model.addAttribute("employee", new Employee()); model.addAttribute("departments", departmentService.findAll()); return "employee-form"; }
    @GetMapping("/employees/{id}/edit")
    public String editEmployee(@PathVariable Long id, Model model) { model.addAttribute("employee", employeeService.findById(id)); model.addAttribute("departments", departmentService.findAll()); return "employee-form"; }
    @PostMapping("/employees/save")
    public String saveEmployee(@Valid @ModelAttribute Employee employee, BindingResult result,
                               @RequestParam(required = false) Long departmentId, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) { model.addAttribute("departments", departmentService.findAll()); return "employee-form"; }
        employee.setDepartment(departmentId == null ? null : departmentService.findById(departmentId));
        employeeService.save(employee); flash.addFlashAttribute("message", "Employee saved."); return "redirect:/employees";
    }
    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes flash) { employeeService.delete(id); flash.addFlashAttribute("message", "Employee deleted."); return "redirect:/employees"; }

    @GetMapping("/departments")
    public String departments(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("departments", departmentService.search(q));
        model.addAttribute("q", q);
        model.addAttribute("employeeCounts", employees.findAll().stream().filter(e -> e.getDepartment() != null)
                .collect(java.util.stream.Collectors.groupingBy(e -> e.getDepartment().getId(), java.util.stream.Collectors.counting())));
        return "departments";
    }
    @GetMapping("/departments/new")
    public String newDepartment(Model model) { model.addAttribute("department", new Department()); return "department-form"; }
    @GetMapping("/departments/{id}/edit")
    public String editDepartment(@PathVariable Long id, Model model) { model.addAttribute("department", departmentService.findById(id)); return "department-form"; }
    @PostMapping("/departments/save")
    public String saveDepartment(@Valid @ModelAttribute Department department, BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) return "department-form";
        departmentService.save(department); flash.addFlashAttribute("message", "Department saved."); return "redirect:/departments";
    }
    @PostMapping("/departments/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes flash) { departmentService.delete(id); flash.addFlashAttribute("message", "Department deleted."); return "redirect:/departments"; }

    @GetMapping("/attendance")
    public String attendance(Model model) { model.addAttribute("attendanceRecords", attendanceService.findAll()); model.addAttribute("employees", employees.findAll()); model.addAttribute("attendance", new Attendance()); return "attendance"; }
    @PostMapping("/attendance/save")
    public String saveAttendance(@Valid @ModelAttribute Attendance record, BindingResult result, @RequestParam Long employeeId,
                                 Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("employees", employees.findAll());
            model.addAttribute("attendanceRecords", attendanceService.findAll());
            model.addAttribute("message", result.getAllErrors().get(0).getDefaultMessage());
            return "attendance";
        }
        if (record.getPresentDays() + record.getLeaveDays() > record.getWorkingDays()) {
            model.addAttribute("employees", employees.findAll());
            model.addAttribute("attendanceRecords", attendanceService.findAll());
            model.addAttribute("message", "Present days plus leave days cannot exceed working days.");
            return "attendance";
        }
        record.setEmployee(employeeService.findById(employeeId)); attendanceService.save(record); flash.addFlashAttribute("message", "Attendance recorded."); return "redirect:/attendance";
    }

    @GetMapping("/leave")
    public String leave(Model model) { model.addAttribute("leaveRequests", leaveService.findAll()); model.addAttribute("employees", employees.findAll()); model.addAttribute("leaveRequest", new LeaveRequest()); return "leave"; }
    @PostMapping("/leave/apply")
    public String applyLeave(@Valid @ModelAttribute LeaveRequest leave, BindingResult result, @RequestParam Long employeeId,
                             Model model, RedirectAttributes flash) {
        if (result.hasErrors() || leave.getEndDate().isBefore(leave.getStartDate())) {
            model.addAttribute("employees", employees.findAll());
            model.addAttribute("leaveRequests", leaveService.findAll());
            model.addAttribute("message", result.hasErrors() ? result.getAllErrors().get(0).getDefaultMessage()
                    : "End date must be on or after the start date.");
            return "leave";
        }
        leave.setEmployee(employeeService.findById(employeeId)); leave.setStatus(LeaveRequest.Status.PENDING); leaveService.save(leave); flash.addFlashAttribute("message", "Leave request submitted."); return "redirect:/leave";
    }
    @PostMapping("/leave/{id}/decision")
    public String decideLeave(@PathVariable Long id, @RequestParam LeaveRequest.Status status, RedirectAttributes flash) {
        leaveService.decide(id, status); flash.addFlashAttribute("message", "Leave request updated."); return "redirect:/leave";
    }

    @GetMapping("/payroll")
    public String payroll(Model model) { model.addAttribute("payrollRecords", payrollService.findAll()); model.addAttribute("employees", employees.findAll()); model.addAttribute("period", LocalDate.now().toString().substring(0, 7)); return "payroll"; }
    @PostMapping("/payroll/generate")
    public String generatePayroll(@RequestParam Long employeeId, @RequestParam String period, @RequestParam(defaultValue = "0") double bonus, RedirectAttributes flash) {
        payrollService.generate(employeeService.findById(employeeId), period, bonus); flash.addFlashAttribute("message", "Payroll generated."); return "redirect:/payroll";
    }
    @GetMapping("/salary-slips")
    public String salarySlips(Model model) { model.addAttribute("payrollRecords", payrollService.findAll()); return "salary-slips"; }
    @GetMapping("/salary-slips/{id}")
    public String salarySlip(@PathVariable Long id, Model model) { model.addAttribute("payroll", payrollService.findById(id)); return "salary-slip"; }

    @GetMapping("/analytics")
    public String analytics(Model model) { model.addAttribute("analytics", analytics.analyzeAll()); return "analytics"; }
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("employees", employees.findAll()); model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("attendanceRecords", attendanceService.findAll()); model.addAttribute("leaveRequests", leaveService.findAll());
        model.addAttribute("payrollRecords", payrollService.findAll()); return "reports";
    }
    @GetMapping("/about")
    public String about() { return "about"; }
}