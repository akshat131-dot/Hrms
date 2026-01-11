package com.hrms.employee.service;

import com.hrms.common.exception.BusinessException;
import com.hrms.employee.dto.*;
import com.hrms.employee.entity.*;
import com.hrms.employee.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new BusinessException("EMPLOYEE_CODE_EXISTS", "Employee code already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new BusinessException("MANAGER_NOT_FOUND", "Manager not found"));
            employee.setManager(manager);
        }

        EmployeeJobDetails job = new EmployeeJobDetails();
        job.setDepartment(request.getJobDetails().getDepartment());
        job.setDesignation(request.getJobDetails().getDesignation());
        job.setJobTitle(request.getJobDetails().getJobTitle());
        job.setEmploymentType(request.getJobDetails().getEmploymentType());
        job.setDateOfJoining(request.getJobDetails().getDateOfJoining());
        job.setWorkLocation(request.getJobDetails().getWorkLocation());
        employee.setJobDetails(job);

        EmployeeContact contact = new EmployeeContact();
        contact.setOfficialEmail(request.getContact().getOfficialEmail());
        contact.setPersonalEmail(request.getContact().getPersonalEmail());
        contact.setPhoneNumber(request.getContact().getPhoneNumber());
        contact.setAddressLine1(request.getContact().getAddressLine1());
        contact.setAddressLine2(request.getContact().getAddressLine2());
        contact.setCity(request.getContact().getCity());
        contact.setState(request.getContact().getState());
        contact.setPostalCode(request.getContact().getPostalCode());
        contact.setEmergencyContactName(request.getContact().getEmergencyContactName());
        contact.setEmergencyContactPhone(request.getContact().getEmergencyContactPhone());
        employee.setContact(contact);

        if (request.getDocuments() != null) {
            List<EmployeeDocument> docs = new ArrayList<>();
            for (EmployeeDocumentDto dto : request.getDocuments()) {
                EmployeeDocument doc = new EmployeeDocument();
                doc.setDocumentType(dto.getDocumentType());
                doc.setDocumentName(dto.getDocumentName());
                doc.setDocumentUrl(dto.getDocumentUrl());
                docs.add(doc);
            }
            employee.setDocuments(new java.util.HashSet<>(docs));
        }

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found"));

        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            employee.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            employee.setGender(request.getGender());
        }
        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new BusinessException("MANAGER_NOT_FOUND", "Manager not found"));
            employee.setManager(manager);
        }

        if (request.getJobDetails() != null) {
            EmployeeJobDetails job = employee.getJobDetails();
            if (job == null) {
                job = new EmployeeJobDetails();
                job.setEmployee(employee);
            }
            if (request.getJobDetails().getDepartment() != null) {
                job.setDepartment(request.getJobDetails().getDepartment());
            }
            if (request.getJobDetails().getDesignation() != null) {
                job.setDesignation(request.getJobDetails().getDesignation());
            }
            if (request.getJobDetails().getJobTitle() != null) {
                job.setJobTitle(request.getJobDetails().getJobTitle());
            }
            if (request.getJobDetails().getEmploymentType() != null) {
                job.setEmploymentType(request.getJobDetails().getEmploymentType());
            }
            if (request.getJobDetails().getDateOfJoining() != null) {
                job.setDateOfJoining(request.getJobDetails().getDateOfJoining());
            }
            if (request.getJobDetails().getWorkLocation() != null) {
                job.setWorkLocation(request.getJobDetails().getWorkLocation());
            }
            employee.setJobDetails(job);
        }

        if (request.getContact() != null) {
            EmployeeContact contact = employee.getContact();
            if (contact == null) {
                contact = new EmployeeContact();
                contact.setEmployee(employee);
            }
            if (request.getContact().getOfficialEmail() != null) {
                contact.setOfficialEmail(request.getContact().getOfficialEmail());
            }
            if (request.getContact().getPersonalEmail() != null) {
                contact.setPersonalEmail(request.getContact().getPersonalEmail());
            }
            if (request.getContact().getPhoneNumber() != null) {
                contact.setPhoneNumber(request.getContact().getPhoneNumber());
            }
            if (request.getContact().getAddressLine1() != null) {
                contact.setAddressLine1(request.getContact().getAddressLine1());
            }
            if (request.getContact().getAddressLine2() != null) {
                contact.setAddressLine2(request.getContact().getAddressLine2());
            }
            if (request.getContact().getCity() != null) {
                contact.setCity(request.getContact().getCity());
            }
            if (request.getContact().getState() != null) {
                contact.setState(request.getContact().getState());
            }
            if (request.getContact().getPostalCode() != null) {
                contact.setPostalCode(request.getContact().getPostalCode());
            }
            if (request.getContact().getEmergencyContactName() != null) {
                contact.setEmergencyContactName(request.getContact().getEmergencyContactName());
            }
            if (request.getContact().getEmergencyContactPhone() != null) {
                contact.setEmergencyContactPhone(request.getContact().getEmergencyContactPhone());
            }
            employee.setContact(contact);
        }

        if (request.getDocuments() != null) {
            employee.getDocuments().clear();
            for (EmployeeDocumentDto dto : request.getDocuments()) {
                EmployeeDocument doc = new EmployeeDocument();
                doc.setDocumentType(dto.getDocumentType());
                doc.setDocumentName(dto.getDocumentName());
                doc.setDocumentUrl(dto.getDocumentUrl());
                employee.getDocuments().add(doc);
                doc.setEmployee(employee);
            }
        }

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found"));
        return mapToResponse(employee);
    }

    public List<EmployeeResponse> searchEmployees(String department, String role, String keyword) {
        Specification<Employee> spec = Specification.where(
                (root, query, cb) -> cb.isFalse(root.get("deleted"))
        );

        if (department != null && !department.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("jobDetails").get("department"), department));
        }
        if (role != null && !role.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("jobDetails").get("designation"), role));
        }
        if (keyword != null && !keyword.isEmpty()) {
            String like = "%" + keyword.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like),
                    cb.like(cb.lower(root.get("employeeCode")), like)
            ));
        }

        return employeeRepository.findAll(spec)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void softDeleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found"));
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse resp = new EmployeeResponse();
        resp.setId(employee.getId());
        resp.setEmployeeCode(employee.getEmployeeCode());
        resp.setFirstName(employee.getFirstName());
        resp.setLastName(employee.getLastName());
        resp.setDateOfBirth(employee.getDateOfBirth());
        resp.setGender(employee.getGender());
        resp.setDeleted(employee.isDeleted());
        resp.setCreatedAt(employee.getCreatedAt());
        resp.setUpdatedAt(employee.getUpdatedAt());

        if (employee.getManager() != null) {
            resp.setManagerId(employee.getManager().getId());
            String managerName = employee.getManager().getFirstName();
            if (employee.getManager().getLastName() != null) {
                managerName = managerName + " " + employee.getManager().getLastName();
            }
            resp.setManagerName(managerName);
        }

        if (employee.getJobDetails() != null) {
            EmployeeJobDetailsDto jd = new EmployeeJobDetailsDto();
            jd.setDepartment(employee.getJobDetails().getDepartment());
            jd.setDesignation(employee.getJobDetails().getDesignation());
            jd.setJobTitle(employee.getJobDetails().getJobTitle());
            jd.setEmploymentType(employee.getJobDetails().getEmploymentType());
            jd.setDateOfJoining(employee.getJobDetails().getDateOfJoining());
            jd.setWorkLocation(employee.getJobDetails().getWorkLocation());
            resp.setJobDetails(jd);
        }

        if (employee.getContact() != null) {
            EmployeeContactDto cd = new EmployeeContactDto();
            cd.setOfficialEmail(employee.getContact().getOfficialEmail());
            cd.setPersonalEmail(employee.getContact().getPersonalEmail());
            cd.setPhoneNumber(employee.getContact().getPhoneNumber());
            cd.setAddressLine1(employee.getContact().getAddressLine1());
            cd.setAddressLine2(employee.getContact().getAddressLine2());
            cd.setCity(employee.getContact().getCity());
            cd.setState(employee.getContact().getState());
            cd.setPostalCode(employee.getContact().getPostalCode());
            cd.setEmergencyContactName(employee.getContact().getEmergencyContactName());
            cd.setEmergencyContactPhone(employee.getContact().getEmergencyContactPhone());
            resp.setContact(cd);
        }

        if (employee.getDocuments() != null) {
            List<EmployeeDocumentDto> docs = employee.getDocuments().stream().map(doc -> {
                EmployeeDocumentDto dto = new EmployeeDocumentDto();
                dto.setId(doc.getId());
                dto.setDocumentType(doc.getDocumentType());
                dto.setDocumentName(doc.getDocumentName());
                dto.setDocumentUrl(doc.getDocumentUrl());
                dto.setUploadedAt(doc.getUploadedAt());
                return dto;
            }).collect(java.util.stream.Collectors.toList());
            resp.setDocuments(docs);
        }

        return resp;
    }
}
