package com.suvaathy.hrmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "leaves")
public class LeaveRequest {
    public enum Status { PENDING, APPROVED, REJECTED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    @NotBlank @Column(name = "leave_type", nullable = false, length = 40)
    private String leaveType;
    @NotNull @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @NotNull @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @NotBlank @Column(nullable = false, length = 500)
    private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12)
    private Status status = Status.PENDING;

    public LeaveRequest() { }
    public LeaveRequest(Employee employee, String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this.employee = employee; this.leaveType = leaveType; this.startDate = startDate; this.endDate = endDate; this.reason = reason;
    }
    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public long getNumberOfDays() {
        return startDate == null || endDate == null || endDate.isBefore(startDate) ? 0 : java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}