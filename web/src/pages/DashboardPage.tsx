import { useEffect, useState } from 'react';
import { getMetrics } from '../api/metrics';
import type { MetricsResponse } from '../types';
import SummaryCards from '../components/metrics/SummaryCards';
import SeverityChart from '../components/metrics/SeverityChart';
import StatusChart from '../components/metrics/StatusChart';
import TeamChart from '../components/metrics/TeamChart';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';

export default function DashboardPage() {
  const [metrics, setMetrics] = useState<MetricsResponse | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMetrics()
      .then(({ data }) => setMetrics(data))
      .catch(() => setError('Failed to load metrics'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorAlert message={error} />;
  if (!metrics) return null;

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-900">Dashboard</h2>
      <SummaryCards metrics={metrics} />
      <div className="grid grid-cols-3 gap-4">
        <SeverityChart data={metrics.countBySeverity} />
        <StatusChart data={metrics.countByStatus} />
        <TeamChart data={metrics.countByTeam} />
      </div>
    </div>
  );
}
