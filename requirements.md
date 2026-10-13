# ACME Salary Management — Requirements

## Goal
Give ACME's HR manager a reliable web application for maintaining compensation records for 10,000 employees across countries and answering basic questions about how the organization pays people.

## Users and scope
The first release is an internal tool for HR managers. It provides:

- A searchable, filterable, paginated employee directory.
- Create, edit, and delete actions for employee compensation records, with validated inputs and clear feedback.
- A compensation overview grouped by country and currency, including employee count and average, minimum, and maximum salary. Salaries are not combined across currencies.
- A reproducible, idempotent seed of 10,000 clearly synthetic employees for a useful out-of-the-box demonstration.
- A persistent relational database and a responsive desktop/mobile interface.

Employee records contain a name, work email, job title, department, country, currency, annual base salary, and start date. Salary amounts are annual gross amounts in the employee's listed currency.

## Deliberately out of scope
- **Authentication, roles, and audit history:** this assessment's single-persona demo has no identity provider or access-control requirements. These are mandatory before real compensation data is used.
- **Payroll processing, bonuses, equity, tax, benefits, and salary recommendations:** the requested product is a salary-management and overview tool, not a payroll engine or compensation policy system.
- **Cross-currency totals or conversions:** exchange rates and a reporting currency were not specified; presenting unlike currencies as one total would be misleading.
- **Bulk spreadsheet import/export and integrations:** these need file-format, conflict-resolution, and source-of-truth decisions. The seeded dataset demonstrates scale without inventing those policies.
- **Production hosting and recorded video:** deployment credentials and a recording destination were not provided. The repository should include local run/deployment guidance and a repeatable demo checklist instead.

## Acceptance criteria
- A fresh local setup can start the API and UI and initialize a relational database with 10,000 sample employees.
- An HR manager can search/filter/page through the directory and create, update, or delete a record.
- The overview explains compensation by country and currency without mixing currencies.
- Invalid records are rejected by the backend and communicated to the UI.
- Automated tests cover core validation, employee operations, filtering, and salary aggregation.
