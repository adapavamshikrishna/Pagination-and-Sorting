package com.company.employee.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.department.entity.Department;
import com.company.department.repository.DepartmentRepository;
import com.company.employee.dto.EmployeeRequestDTO;
import com.company.employee.dto.EmployeeResponseDTO;
import com.company.employee.entity.Employee;
import com.company.employee.mapper.EmployeeMapper;
import com.company.employee.repository.EmployeeRepository;
import com.company.exception.ResourceNotFoundException;
import com.company.exception.DuplicateResourceException;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public EmployeeResponseDTO create(EmployeeRequestDTO request) {
        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Employee email already exists");
        }
        Department department = findDepartment(request.getDepartmentId());
        Employee saved = employeeRepository.save(EmployeeMapper.toEntity(request, department));
        return EmployeeMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(EmployeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getById(Long id) {
        return EmployeeMapper.toResponse(findEmployee(id));
    }

    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO request) {
        Employee employee = findEmployee(id);
        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Employee email already exists");
        }
        Department department = findDepartment(request.getDepartmentId());
        EmployeeMapper.updateEntity(employee, request, department);
        return EmployeeMapper.toResponse(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        employeeRepository.delete(findEmployee(id));
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }
}
