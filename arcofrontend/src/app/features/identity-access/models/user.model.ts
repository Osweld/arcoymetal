export type Role = 'ADMINISTRATOR' | 'MANAGER';

export type UserStatus = 'ACTIVE' | 'DISABLED' | 'LOCKED';

export interface UserSummary {
  id: string;
  employeeId: string;
  email: string;
  roles: Role[];
  status: UserStatus;
  createdAt: string;
}

export interface CreateUserRequest {
  password: string;
  email: string;
  employeeId: string;
  roles: Role[];
}

export interface CreateUserResponse {
  userId: string;
}

export interface PageResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export const ROLE_LABELS: Record<Role, string> = {
  ADMINISTRATOR: 'Administrador',
  MANAGER: 'Gerente / Jefe de Área',
};

export const USER_STATUS_LABELS: Record<UserStatus, string> = {
  ACTIVE: 'Activa',
  DISABLED: 'Desactivada',
  LOCKED: 'Bloqueada',
};
