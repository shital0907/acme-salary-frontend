import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { CountrySummary, Employee, EmployeePayload, EmployeeService } from './employee.service';

const DEPARTMENTS = ['Engineering', 'Product', 'Design', 'People', 'Finance', 'Sales', 'Marketing', 'Operations'];
const COUNTRY_CURRENCIES = [
  { country: 'Australia', currency: 'AUD' }, { country: 'Canada', currency: 'CAD' },
  { country: 'Germany', currency: 'EUR' }, { country: 'India', currency: 'INR' },
  { country: 'Japan', currency: 'JPY' }, { country: 'Singapore', currency: 'SGD' },
  { country: 'United Kingdom', currency: 'GBP' }, { country: 'United States', currency: 'USD' }
];

const blankDraft = (): EmployeePayload => ({
  name: '', email: '', title: '', department: DEPARTMENTS[0], country: COUNTRY_CURRENCIES[0].country,
  currency: COUNTRY_CURRENCIES[0].currency, annualSalary: 0, startDate: new Date().toISOString().slice(0, 10)
});

@Component({
  selector: 'app-root',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private readonly api = inject(EmployeeService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly searchChanges = new Subject<string>();

  readonly departments = DEPARTMENTS;
  readonly countryCurrencies = COUNTRY_CURRENCIES;
  readonly pageSize = 12;
  readonly today = new Date().toISOString().slice(0, 10);
  readonly Math = Math;
  employees: Employee[] = [];
  summaries: CountrySummary[] = [];
  totalEmployees = 0;
  totalPages = 0;
  page = 0;
  search = '';
  countryFilter = '';
  departmentFilter = '';
  loading = true;
  saving = false;
  dialogOpen = false;
  editing: Employee | null = null;
  draft = blankDraft();
  errorMessage = '';
  formError = '';

  constructor() {
    this.searchChanges.pipe(debounceTime(250), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.page = 0;
        this.loadEmployees();
      });
  }

  get workforceTotal(): number {
    return this.summaries.reduce((total, item) => total + item.employeeCount, 0);
  }

  ngOnInit(): void {
    this.loadEmployees();
    this.api.getCountrySummaries().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: summaries => this.summaries = summaries,
      error: () => this.errorMessage = 'Could not load compensation summaries. Check that the API is running.'
    });
  }

  get topSummaries(): CountrySummary[] {
    return [...this.summaries].sort((a, b) => b.employeeCount - a.employeeCount).slice(0, 5);
  }

  onSearch(event: Event): void {
    this.search = (event.target as HTMLInputElement).value;
    this.searchChanges.next(this.search);
  }

  onCountryChange(event: Event): void {
    this.countryFilter = (event.target as HTMLSelectElement).value;
    this.page = 0;
    this.loadEmployees();
  }

  onDepartmentChange(event: Event): void {
    this.departmentFilter = (event.target as HTMLSelectElement).value;
    this.page = 0;
    this.loadEmployees();
  }

  changePage(offset: number): void {
    this.page += offset;
    this.loadEmployees();
  }

  openCreate(): void {
    this.editing = null;
    this.draft = blankDraft();
    this.formError = '';
    this.dialogOpen = true;
  }

  openEdit(employee: Employee): void {
    this.editing = employee;
    this.draft = {
      name: employee.name, email: employee.email, title: employee.title,
      department: employee.department, country: employee.country, currency: employee.currency,
      annualSalary: employee.annualSalary, startDate: employee.startDate
    };
    this.formError = '';
    this.dialogOpen = true;
  }

  closeDialog(): void {
    if (!this.saving) this.dialogOpen = false;
  }

  updateCurrency(): void {
    this.draft.currency = COUNTRY_CURRENCIES.find(item => item.country === this.draft.country)?.currency ?? '';
  }

  saveEmployee(): void {
    this.saving = true;
    this.formError = '';
    const request = this.editing
      ? this.api.update(this.editing.id, this.draft)
      : this.api.create(this.draft);
    request.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.dialogOpen = false;
        this.saving = false;
        this.loadEmployees();
        this.api.getCountrySummaries().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
          next: data => this.summaries = data,
          error: () => this.errorMessage = 'Employee saved, but compensation summaries could not be refreshed.'
        });
      },
      error: error => {
        this.saving = false;
        this.formError = this.errorText(error, 'Could not save employee. Please check the details and try again.');
      }
    });
  }

  remove(employee: Employee): void {
    if (!window.confirm(`Delete ${employee.name}'s employee record? This cannot be undone.`)) return;
    this.api.delete(employee.id).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        if (this.employees.length === 1 && this.page > 0) this.page--;
        this.loadEmployees();
        this.api.getCountrySummaries().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
          next: data => this.summaries = data,
          error: () => this.errorMessage = 'Employee deleted, but compensation summaries could not be refreshed.'
        });
      },
      error: error => this.errorMessage = this.errorText(error, 'Could not delete employee. Please try again.')
    });
  }

  initials(name: string): string {
    return name.split(/\s+/).map(part => part[0]).slice(0, 2).join('').toUpperCase();
  }

  barWidth(employeeCount: number): number {
    return this.workforceTotal ? Math.max(2, employeeCount / this.workforceTotal * 100) : 0;
  }

  workforceShare(employeeCount: number): string {
    return this.workforceTotal ? (employeeCount / this.workforceTotal * 100).toFixed(1) : '0.0';
  }

  private loadEmployees(): void {
    this.loading = true;
    this.errorMessage = '';
    this.api.getEmployees(this.page, this.pageSize, this.search, this.countryFilter, this.departmentFilter)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: result => {
          this.employees = result.content;
          this.totalEmployees = result.totalElements;
          this.totalPages = result.totalPages;
          this.loading = false;
        },
        error: error => {
          this.loading = false;
          this.errorMessage = this.errorText(error, 'Could not load employees. Check that the API is running.');
        }
      });
  }

  private errorText(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse && error.status === 409) return 'That email address is already in use.';
    if (error instanceof HttpErrorResponse && error.status === 400) return 'Please check the employee details and try again.';
    return fallback;
  }
}
