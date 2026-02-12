import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getIncident, updateIncident } from '../api/incidents';
import type { IncidentResponse } from '../types';
import IncidentForm from '../components/incidents/IncidentForm';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';

export default function IncidentEditPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [incident, setIncident] = useState<IncidentResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getIncident(Number(id))
      .then(({ data }) => setIncident(data))
      .catch(() => setError('Failed to load incident'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorAlert message={error} />;
  if (!incident) return null;

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold text-gray-900">Edit Incident</h2>
      <IncidentForm
        initial={{
          title: incident.title,
          description: incident.description,
          severity: incident.severity,
          type: incident.type,
        }}
        submitLabel="Update Incident"
        onSubmit={async (form) => {
          await updateIncident(Number(id), {
            title: form.title,
            description: form.description || undefined,
            severity: form.severity,
            type: form.type,
            assignedTeamId: form.assignedTeamId ? Number(form.assignedTeamId) : undefined,
          });
          navigate(`/incidents/${id}`);
        }}
      />
    </div>
  );
}
