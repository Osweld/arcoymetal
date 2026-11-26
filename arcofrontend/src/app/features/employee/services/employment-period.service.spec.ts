import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { EmploymentPeriodService } from './employment-period.service';
import {
  CreateEmploymentPeriodRequest,
  RegisterEmploymentChangeRequest,
  TerminateEmploymentPeriodRequest,
} from '../models/employee.model';

describe('EmploymentPeriodService', () => {
  let service: EmploymentPeriodService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(EmploymentPeriodService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('fetches employee employment history from GET /api/v1/employees/:id/employment-periods', () => {
    let receivedCount = -1;
    service.getEmployeeHistory('emp-1').subscribe((res) => (receivedCount = res.length));

    const req = httpMock.expectOne('/api/v1/employees/emp-1/employment-periods');
    expect(req.request.method).toBe('GET');
    req.flush([]);

    expect(receivedCount).toBe(0);
  });

  it('creates an employment period via POST /api/v1/employment-periods', () => {
    const payload: CreateEmploymentPeriodRequest = {
      employeeId: 'emp-1',
      startDate: '2026-01-15',
      jobTitle: 'Soldador Industrial',
      contractType: 'FULL_TIME',
      baseSalary: 650.5,
    };

    let receivedId: string | undefined;
    service.createEmploymentPeriod(payload).subscribe((res) => (receivedId = res.employmentPeriodId));

    const req = httpMock.expectOne('/api/v1/employment-periods');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ employmentPeriodId: 'ep-1' }, { status: 201, statusText: 'Created' });

    expect(receivedId).toBe('ep-1');
  });

  it('registers an employment change via POST /api/v1/employment-periods/:id/changes', () => {
    const payload: RegisterEmploymentChangeRequest = {
      jobTitle: 'Supervisor de Taller',
      contractType: 'FULL_TIME',
      baseSalary: 820.0,
      effectiveFrom: '2026-06-01',
    };

    let receivedId: string | undefined;
    service.registerChange('ep-1', payload).subscribe((res) => (receivedId = res.employmentChangeId));

    const req = httpMock.expectOne('/api/v1/employment-periods/ep-1/changes');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ employmentChangeId: 'chg-1' }, { status: 201, statusText: 'Created' });

    expect(receivedId).toBe('chg-1');
  });

  it('terminates an employment period via POST /api/v1/employment-periods/:id/terminate', () => {
    const payload: TerminateEmploymentPeriodRequest = {
      endDate: '2026-12-31',
      terminationReason: 'RESIGNATION',
    };

    let receivedId: string | undefined;
    service.terminatePeriod('ep-1', payload).subscribe((res) => (receivedId = res.employmentPeriodId));

    const req = httpMock.expectOne('/api/v1/employment-periods/ep-1/terminate');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ employmentPeriodId: 'ep-1' });

    expect(receivedId).toBe('ep-1');
  });

  it('removes latest employment change via DELETE /api/v1/employment-periods/:id/changes/:changeId', () => {
    let completed = false;
    service.removeChange('ep-1', 'chg-2').subscribe(() => (completed = true));

    const req = httpMock.expectOne('/api/v1/employment-periods/ep-1/changes/chg-2');
    expect(req.request.method).toBe('DELETE');
    req.flush(null, { status: 204, statusText: 'No Content' });

    expect(completed).toBe(true);
  });
});
