package com.acme.salary.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<Employee> search(String search, String country, String department, Pageable pageable) {
        return repository.search(blankToNull(search), blankToNull(country), blankToNull(department), pageable);
    }

    public Employee create(EmployeeRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address is already in use");
        }
        Employee employee = new Employee(request.name().trim(), request.email().trim().toLowerCase(),
                request.title().trim(), request.department().trim(), request.country().trim(),
                request.currency(), request.annualSalary(), request.startDate());
        return repository.save(employee);
    }

    public Employee update(long id, EmployeeRequest request) {
        Employee employee = findById(id);
        if (repository.existsByEmailIgnoreCaseAndIdNot(request.email().trim(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address is already in use");
        }
        employee.update(request.name().trim(), request.email().trim().toLowerCase(),
                request.title().trim(), request.department().trim(), request.country().trim(),
                request.currency(), request.annualSalary(), request.startDate());
        return repository.save(employee);
    }

    public void delete(long id) {
        repository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeRepository.CountrySalarySummary> analytics() {
        return repository.summarizeByCountry();
    }

    private Employee findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
