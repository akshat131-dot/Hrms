package com.hrms.leave.service;

import com.hrms.leave.entity.LeaveType;
import com.hrms.leave.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository repo;

    public LeaveType create(LeaveType t) {
        return repo.save(t);
    }

    public List<LeaveType> getAll() {
        return repo.findAll();
    }

    public LeaveType getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public LeaveType update(Long id, LeaveType data) {
        LeaveType ex = getById(id);
        ex.setName(data.getName());
        ex.setYearlyAccrual(data.getYearlyAccrual());
        ex.setCarryForward(data.isCarryForward());
        return repo.save(ex);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
