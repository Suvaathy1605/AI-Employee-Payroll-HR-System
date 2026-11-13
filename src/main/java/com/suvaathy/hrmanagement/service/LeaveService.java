package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.model.LeaveRequest;
import com.suvaathy.hrmanagement.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveService {
    private final LeaveRequestRepository leaves;
    public LeaveService(LeaveRequestRepository leaves) { this.leaves = leaves; }
    public List<LeaveRequest> findAll() { return leaves.findByOrderByStartDateDesc(); }
    public LeaveRequest save(LeaveRequest leave) {
        if (leave.getEndDate().isBefore(leave.getStartDate())) throw new IllegalArgumentException("End date must be on or after the start date.");
        if (leave.getStatus() == null) leave.setStatus(LeaveRequest.Status.PENDING);
        return leaves.save(leave);
    }
    public void decide(Long id, LeaveRequest.Status status) {
        LeaveRequest leave = leaves.findById(id).orElseThrow(() -> new IllegalArgumentException("Leave request was not found."));
        leave.setStatus(status);
        leaves.save(leave);
    }
    public long approvedDaysThisYear(Long employeeId) {
        List<LeaveRequest> records = leaves.findByEmployeeIdOrderByStartDateDesc(employeeId);
        LocalDate start = LocalDate.now().withDayOfYear(1), end = start.plusYears(1).minusDays(1);
        return records.stream().filter(item -> item.getStatus() == LeaveRequest.Status.APPROVED)
                .filter(item -> !item.getStartDate().isAfter(end) && !item.getEndDate().isBefore(start))
                .mapToLong(LeaveRequest::getNumberOfDays).sum();
    }
}