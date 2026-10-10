package com.projectx.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class JoinRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long requestId;
    private Long projectId;
    private String projectTitle;
    private String projectCategory;
    private Long projectOwnerId;

    private Long studentId;
    private String applicantName;
    private String applicantEmail;
    private String applicantDepartment;
    private String applicantSkills;
    private String applicantAvatar;

    private String message;
    private String status; // Pending, Accepted, Rejected
    private Timestamp createdAt;
    private Timestamp respondedAt;

    public JoinRequest() {
        this.status = "Pending";
    }

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getProjectCategory() { return projectCategory; }
    public void setProjectCategory(String projectCategory) { this.projectCategory = projectCategory; }

    public Long getProjectOwnerId() { return projectOwnerId; }
    public void setProjectOwnerId(Long projectOwnerId) { this.projectOwnerId = projectOwnerId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

    public String getApplicantDepartment() { return applicantDepartment; }
    public void setApplicantDepartment(String applicantDepartment) { this.applicantDepartment = applicantDepartment; }

    public String getApplicantSkills() { return applicantSkills; }
    public void setApplicantSkills(String applicantSkills) { this.applicantSkills = applicantSkills; }

    public String getApplicantAvatar() { return applicantAvatar; }
    public void setApplicantAvatar(String applicantAvatar) { this.applicantAvatar = applicantAvatar; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getRespondedAt() { return respondedAt; }
    public void setRespondedAt(Timestamp respondedAt) { this.respondedAt = respondedAt; }
}
