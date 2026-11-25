import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateEmploymentPeriodRequest,
  CreateEmploymentPeriodResponse,
  EmploymentPeriodDetails,
  RegisterEmploymentChangeRequest,
  RegisterEmploymentChangeResponse,
  TerminateEmploymentPeriodRequest,
  TerminateEmploymentPeriodResponse,
} from '../models/employee.model';

@Injectable({
  providedIn: 'root',
})
export class EmploymentPeriodService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/employment-periods';
  private readonly employeesUrl = '/api/v1/employees';

  getEmployeeHistory(employeeId: string): Observable<EmploymentPeriodDetails[]> {
    return this.http.get<EmploymentPeriodDetails[]>(
      `${this.employeesUrl}/${employeeId}/employment-periods`
    );
  }

  createEmploymentPeriod(
    payload: CreateEmploymentPeriodRequest
  ): Observable<CreateEmploymentPeriodResponse> {
    return this.http.post<CreateEmploymentPeriodResponse>(this.apiUrl, payload);
  }

  registerChange(
    employmentPeriodId: string,
    payload: RegisterEmploymentChangeRequest
  ): Observable<RegisterEmploymentChangeResponse> {
    return this.http.post<RegisterEmploymentChangeResponse>(
      `${this.apiUrl}/${employmentPeriodId}/changes`,
      payload
    );
  }

  terminatePeriod(
    employmentPeriodId: string,
    payload: TerminateEmploymentPeriodRequest
  ): Observable<TerminateEmploymentPeriodResponse> {
    return this.http.post<TerminateEmploymentPeriodResponse>(
      `${this.apiUrl}/${employmentPeriodId}/terminate`,
      payload
    );
  }
}
