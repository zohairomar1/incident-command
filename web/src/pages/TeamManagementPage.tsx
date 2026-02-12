import { useEffect, useState } from 'react';
import { getTeams, createTeam } from '../api/teams';
import type { TeamResponse } from '../types';
import TeamList from '../components/teams/TeamList';
import TeamForm from '../components/teams/TeamForm';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';

export default function TeamManagementPage() {
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadTeams = () => {
    getTeams()
      .then(({ data }) => setTeams(data))
      .catch(() => setError('Failed to load teams'))
      .finally(() => setLoading(false));
  };

  useEffect(loadTeams, []);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorAlert message={error} />;

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-gray-900">Team Management</h2>
      <TeamForm onSubmit={async (name) => {
        await createTeam({ name });
        loadTeams();
      }} />
      <TeamList teams={teams} />
    </div>
  );
}
