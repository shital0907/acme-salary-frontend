package com.acme.salary.employee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class EmployeeSeed implements CommandLineRunner {
    private static final String[] FIRST_NAMES = {"Avery", "Jordan", "Riley", "Morgan", "Casey", "Taylor", "Quinn", "Jamie", "Cameron", "Alex"};
    private static final String[] LAST_NAMES = {"Morgan", "Patel", "Chen", "Kim", "Garcia", "Singh", "Brown", "Wilson", "Khan", "Diaz"};
    private static final String[][] LOCATIONS = {
            {"United States", "USD"}, {"United Kingdom", "GBP"}, {"Canada", "CAD"},
            {"Germany", "EUR"}, {"India", "INR"}, {"Australia", "AUD"},
            {"Singapore", "SGD"}, {"Japan", "JPY"}
    };
    private static final String[] DEPARTMENTS = {"Engineering", "Product", "Design", "People", "Finance", "Sales", "Marketing", "Operations"};
    private static final String[] TITLES = {"Associate", "Specialist", "Senior", "Lead", "Manager"};

    private final EmployeeRepository repository;

    public EmployeeSeed(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.count() > 0) return;
        List<Employee> batch = new ArrayList<>(1000);
        for (int i = 0; i < 10_000; i++) {
            String[] location = LOCATIONS[i % LOCATIONS.length];
            batch.add(new Employee(
                    FIRST_NAMES[i % FIRST_NAMES.length] + " " + LAST_NAMES[(i / 7) % LAST_NAMES.length],
                    "employee" + (i + 1) + "@acme.example",
                    TITLES[(i / 13) % TITLES.length] + " " + DEPARTMENTS[(i / 29) % DEPARTMENTS.length],
                    DEPARTMENTS[(i / 29) % DEPARTMENTS.length],
                    location[0], location[1],
                    BigDecimal.valueOf(45_000L + (i * 173L) % 115_000L),
                    LocalDate.of(2018, 1, 1).plusDays((i * 37L) % 3000)));
            if (batch.size() == 1000) {
                repository.saveAll(batch);
                batch.clear();
            }
        }
        repository.saveAll(batch);
    }
}
