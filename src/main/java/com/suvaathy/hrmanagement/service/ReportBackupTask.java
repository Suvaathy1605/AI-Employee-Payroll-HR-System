package com.suvaathy.hrmanagement.service;

import java.io.IOException;

public class ReportBackupTask implements Runnable {
    private static final System.Logger LOGGER = System.getLogger(ReportBackupTask.class.getName());
    private final ReportService reports;

    public ReportBackupTask(ReportService reports) { this.reports = reports; }

    @Override
    public void run() {
        try {
            LOGGER.log(System.Logger.Level.INFO, "Attendance backup written to {0}", reports.createAttendanceBackup());
        } catch (IOException | RuntimeException exception) {
            LOGGER.log(System.Logger.Level.ERROR, "Attendance backup failed: {0}", exception.getMessage());
        }
    }
}