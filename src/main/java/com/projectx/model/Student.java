package com.projectx.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long studentId;
    private String fullName;
    private String email;
    private String department;
    private String yearOfStudy;
    private String skills;
    private String bio;
    private String avatarUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Derived analytics counters
    private int projectsCreated;
    private int teamsJoined;
    private int pendingRequestsSent;

    public Student() {}

    public Student(Long studentId, String fullName, String email, String department, 
                   String yearOfStudy, String skills, String bio, String avatarUrl) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.skills = skills;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getProjectsCreated() { return projectsCreated; }
    public void setProjectsCreated(int projectsCreated) { this.projectsCreated = projectsCreated; }

    public int getTeamsJoined() { return teamsJoined; }
    public void setTeamsJoined(int teamsJoined) { this.teamsJoined = teamsJoined; }

    public int getPendingRequestsSent() { return pendingRequestsSent; }
    public void setPendingRequestsSent(int pendingRequestsSent) { this.pendingRequestsSent = pendingRequestsSent; }
}
