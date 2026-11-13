package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.model.Employee;
import com.suvaathy.hrmanagement.repository.*;
import com.suvaathy.hrmanagement.exception.DuplicateEmployeeException;
import com.suvaathy.hrmanagement.exception.EmployeeNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employees;
    private final AttendanceRepository attendance;
    private final LeaveRequestRepository leaves;
    private final PayrollRepository payroll;

    public EmployeeService(EmployeeRepository employees, AttendanceRepository attendance,
                          LeaveRequestRepository leaves, PayrollRepository payroll) {
        this.employees = employees; this.attendance = attendance; this.leaves = leaves; this.payroll = payroll;
    }

    public List<Employee> search(String query) {
        return search(query, null, "name", "asc");
        }

        public List<Employee> search(String query, Long departmentId, String sort, String direction) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(java.util.Locale.ROOT);
        Comparator<Employee> ordering = switch (sort) {
            case "salary" -> Comparator.comparing(Employee::getBasicSalary, Comparator.nullsLast(Double::compareTo));
            case "joiningDate" -> Comparator.comparing(Employee::getJoiningDate, Comparator.nullsLast(java.time.LocalDate::compareTo));
            case "employeeCode" -> Comparator.comparing(Employee::getEmployeeCode, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER);
        };
        if ("desc".equalsIgnoreCase(direction)) ordering = ordering.reversed();
        return employees.findAll().stream()
            .filter(employee -> normalizedQuery.isEmpty()
                || employee.getName().toLowerCase(java.util.Locale.ROOT).contains(normalizedQuery)
                || employee.getEmployeeCode().toLowerCase(java.util.Locale.ROOT).contains(normalizedQuery)
                || employee.getEmail().toLowerCase(java.util.Locale.ROOT).contains(normalizedQuery))
            .filter(employee -> departmentId == null || employee.getDepartment() != null
                && departmentId.equals(employee.getDepartment().getId()))
            .sorted(ordering)
            .toList();
    }

    public Employee findById(Long id) {
        return employees.findById(id).orElseThrow(() -> new EmployeeNotFoundException("Employee was not found."));
    }

    public Employee save(Employee employee) {
        boolean duplicateCode = employees.existsByEmployeeCodeIgnoreCase(employee.getEmployeeCode())
                && (employee.getId() == null || !employees.findById(employee.getId()).map(old -> old.getEmployeeCode().equalsIgnoreCase(employee.getEmployeeCode())).orElse(false));
        boolean duplicateEmail = employees.existsByEmailIgnoreCase(employee.getEmail())
                && (employee.getId() == null || !employees.findById(employee.getId()).map(old -> old.getEmail().equalsIgnoreCase(employee.getEmail())).orElse(false));
        if (duplicateCode || duplicateEmail) throw new DuplicateEmployeeException("Employee ID and email must be unique.");
        return employees.save(employee);
    }

    @Transactional
    public void delete(Long id) {
        if (attendance.existsByEmployeeId(id) || leaves.existsByEmployeeId(id) || payroll.existsByEmployeeId(id)) {
            throw new IllegalStateException("This employee has HR history and cannot be deleted.");
        }
        employees.delete(findById(id));
    }
}