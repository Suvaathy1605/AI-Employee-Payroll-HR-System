package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.model.Department;
import com.suvaathy.hrmanagement.repository.DepartmentRepository;
import com.suvaathy.hrmanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departments;
    private final EmployeeRepository employees;
    public DepartmentService(DepartmentRepository departments, EmployeeRepository employees) {
        this.departments = departments; this.employees = employees;
    }
    public List<Department> findAll() { return departments.findAll(org.springframework.data.domain.Sort.by("name")); }
    public List<Department> search(String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return findAll().stream().filter(department -> normalizedQuery.isEmpty()
                || department.getName().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                || department.getDescription() != null
                && department.getDescription().toLowerCase(Locale.ROOT).contains(normalizedQuery)).toList();
    }
    public Department findById(Long id) { return departments.findById(id).orElseThrow(() -> new IllegalArgumentException("Department was not found.")); }
    public Department save(Department department) { return departments.save(department); }
    public void delete(Long id) {
        if (!employees.findByDepartmentId(id).isEmpty()) throw new IllegalStateException("Move employees to another department before deleting this one.");
        departments.delete(findById(id));
    }
}