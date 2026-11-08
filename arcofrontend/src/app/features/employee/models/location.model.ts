export interface DistrictDetails {
  id: string;
  name: string;
}

export interface MunicipalityDetails {
  id: string;
  name: string;
  districts: DistrictDetails[];
}

export interface DepartmentDetails {
  id: string;
  name: string;
  municipalities: MunicipalityDetails[];
}

export interface ResolvedLocation {
  department: DepartmentDetails;
  municipality: MunicipalityDetails;
  district: DistrictDetails;
}
