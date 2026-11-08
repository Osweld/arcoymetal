import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { DepartmentDetails, ResolvedLocation } from '../models/location.model';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/locations/hierarchy';

  private readonly cachedHierarchy = signal<DepartmentDetails[] | null>(null);

  getHierarchy(): Observable<DepartmentDetails[]> {
    const cached = this.cachedHierarchy();
    if (cached) {
      return of(cached);
    }

    return this.http.get<DepartmentDetails[]>(this.apiUrl).pipe(
      tap((departments) => this.cachedHierarchy.set(departments))
    );
  }

  resolveDistrict(
    hierarchy: DepartmentDetails[],
    districtId: string
  ): ResolvedLocation | null {
    for (const department of hierarchy) {
      for (const municipality of department.municipalities) {
        const district = municipality.districts.find((d) => d.id === districtId);
        if (district) {
          return { department, municipality, district };
        }
      }
    }
    return null;
  }
}
