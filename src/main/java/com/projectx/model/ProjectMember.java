package com.projectx.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ProjectMember implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long memberId;
    private Long projectId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentDepartment;
    private String studentSkills;
    private String studentAvatar;
    private String role;
    private Timestamp joinedAt;

    public ProjectMember() {}

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentDepartment() { return studentDepartment; }
    public void setStudentDepartment(String studentDepartment) { this.studentDepartment = studentDepartment; }

    public String getStudentSkills() { return studentSkills; }
    public void setStudentSkills(String studentSkills) { this.studentSkills = studentSkills; }

    public String getStudentAvatar() { return studentAvatar; }
    public void setStudentAvatar(String studentAvatar) { this.studentAvatar = studentAvatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }
}
