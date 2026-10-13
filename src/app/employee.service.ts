import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface Employee {
  id: number;
  name: string;
  email: string;
  title: string;
  department: string;
  country: string;
  currency: string;
  annualSalary: number;
  startDate: string;
}

export type EmployeePayload = Omit<Employee, 'id'>;

export interface EmployeePage {
  content: Employee[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface CountrySummary {
  country: string;
  currency: string;
  employeeCount: number;
  averageSalary: number;
  minimumSalary: number;
  maximumSalary: number;
}

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api';

  getEmployees(page: number, size: number, search: string, country: string, department: string): Observable<EmployeePage> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search.trim()) params = params.set('search', search.trim());
    if (country) params = params.set('country', country);
    if (department) params = params.set('department', department);
    return this.http.get<EmployeePage>(`${this.baseUrl}/employees`, { params });
  }

  getCountrySummaries(): Observable<CountrySummary[]> {
    return this.http.get<CountrySummary[]>(`${this.baseUrl}/analytics/countries`);
  }

  create(employee: EmployeePayload): Observable<Employee> {
    return this.http.post<Employee>(`${this.baseUrl}/employees`, employee);
  }

  update(id: number, employee: EmployeePayload): Observable<Employee> {
    return this.http.put<Employee>(`${this.baseUrl}/employees/${id}`, employee);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/employees/${id}`);
  }
}
