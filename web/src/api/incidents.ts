import client from './client';
import type { CreateIncidentRequest, UpdateIncidentRequest, IncidentResponse } from '../types';

export const getIncidents = () =>
  client.get<IncidentResponse[]>('/incidents');

export const getIncident = (id: number) =>
  client.get<IncidentResponse>(`/incidents/${id}`);

export const createIncident = (data: CreateIncidentRequest) =>
  client.post<IncidentResponse>('/incidents', data);

export const updateIncident = (id: number, data: UpdateIncidentRequest) =>
  client.put<IncidentResponse>(`/incidents/${id}`, data);

export const patchIncidentStatus = (id: number, status: string) =>
  client.patch<IncidentResponse>(`/incidents/${id}/status`, null, { params: { status } });

export const deleteIncident = (id: number) =>
  client.delete<void>(`/incidents/${id}`);
