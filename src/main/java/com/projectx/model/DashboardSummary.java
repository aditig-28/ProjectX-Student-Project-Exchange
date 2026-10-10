package com.projectx.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DashboardSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    // Platform-wide counts
    private int totalStudents;
    private int totalProjects;
    private int openProjects;
    private int activeCollaborations;
    private int totalRequests;

    // Current Student specific stats
    private Long currentStudentId;
    private Student currentStudent;
    private int myProjectsCount;
    private int myMembershipsCount;
    private int incomingRequestsCount;
    private int outgoingRequestsCount;

    // Recent data lists for dashboard
    private List<Project> myProjects;
    private List<JoinRequest> incomingRequests;
    private List<JoinRequest> outgoingRequests;
    private List<Project> joinedProjects;
    private Map<String, Integer> categoryDistribution;

    public DashboardSummary() {}

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getTotalProjects() { return totalProjects; }
    public void setTotalProjects(int totalProjects) { this.totalProjects = totalProjects; }

    public int getOpenProjects() { return openProjects; }
    public void setOpenProjects(int openProjects) { this.openProjects = openProjects; }

    public int getActiveCollaborations() { return activeCollaborations; }
    public void setActiveCollaborations(int activeCollaborations) { this.activeCollaborations = activeCollaborations; }

    public int getTotalRequests() { return totalRequests; }
    public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }

    public Long getCurrentStudentId() { return currentStudentId; }
    public void setCurrentStudentId(Long currentStudentId) { this.currentStudentId = currentStudentId; }

    public Student getCurrentStudent() { return currentStudent; }
    public void setCurrentStudent(Student currentStudent) { this.currentStudent = currentStudent; }

    public int getMyProjectsCount() { return myProjectsCount; }
    public void setMyProjectsCount(int myProjectsCount) { this.myProjectsCount = myProjectsCount; }

    public int getMyMembershipsCount() { return myMembershipsCount; }
    public void setMyMembershipsCount(int myMembershipsCount) { this.myMembershipsCount = myMembershipsCount; }

    public int getIncomingRequestsCount() { return incomingRequestsCount; }
    public void setIncomingRequestsCount(int incomingRequestsCount) { this.incomingRequestsCount = incomingRequestsCount; }

    public int getOutgoingRequestsCount() { return outgoingRequestsCount; }
    public void setOutgoingRequestsCount(int outgoingRequestsCount) { this.outgoingRequestsCount = outgoingRequestsCount; }

    public List<Project> getMyProjects() { return myProjects; }
    public void setMyProjects(List<Project> myProjects) { this.myProjects = myProjects; }

    public List<JoinRequest> getIncomingRequests() { return incomingRequests; }
    public void setIncomingRequests(List<JoinRequest> incomingRequests) { this.incomingRequests = incomingRequests; }

    public List<JoinRequest> getOutgoingRequests() { return outgoingRequests; }
    public void setOutgoingRequests(List<JoinRequest> outgoingRequests) { this.outgoingRequests = outgoingRequests; }

    public List<Project> getJoinedProjects() { return joinedProjects; }
    public void setJoinedProjects(List<Project> joinedProjects) { this.joinedProjects = joinedProjects; }

    public Map<String, Integer> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Integer> categoryDistribution) { this.categoryDistribution = categoryDistribution; }
}
