package com.suvaathy.hrmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "period"}))
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    @NotBlank @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") @Column(nullable = false, length = 7)
    private String period;
    @NotNull @Min(1) @Max(31) @Column(name = "working_days", nullable = false)
    private Integer workingDays;
    @NotNull @Min(0) @Column(name = "present_days", nullable = false)
    private Integer presentDays;
    @NotNull @Min(0) @Column(name = "leave_days", nullable = false)
    private Integer leaveDays;
    @Column(name = "absent_days", nullable = false)
    private Integer absentDays;
    @Column(name = "attendance_percentage", nullable = false)
    private Double attendancePercentage;

    public Attendance() { }
    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Integer getWorkingDays() { return workingDays; }
    public void setWorkingDays(Integer workingDays) { this.workingDays = workingDays; }
    public Integer getPresentDays() { return presentDays; }
    public void setPresentDays(Integer presentDays) { this.presentDays = presentDays; }
    public Integer getLeaveDays() { return leaveDays; }
    public void setLeaveDays(Integer leaveDays) { this.leaveDays = leaveDays; }
    public Integer getAbsentDays() { return absentDays; }
    public void setAbsentDays(Integer absentDays) { this.absentDays = absentDays; }
    public Double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
}