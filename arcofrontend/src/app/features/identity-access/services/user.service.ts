import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateUserRequest,
  CreateUserResponse,
  PageResult,
  UserSummary,
} from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/users';

  getUsers(page = 0, size = 10): Observable<PageResult<UserSummary>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResult<UserSummary>>(this.apiUrl, { params });
  }

  getUserById(id: string): Observable<UserSummary> {
    return this.http.get<UserSummary>(`${this.apiUrl}/${id}`);
  }

  getUserByEmployeeId(employeeId: string): Observable<UserSummary> {
    return this.http.get<UserSummary>(`${this.apiUrl}/by-employee/${employeeId}`);
  }

  getLinkedEmployeeIds(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/linked-employee-ids`);
  }

  createUser(payload: CreateUserRequest): Observable<CreateUserResponse> {
    return this.http.post<CreateUserResponse>(this.apiUrl, payload);
  }
}
