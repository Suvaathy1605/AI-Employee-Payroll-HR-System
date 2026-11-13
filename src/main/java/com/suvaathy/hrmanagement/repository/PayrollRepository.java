package com.suvaathy.hrmanagement.repository;

import com.suvaathy.hrmanagement.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    @Override
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<Payroll> findAll();

    @Override
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    Optional<Payroll> findById(Long id);

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<Payroll> findByOrderByPeriodDesc();

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<Payroll> findByEmployeeIdOrderByPeriodDesc(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
}