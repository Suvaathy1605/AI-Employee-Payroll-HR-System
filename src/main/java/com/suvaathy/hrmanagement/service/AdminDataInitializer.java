package com.suvaathy.hrmanagement.service;

import com.suvaathy.hrmanagement.model.Admin;
import com.suvaathy.hrmanagement.repository.AdminRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminDataInitializer implements ApplicationRunner {
    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminDataInitializer(AdminRepository admins, PasswordEncoder passwordEncoder,
                                @org.springframework.beans.factory.annotation.Value("${app.admin.username}") String username,
                                @org.springframework.beans.factory.annotation.Value("${app.admin.password}") String password) {
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalStateException("Set ADMIN_USERNAME and ADMIN_PASSWORD to initialize the admin account.");
        }
        if (admins.findByUsername(username).isEmpty()) {
            admins.save(new Admin(username, passwordEncoder.encode(password)));
        }
    }
}