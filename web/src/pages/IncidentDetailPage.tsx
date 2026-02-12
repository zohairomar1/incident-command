import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getIncident, patchIncidentStatus, deleteIncident } from '../api/incidents';
import type { IncidentResponse, IncidentStatus } from '../types';
import { useAuth } from '../hooks/useAuth';
import { StatusBadge, SeverityBadge } from '../components/incidents/StatusBadge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';

const transitions: Record<IncidentStatus, IncidentStatus[]> = {
  OPEN: ['ACKNOWLEDGED', 'RESOLVED', 'CLOSED'],
  ACKNOWLEDGED: ['RESOLVED', 'CLOSED'],
  RESOLVED: ['CLOSED', 'OPEN'],
  CLOSED: [],
};

export default function IncidentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { hasAnyRole } = useAuth();
  const [incident, setIncident] = useState<IncidentResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getIncident(Number(id))
      .then(({ data }) => setIncident(data))
      .catch(() => setError('Failed to load incident'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleStatusChange = async (status: IncidentStatus) => {
    try {
      const { data } = await patchIncidentStatus(Number(id), status);
      setIncident(data);
    } catch {
      setError('Failed to update status');
    }
  };

  const handleDelete = async () => {
    if (!confirm('Delete this incident?')) return;
    try {
      await deleteIncident(Number(id));
      navigate('/incidents');
    } catch {
      setError('Failed to delete incident');
    }
  };

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorAlert message={error} />;
  if (!incident) return null;

  const canModify = hasAnyRole('ADMIN', 'RESPONDER');

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-900">{incident.title}</h2>
        {canModify && (
          <div className="flex gap-2">
            <Link
              to={`/incidents/${id}/edit`}
              className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              Edit
            </Link>
            <button
              onClick={handleDelete}
              className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700"
            >
              Delete
            </button>
          </div>
        )}
      </div>

      <div className="rounded-xl border border-gray-200 bg-white p-6">
        <dl className="grid grid-cols-2 gap-4">
          <div>
            <dt className="text-sm text-gray-500">Severity</dt>
            <dd className="mt-1"><SeverityBadge severity={incident.severity} /></dd>
          </div>
          <div>
            <dt className="text-sm text-gray-500">Status</dt>
            <dd className="mt-1"><StatusBadge status={incident.status} /></dd>
          </div>
          <div>
            <dt className="text-sm text-gray-500">Type</dt>
            <dd className="mt-1 text-gray-900">{incident.type.replace('_', ' ')}</dd>
          </div>
          <div>
            <dt className="text-sm text-gray-500">Created By</dt>
            <dd className="mt-1 text-gray-900">{incident.createdByUsername}</dd>
          </div>
          <div>
            <dt className="text-sm text-gray-500">Assigned Team</dt>
            <dd className="mt-1 text-gray-900">{incident.assignedTeamName || '-'}</dd>
          </div>
          <div>
            <dt className="text-sm text-gray-500">Assigned User</dt>
            <dd className="mt-1 text-gray-900">{incident.assignedUsername || '-'}</dd>
          </div>
          <div>
            <dt className="text-sm text-gray-500">Created At</dt>
            <dd className="mt-1 text-gray-900">{new Date(incident.createdAt).toLocaleString()}</dd>
          </div>
          <div>
            <dt className="text-sm text-gray-500">Resolved At</dt>
            <dd className="mt-1 text-gray-900">{incident.resolvedAt ? new Date(incident.resolvedAt).toLocaleString() : '-'}</dd>
          </div>
          <div className="col-span-2">
            <dt className="text-sm text-gray-500">Description</dt>
            <dd className="mt-1 whitespace-pre-wrap text-gray-900">{incident.description || 'No description.'}</dd>
          </div>
        </dl>
      </div>

      {canModify && transitions[incident.status].length > 0 && (
        <div className="rounded-xl border border-gray-200 bg-white p-6">
          <h3 className="mb-3 text-sm font-medium text-gray-700">Transition Status</h3>
          <div className="flex gap-2">
            {transitions[incident.status].map((status) => (
              <button
                key={status}
                onClick={() => handleStatusChange(status)}
                className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                {status}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
