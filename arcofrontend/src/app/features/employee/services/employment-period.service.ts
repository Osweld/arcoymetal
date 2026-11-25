import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateEmploymentPeriodRequest,
  CreateEmploymentPeriodResponse,
} from '../models/employee.model';

@Injectable({
  providedIn: 'root',
})
export class EmploymentPeriodService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/employment-periods';

  createEmploymentPeriod(
    payload: CreateEmploymentPeriodRequest
  ): Observable<CreateEmploymentPeriodResponse> {
    return this.http.post<CreateEmploymentPeriodResponse>(this.apiUrl, payload);
  }
}
