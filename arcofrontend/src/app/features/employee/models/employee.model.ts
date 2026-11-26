export type Gender = 'MALE' | 'FEMALE';

export type ContractType =
  | 'FULL_TIME'
  | 'PART_TIME'
  | 'CONTRACTOR'
  | 'INTERN'
  | 'TEMPORARY';

export type PensionFundInstitution = 'AFP_CONFIA' | 'AFP_CRECER' | 'ISP';

export type EmploymentStatus = 'ACTIVE' | 'TERMINATED';

export type EmployeeEmploymentStatusFilter = 'ACTIVE' | 'TERMINATED' | 'WITHOUT_PERIOD';

export type TerminationReason =
  | 'RESIGNATION'
  | 'DISMISSAL'
  | 'MUTUAL_AGREEMENT'
  | 'CONTRACT_EXPIRATION'
  | 'RETIREMENT'
  | 'DEATH'
  | 'OTHER';

export interface EmploymentChangeDetails {
  employmentChangeId: string;
  jobTitle: string;
  contractType: ContractType;
  baseSalary: number;
  effectiveFrom: string;
  createdAt: string;
}

export interface EmploymentPeriodDetails {
  employmentPeriodId: string;
  employeeId: string;
  startDate: string;
  endDate: string | null;
  status: EmploymentStatus;
  terminationReason: TerminationReason | null;
  changes: EmploymentChangeDetails[];
}

export interface RegisterEmploymentChangeRequest {
  jobTitle: string;
  contractType: ContractType;
  baseSalary: number;
  effectiveFrom: string;
}

export interface RegisterEmploymentChangeResponse {
  employmentChangeId: string;
}

export interface TerminateEmploymentPeriodRequest {
  endDate: string;
  terminationReason: TerminationReason;
}

export interface TerminateEmploymentPeriodResponse {
  employmentPeriodId: string;
}

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
  emergencyContactName: string | null;
  emergencyContactPhone: string | null;
  employmentStatus: EmployeeEmploymentStatusFilter;
  currentJobTitle: string | null;
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
  emergencyContactName?: string | null;
  emergencyContactPhone?: string | null;
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

export const EMPLOYEE_EMPLOYMENT_FILTER_LABELS: Record<EmployeeEmploymentStatusFilter, string> = {
  ACTIVE: 'Activo',
  TERMINATED: 'Finalizado / Baja',
  WITHOUT_PERIOD: 'Sin periodo',
};

export const TERMINATION_REASON_LABELS: Record<TerminationReason, string> = {
  RESIGNATION: 'Renuncia voluntaria',
  DISMISSAL: 'Despido',
  MUTUAL_AGREEMENT: 'Mutuo acuerdo',
  CONTRACT_EXPIRATION: 'Vencimiento de contrato',
  RETIREMENT: 'Jubilación',
  DEATH: 'Fallecimiento',
  OTHER: 'Otro motivo',
};
