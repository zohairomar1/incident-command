import client from './client';
import type { MetricsResponse } from '../types';

export const getMetrics = () =>
  client.get<MetricsResponse>('/metrics');
