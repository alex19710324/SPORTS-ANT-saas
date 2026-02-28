// Core Entity Definitions

// User
export interface IUser {
  id: number;
  username: string;
  email: string;
  tenantId?: number;
  roles: IRole[];
  status?: 'ACTIVE' | 'INACTIVE';
  createdAt?: string;
}

// Role
export interface IRole {
  id: number;
  name: string; // e.g. 'ROLE_ADMIN'
  permissions: IPermission[];
  description?: string;
}

// Permission
export interface IPermission {
  id: number;
  name: string; // e.g. 'USER_READ'
  description?: string;
}

// System Config
export interface ISystemConfig {
  id: number;
  configKey: string;
  configValue: string;
  description?: string;
}

// API Response Wrapper
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

// Pagination
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // page index
}

// Add to global namespace if needed, or just export
declare global {
  type User = IUser;
  type Role = IRole;
  type Permission = IPermission;
  type SystemConfig = ISystemConfig;
}
