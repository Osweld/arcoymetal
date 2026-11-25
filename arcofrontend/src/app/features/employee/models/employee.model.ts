export type Gender = 'MALE' | 'FEMALE';

export type ContractType =
  | 'FULL_TIME'
  | 'PART_TIME'
  | 'CONTRACTOR'
  | 'INTERN'
  | 'TEMPORARY';

export type PensionFundInstitution = 'AFP_CONFIA' | 'AFP_CRECER' | 'ISP';

export type EmploymentStatus = 'ACTIVE' | 'TERMINATED';

export interface EmployeeDetails {
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: Gender;
  birthDate: string;
  addressLine: string;
  districtId: string;
  dui: string;
  nit: string | null;
  isss: string | null;
  nup: string | null;
  pensionFundInstitution: PensionFundInstitution;
  createdAt: string;
}

export interface CreateEmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: Gender;
  birthDate: string;
  addressLine: string;
  districtId: string;
  dui: string;
  nit?: string | null;
  isss?: string | null;
  nup?: string | null;
  pensionFundInstitution: PensionFundInstitution;
}

export interface CreateEmployeeResponse {
  employeeId: string;
}

export type UpdateEmployeeRequest = CreateEmployeeRequest;

export interface UpdateEmployeeResponse {
  employeeId: string;
}

export interface CreateEmploymentPeriodRequest {
  employeeId: string;
  startDate: string;
  jobTitle: string;
  contractType: ContractType;
  baseSalary: number;
}

export interface CreateEmploymentPeriodResponse {
  employmentPeriodId: string;
}

export const GENDER_LABELS: Record<Gender, string> = {
  MALE: 'Masculino',
  FEMALE: 'Femenino',
};

export const CONTRACT_TYPE_LABELS: Record<ContractType, string> = {
  FULL_TIME: 'Tiempo Completo (Planilla)',
  PART_TIME: 'Medio Tiempo',
  CONTRACTOR: 'Servicios Profesionales / Contratista',
  INTERN: 'Pasantía',
  TEMPORARY: 'Temporal / Por Obra',
};

export const PENSION_FUND_LABELS: Record<PensionFundInstitution, string> = {
  AFP_CONFIA: 'AFP Confía',
  AFP_CRECER: 'AFP Crecer',
  ISP: 'Instituto Salvadoreño de Pensiones (ISP)',
};

export const EMPLOYMENT_STATUS_LABELS: Record<EmploymentStatus, string> = {
  ACTIVE: 'Activo',
  TERMINATED: 'Finalizado / Baja',
};
