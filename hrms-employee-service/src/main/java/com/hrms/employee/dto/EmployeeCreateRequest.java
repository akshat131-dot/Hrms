package com.hrms.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class EmployeeCreateRequest {

    @NotBlank
    private String employeeCode;

    @NotBlank
    private String firstName;

    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private Long managerId;

    @NotNull
    private EmployeeJobDetailsDto jobDetails;

    @NotNull
    private EmployeeContactDto contact;

    private List<EmployeeDocumentDto> documents;

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
