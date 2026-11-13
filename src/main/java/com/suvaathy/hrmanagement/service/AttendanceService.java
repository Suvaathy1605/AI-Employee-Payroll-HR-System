package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.model.Attendance;
import com.suvaathy.hrmanagement.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendance;
    public AttendanceService(AttendanceRepository attendance) { this.attendance = attendance; }
    public List<Attendance> findAll() { return attendance.findByOrderByPeriodDesc(); }
    public List<Attendance> history(Long employeeId) { return attendance.findByEmployeeIdOrderByPeriodDesc(employeeId); }
    public Attendance save(Attendance record) {
        if (record.getPresentDays() + record.getLeaveDays() > record.getWorkingDays()) {
            throw new IllegalArgumentException("Present days plus leave days cannot exceed working days.");
        }
        int absent = record.getWorkingDays() - record.getPresentDays() - record.getLeaveDays();
        record.setAbsentDays(absent);
        record.setAttendancePercentage(Math.round(record.getPresentDays() * 10000.0 / record.getWorkingDays()) / 100.0);
        return attendance.save(record);
    }
    public void delete(Long id) { attendance.deleteById(id); }
}