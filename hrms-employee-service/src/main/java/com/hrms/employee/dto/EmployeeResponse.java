package com.hrms.employee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private Long managerId;
    private String managerName;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EmployeeJobDetailsDto jobDetails;
    private EmployeeContactDto contact;
    private List<EmployeeDocumentDto> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public EmployeeJobDetailsDto getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(EmployeeJobDetailsDto jobDetails) {
        this.jobDetails = jobDetails;
    }

    public EmployeeContactDto getContact() {
        return contact;
    }

    public void setContact(EmployeeContactDto contact) {
        this.contact = contact;
    }

    public List<EmployeeDocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<EmployeeDocumentDto> documents) {
        this.documents = documents;
    }
}
