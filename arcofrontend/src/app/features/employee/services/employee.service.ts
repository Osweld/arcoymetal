import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateEmployeeRequest,
  CreateEmployeeResponse,
  EmployeeDetails,
  UpdatePersonalInformationRequest,
  UpdatePersonalInformationResponse,
} from '../models/employee.model';

@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/employees';

  getAllEmployees(): Observable<EmployeeDetails[]> {
    return this.http.get<EmployeeDetails[]>(this.apiUrl);
  }

  getEmployeeById(id: string): Observable<EmployeeDetails> {
    return this.http.get<EmployeeDetails>(`${this.apiUrl}/${id}`);
  }

  createEmployee(payload: CreateEmployeeRequest): Observable<CreateEmployeeResponse> {
    return this.http.post<CreateEmployeeResponse>(this.apiUrl, payload);
  }

  updatePersonalInformation(
    id: string,
    payload: UpdatePersonalInformationRequest
  ): Observable<UpdatePersonalInformationResponse> {
    return this.http.put<UpdatePersonalInformationResponse>(
      `${this.apiUrl}/${id}/personal-information`,
      payload
    );
  }
}
