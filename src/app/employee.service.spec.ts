import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { EmployeeService } from './employee.service';

describe('EmployeeService', () => {
  let service: EmployeeService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(EmployeeService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('requests a server-side employee page with active filters', () => {
    service.getEmployees(2, 12, '  Alex ', 'Canada', 'Engineering').subscribe();

    const request = http.expectOne(req => req.url === '/api/employees');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('page')).toBe('2');
    expect(request.request.params.get('size')).toBe('12');
    expect(request.request.params.get('search')).toBe('Alex');
    expect(request.request.params.get('country')).toBe('Canada');
    expect(request.request.params.get('department')).toBe('Engineering');
    request.flush({ content: [], totalElements: 0, totalPages: 0, number: 2, size: 12 });
  });

  it('creates, updates, and deletes employee records through the API', () => {
    const employee = {
      name: 'Alex Kim', email: 'alex@acme.test', title: 'Analyst',
      department: 'Product', country: 'Canada', currency: 'CAD',
      annualSalary: 90000, startDate: '2022-01-01'
    };

    service.create(employee).subscribe();
    const createRequest = http.expectOne('/api/employees');
    expect(createRequest.request.method).toBe('POST');
    createRequest.flush({ id: 7, ...employee });

    service.update(7, employee).subscribe();
    const updateRequest = http.expectOne('/api/employees/7');
    expect(updateRequest.request.method).toBe('PUT');
    updateRequest.flush({ id: 7, ...employee });

    service.delete(7).subscribe();
    const deleteRequest = http.expectOne('/api/employees/7');
    expect(deleteRequest.request.method).toBe('DELETE');
    deleteRequest.flush(null);
  });
});
