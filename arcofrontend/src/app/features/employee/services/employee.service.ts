import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResult } from '../../../shared/models/pagination.model';
import {
  CreateEmployeeRequest,
  CreateEmployeeResponse,
  EmployeeDetails,
  EmployeeEmploymentStatusFilter,
  UpdateEmployeeRequest,
  UpdateEmployeeResponse,
} from '../models/employee.model';

export interface EmployeeSearchFilters {
  search?: string;
  employmentStatus?: EmployeeEmploymentStatusFilter | '';
}

@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/employees';

  getEmployeesPage(
    page = 0,
    size = 20,
    filters?: EmployeeSearchFilters
  ): Observable<PageResult<EmployeeDetails>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    const trimmedSearch = filters?.search?.trim();
    if (trimmedSearch) {
      params = params.set('search', trimmedSearch);
    }
    if (filters?.employmentStatus) {
      params = params.set('employmentStatus', filters.employmentStatus);
    }

    return this.http.get<PageResult<EmployeeDetails>>(this.apiUrl, { params });
  }

  getEmployeeById(id: string): Observable<EmployeeDetails> {
    return this.http.get<EmployeeDetails>(`${this.apiUrl}/${id}`);
  }

  createEmployee(payload: CreateEmployeeRequest): Observable<CreateEmployeeResponse> {
    return this.http.post<CreateEmployeeResponse>(this.apiUrl, payload);
  }

  updateEmployee(
    id: string,
    payload: UpdateEmployeeRequest
  ): Observable<UpdateEmployeeResponse> {
    return this.http.put<UpdateEmployeeResponse>(`${this.apiUrl}/${id}`, payload);
  }
}
