package com.suvaathy.hrmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 60)
    private String username;
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    protected Admin() { }
    public Admin(String username, String passwordHash) { this.username = username; this.passwordHash = passwordHash; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}