package com.acme.salary.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "app.seed.enabled=false")
@Transactional
class EmployeeServiceTest {
    @Autowired EmployeeService service;
    @Autowired EmployeeRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsNormalizesSearchesAndUpdatesEmployee() {
        Employee created = service.create(request("  Alex Kim ", " ALEX@acme.test ", "Canada", "Engineering", "CAD", "95000"));
        assertThat(created.getName()).isEqualTo("Alex Kim");
        assertThat(created.getEmail()).isEqualTo("alex@acme.test");
        assertThat(service.search("alex", "Canada", "Engineering", PageRequest.of(0, 10)).getTotalElements()).isEqualTo(1);

        Employee updated = service.update(created.getId(), request("Alex Kim", "alex@acme.test", "Canada", "Product", "CAD", "105000"));
        assertThat(updated.getDepartment()).isEqualTo("Product");
        assertThat(updated.getAnnualSalary()).isEqualByComparingTo("105000");
        assertThat(service.search(null, "Canada", "Engineering", PageRequest.of(0, 10)).getTotalElements()).isZero();
    }

    @Test
    void groupsCompensationByCountryAndCurrency() {
        service.create(request("Alex Kim", "alex@acme.test", "Canada", "Engineering", "CAD", "90000"));
        service.create(request("Riley Chen", "riley@acme.test", "Canada", "Product", "CAD", "110000"));
        service.create(request("Sam Lee", "sam@acme.test", "Japan", "Engineering", "JPY", "8000000"));

        var summaries = service.analytics();
        assertThat(summaries).hasSize(2);
        var canada = summaries.stream().filter(row -> row.getCountry().equals("Canada")).findFirst().orElseThrow();
        assertThat(canada.getCurrency()).isEqualTo("CAD");
        assertThat(canada.getEmployeeCount()).isEqualTo(2);
        assertThat(canada.getAverageSalary()).isEqualByComparingTo("100000");
    }

    @Test
    void rejectsMissingEmployeeOnUpdate() {
        assertThatThrownBy(() -> service.update(987654, request("Alex", "alex@acme.test", "Canada", "Product", "CAD", "90000")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void rejectsDuplicateEmailWithoutChangingExistingRecord() {
        service.create(request("Alex Kim", "alex@acme.test", "Canada", "Engineering", "CAD", "90000"));
        assertThatThrownBy(() -> service.create(request("Riley Chen", "ALEX@ACME.TEST", "Canada", "Product", "CAD", "95000")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode").isEqualTo(HttpStatus.CONFLICT);
        assertThat(repository.count()).isEqualTo(1);
    }

    private EmployeeRequest request(String name, String email, String country, String department, String currency, String salary) {
        return new EmployeeRequest(name, email, "Analyst", department, country, currency,
                new BigDecimal(salary), LocalDate.of(2022, 1, 1));
    }
}
