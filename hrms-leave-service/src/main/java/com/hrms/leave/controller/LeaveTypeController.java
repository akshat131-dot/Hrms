package com.hrms.leave.controller;

import com.hrms.leave.entity.LeaveType;
import com.hrms.leave.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave/type")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService service;

    @PostMapping
    public LeaveType create(@RequestBody LeaveType type) {
        return service.create(type);
    }

    @GetMapping
    public List<LeaveType> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public LeaveType update(@PathVariable Long id, @RequestBody LeaveType data) {
        return service.update(id, data);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted";
    }
}
