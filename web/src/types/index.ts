export type Severity = 'P1' | 'P2' | 'P3' | 'P4';
export type IncidentStatus = 'OPEN' | 'ACKNOWLEDGED' | 'RESOLVED' | 'CLOSED';
export type IncidentType = 'SECURITY' | 'INFRASTRUCTURE' | 'SERVICE_DEGRADATION';
export type RoleName = 'ADMIN' | 'RESPONDER' | 'VIEWER';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role?: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  roles: string[];
}

export interface CreateIncidentRequest {
  title: string;
  description?: string;
  severity: Severity;
  type: IncidentType;
  assignedTeamId?: number;
  assignedUserId?: number;
}

export interface UpdateIncidentRequest {
  title?: string;
  description?: string;
  severity?: Severity;
  status?: IncidentStatus;
  type?: IncidentType;
  assignedTeamId?: number;
  assignedUserId?: number;
}

export interface IncidentResponse {
  id: number;
  title: string;
  description: string;
  severity: Severity;
  status: IncidentStatus;
  type: IncidentType;
  assignedTeamName: string | null;
  assignedUsername: string | null;
  createdByUsername: string;
  createdAt: string;
  updatedAt: string;
  resolvedAt: string | null;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
  createdAt: string;
}

export interface TeamResponse {
  id: number;
  name: string;
  memberUsernames: string[];
  createdAt: string;
}

export interface CreateTeamRequest {
  name: string;
  memberIds?: number[];
}

export interface MetricsResponse {
  meanTimeToResolveMinutes: number;
  totalIncidents: number;
  openIncidents: number;
  resolvedIncidents: number;
  countBySeverity: Record<string, number>;
  countByStatus: Record<string, number>;
  countByTeam: Record<string, number>;
}
