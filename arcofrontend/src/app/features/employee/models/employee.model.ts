export type Gender = 'MALE' | 'FEMALE';

export type ContractType =
  | 'FULL_TIME'
  | 'PART_TIME'
  | 'CONTRACTOR'
  | 'INTERN'
  | 'TEMPORARY';

export type PensionFundInstitution = 'AFP_CONFIA' | 'AFP_CRECER' | 'ISP';

export type EmployeeStatus = 'ACTIVE' | 'INACTIVE' | 'TERMINATED';

export interface EmployeeDetails {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: Gender;
  birthDate: string;
  addressLine: string;
  districtId: string;
  jobTitle: string;
  baseSalary: number;
  contractType: ContractType;
  dui: string;
  nit: string | null;
  isss: string | null;
  nup: string | null;
  pensionFundInstitution: PensionFundInstitution;
  hireDate: string;
  terminationDate: string | null;
  status: EmployeeStatus;
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
  jobTitle: string;
  baseSalary: number;
  contractType: ContractType;
  dui: string;
  nit?: string | null;
  isss?: string | null;
  nup?: string | null;
  pensionFundInstitution: PensionFundInstitution;
  hireDate: string;
}

export interface CreateEmployeeResponse {
  employeeId: string;
}

export interface UpdatePersonalInformationRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  addressLine: string;
  districtId: string;
}

export interface UpdatePersonalInformationResponse {
  employeeId: string;
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

export const EMPLOYEE_STATUS_LABELS: Record<EmployeeStatus, string> = {
  ACTIVE: 'Activo',
  INACTIVE: 'Inactivo',
  TERMINATED: 'Dado de baja',
};
