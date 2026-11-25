import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { EmploymentPeriodService } from './employment-period.service';
import { CreateEmploymentPeriodRequest } from '../models/employee.model';

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
});
