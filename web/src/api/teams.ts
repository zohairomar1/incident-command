import client from './client';
import type { CreateTeamRequest, TeamResponse } from '../types';

export const getTeams = () =>
  client.get<TeamResponse[]>('/teams');

export const getTeam = (id: number) =>
  client.get<TeamResponse>(`/teams/${id}`);

export const createTeam = (data: CreateTeamRequest) =>
  client.post<TeamResponse>('/teams', data);
