package com.acme.salary.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("""
            select e from Employee e
            where (:search is null or lower(e.name) like lower(concat('%', :search, '%'))
                or lower(e.email) like lower(concat('%', :search, '%'))
                or lower(e.title) like lower(concat('%', :search, '%')))
              and (:country is null or e.country = :country)
              and (:department is null or e.department = :department)
            """)
    Page<Employee> search(@Param("search") String search, @Param("country") String country,
                          @Param("department") String department, Pageable pageable);

    @Query("""
            select e.country as country, e.currency as currency, count(e) as employeeCount,
                   avg(e.annualSalary) as averageSalary, min(e.annualSalary) as minimumSalary,
                   max(e.annualSalary) as maximumSalary
            from Employee e
            group by e.country, e.currency
            order by e.country
            """)
    List<CountrySalarySummary> summarizeByCountry();

    interface CountrySalarySummary {
        String getCountry();
        String getCurrency();
        Long getEmployeeCount();
        java.math.BigDecimal getAverageSalary();
        java.math.BigDecimal getMinimumSalary();
        java.math.BigDecimal getMaximumSalary();
    }
}
