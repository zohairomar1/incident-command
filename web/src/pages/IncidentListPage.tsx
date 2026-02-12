import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getIncidents } from '../api/incidents';
import type { IncidentResponse } from '../types';
import { useAuth } from '../hooks/useAuth';
import IncidentTable from '../components/incidents/IncidentTable';
import IncidentFilters from '../components/incidents/IncidentFilters';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';

export default function IncidentListPage() {
  const [incidents, setIncidents] = useState<IncidentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [severityFilter, setSeverityFilter] = useState('');
  const { hasAnyRole } = useAuth();

  useEffect(() => {
    getIncidents()
      .then(({ data }) => setIncidents(data))
      .catch(() => setError('Failed to load incidents'))
      .finally(() => setLoading(false));
  }, []);

  const filtered = incidents.filter((inc) => {
    if (statusFilter && inc.status !== statusFilter) return false;
    if (severityFilter && inc.severity !== severityFilter) return false;
    return true;
  });

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorAlert message={error} />;

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-900">Incidents</h2>
        {hasAnyRole('ADMIN', 'RESPONDER') && (
          <Link
            to="/incidents/new"
            className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
          >
            New Incident
          </Link>
        )}
      </div>
      <IncidentFilters
        status={statusFilter}
        severity={severityFilter}
        onStatusChange={setStatusFilter}
        onSeverityChange={setSeverityFilter}
      />
      <IncidentTable incidents={filtered} />
    </div>
  );
}
