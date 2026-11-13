package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.model.Attendance;
import com.suvaathy.hrmanagement.repository.AttendanceRepository;
import com.suvaathy.hrmanagement.exception.DatabaseOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class ReportService {
    private final DataSource dataSource;
    private final AttendanceRepository attendanceRepository;
    public ReportService(DataSource dataSource, AttendanceRepository attendanceRepository) {
        this.dataSource = dataSource; this.attendanceRepository = attendanceRepository;
    }

    public byte[] attendanceCsv(String period) {
        String sql = "SELECT e.employee_code, e.name, a.period, a.working_days, a.present_days, a.absent_days, a.leave_days, a.attendance_percentage "
                + "FROM attendance a JOIN employees e ON e.id = a.employee_id WHERE (? IS NULL OR a.period = ?) ORDER BY a.period DESC, e.name";
        StringBuilder csv = new StringBuilder("Employee ID,Employee Name,Period,Working Days,Present Days,Absent Days,Leave Days,Attendance %\n");
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, period == null || period.isBlank() ? null : period);
            statement.setString(2, period == null || period.isBlank() ? null : period);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    csv.append(escape(rows.getString("employee_code"))).append(',')
                            .append(escape(rows.getString("name"))).append(',')
                            .append(rows.getString("period")).append(',')
                            .append(rows.getInt("working_days")).append(',').append(rows.getInt("present_days")).append(',')
                            .append(rows.getInt("absent_days")).append(',').append(rows.getInt("leave_days")).append(',')
                            .append(rows.getDouble("attendance_percentage")).append('\n');
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseOperationException("Attendance report could not be generated. Check the database connection.", exception);
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public synchronized Path writeBackup(List<Attendance> rows) throws IOException {
        Path directory = Path.of("backups");
        Files.createDirectories(directory);
        Path file = directory.resolve("attendance-backup.csv");
        StringBuilder content = new StringBuilder("Employee ID,Period,Present Days,Attendance %\n");
        rows.forEach(row -> content.append(row.getEmployee().getEmployeeCode()).append(',')
                .append(row.getPeriod()).append(',').append(row.getPresentDays()).append(',')
                .append(row.getAttendancePercentage()).append('\n'));
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    @Transactional(readOnly = true)
    public Path createAttendanceBackup() throws IOException {
        return writeBackup(attendanceRepository.findByOrderByPeriodDesc());
    }

    private String escape(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}