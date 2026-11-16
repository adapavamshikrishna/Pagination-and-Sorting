package com.company.department.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.department.dto.DepartmentRequestDTO;
import com.company.department.dto.DepartmentResponseDTO;
import com.company.department.entity.Department;
import com.company.department.mapper.DepartmentMapper;
import com.company.department.repository.DepartmentRepository;
import com.company.exception.ResourceNotFoundException;
import com.company.exception.DuplicateResourceException;
import com.company.exception.BadRequestException;

@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentResponseDTO create(DepartmentRequestDTO request) {
        if (departmentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Department name already exists");
        }
        Department saved = departmentRepository.save(DepartmentMapper.toEntity(request));
        return DepartmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAll() {
        return departmentRepository.findAll().stream()
                .map(DepartmentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponseDTO getById(Long id) {
        return DepartmentMapper.toResponse(findDepartment(id));
    }

    public DepartmentResponseDTO update(Long id, DepartmentRequestDTO request) {
        Department department = findDepartment(id);
        if (!department.getName().equalsIgnoreCase(request.getName())
                && departmentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Department name already exists");
        }
        DepartmentMapper.updateEntity(department, request);
        return DepartmentMapper.toResponse(departmentRepository.save(department));
    }

    public void delete(Long id) {
        Department department = findDepartment(id);
        if (!department.getEmployees().isEmpty()) {
            throw new BadRequestException("Cannot delete a department that has employees");
        }
        departmentRepository.delete(department);
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }
}
