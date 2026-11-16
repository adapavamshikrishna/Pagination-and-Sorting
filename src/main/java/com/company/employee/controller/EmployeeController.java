package com.company.employee.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.company.employee.dto.EmployeeRequestDTO;
import com.company.employee.dto.EmployeeResponseDTO;
import com.company.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO create(@Valid @RequestBody EmployeeRequestDTO request) {
        return employeeService.create(request);
    }

    // Example: GET /api/employees?page=0&size=5&sort=name,asc
    @GetMapping
    public Page<EmployeeResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        if (page < 0) {
            throw new com.company.exception.BadRequestException("Page index must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new com.company.exception.BadRequestException("Page size must be between 1 and 100");
        }

        Sort sortOrder = Sort.by(
                "desc".equalsIgnoreCase(sort.length > 1 ? sort[1] : "asc")
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return employeeService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public EmployeeResponseDTO getById(@PathVariable Long id) {
        return employeeService.getById(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponseDTO update(@PathVariable Long id,
                                      @Valid @RequestBody EmployeeRequestDTO request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
}
