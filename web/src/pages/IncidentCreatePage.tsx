import { useNavigate } from 'react-router-dom';
import { createIncident } from '../api/incidents';
import IncidentForm from '../components/incidents/IncidentForm';

export default function IncidentCreatePage() {
  const navigate = useNavigate();

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold text-gray-900">Create Incident</h2>
      <IncidentForm
        submitLabel="Create Incident"
        onSubmit={async (form) => {
          await createIncident({
            title: form.title,
            description: form.description || undefined,
            severity: form.severity,
            type: form.type,
            assignedTeamId: form.assignedTeamId ? Number(form.assignedTeamId) : undefined,
          });
          navigate('/incidents');
        }}
      />
    </div>
  );
}
