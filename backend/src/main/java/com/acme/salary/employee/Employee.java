package com.acme.salary.employee;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_email", columnList = "email", unique = true),
        @Index(name = "idx_employee_country_department", columnList = "country, department")
})
@SequenceGenerator(name = "employee_sequence", sequenceName = "employee_sequence", allocationSize = 500)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_sequence")
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(nullable = false, length = 80)
    private String country;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal annualSalary;

    @Column(nullable = false)
    private LocalDate startDate;

    protected Employee() {}

    public Employee(String name, String email, String title, String department,
                    String country, String currency, BigDecimal annualSalary, LocalDate startDate) {
        this.name = name;
        this.email = email;
        this.title = title;
        this.department = department;
        this.country = country;
        this.currency = currency;
        this.annualSalary = annualSalary;
        this.startDate = startDate;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getCountry() { return country; }
    public String getCurrency() { return currency; }
    public BigDecimal getAnnualSalary() { return annualSalary; }
    public LocalDate getStartDate() { return startDate; }

    public void update(String name, String email, String title, String department,
                       String country, String currency, BigDecimal annualSalary, LocalDate startDate) {
        this.name = name;
        this.email = email;
        this.title = title;
        this.department = department;
        this.country = country;
        this.currency = currency;
        this.annualSalary = annualSalary;
        this.startDate = startDate;
    }
}
