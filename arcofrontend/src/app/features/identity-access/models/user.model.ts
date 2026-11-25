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

export type { PageResult } from '../../../shared/models/pagination.model';

export const ROLE_LABELS: Record<Role, string> = {
  ADMINISTRATOR: 'Administrador',
  MANAGER: 'Gerente / Jefe de Área',
};

export const USER_STATUS_LABELS: Record<UserStatus, string> = {
  ACTIVE: 'Activa',
  DISABLED: 'Desactivada',
  LOCKED: 'Bloqueada',
};
