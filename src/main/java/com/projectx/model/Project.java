package com.projectx.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long projectId;
    private String title;
    private String description;
    private String category;
    private String technologies;
    private Long ownerId;
    private int teamSize;
    private String requiredSkills;
    private String status;
    private String githubUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined Owner Information
    private String ownerName;
    private String ownerEmail;
    private String ownerDepartment;

    // Membership Statistics
    private int currentMembers;
    private int spotsRemaining;
    private List<ProjectMember> members;

    public Project() {
        this.status = "Open";
        this.teamSize = 4;
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getOwnerDepartment() { return ownerDepartment; }
    public void setOwnerDepartment(String ownerDepartment) { this.ownerDepartment = ownerDepartment; }

    public int getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(int currentMembers) { this.currentMembers = currentMembers; }

    public int getSpotsRemaining() { return spotsRemaining; }
    public void setSpotsRemaining(int spotsRemaining) { this.spotsRemaining = spotsRemaining; }

    public List<ProjectMember> getMembers() { return members; }
    public void setMembers(List<ProjectMember> members) { this.members = members; }
}
