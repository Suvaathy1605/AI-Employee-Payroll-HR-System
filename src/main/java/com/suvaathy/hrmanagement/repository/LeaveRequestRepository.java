package com.suvaathy.hrmanagement.repository;

import com.suvaathy.hrmanagement.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    @Override
    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<LeaveRequest> findAll();

    @EntityGraph(attributePaths = {"employee", "employee.department"})
    List<LeaveRequest> findByOrderByStartDateDesc();
    List<LeaveRequest> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
    long countByStatus(LeaveRequest.Status status);
    boolean existsByEmployeeId(Long employeeId);
    long countByEmployeeIdAndStatusAndStartDateBetween(Long employeeId, LeaveRequest.Status status,
                                                       java.time.LocalDate from, java.time.LocalDate to);
}