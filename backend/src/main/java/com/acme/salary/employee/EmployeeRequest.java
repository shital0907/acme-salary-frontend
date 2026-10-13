package com.acme.salary.employee;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 80) String department,
        @NotBlank @Size(max = 80) String country,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$", message = "must be a 3-letter uppercase currency code") String currency,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal annualSalary,
        @NotNull @PastOrPresent LocalDate startDate
) {}
