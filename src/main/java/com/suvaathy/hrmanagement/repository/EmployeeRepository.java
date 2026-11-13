package com.suvaathy.hrmanagement.repository;

import com.suvaathy.hrmanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Override
    @EntityGraph(attributePaths = "department")
    List<Employee> findAll();

    @Override
    @EntityGraph(attributePaths = "department")
    Optional<Employee> findById(Long id);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);
    boolean existsByEmailIgnoreCase(String email);
    List<Employee> findByNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCase(String name, String employeeCode);
    List<Employee> findByDepartmentId(Long departmentId);
}