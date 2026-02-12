import { useEffect, useState } from 'react';
import type { Severity, IncidentType, TeamResponse } from '../../types';
import { getTeams } from '../../api/teams';

interface FormData {
  title: string;
  description: string;
  severity: Severity;
  type: IncidentType;
  assignedTeamId: string;
}

interface Props {
  initial?: Partial<FormData>;
  onSubmit: (data: FormData) => Promise<void>;
  submitLabel: string;
}

export default function IncidentForm({ initial, onSubmit, submitLabel }: Props) {
  const [form, setForm] = useState<FormData>({
    title: initial?.title || '',
    description: initial?.description || '',
    severity: initial?.severity || 'P3',
    type: initial?.type || 'SERVICE_DEGRADATION',
    assignedTeamId: initial?.assignedTeamId || '',
  });
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    getTeams().then(({ data }) => setTeams(data)).catch(() => {});
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await onSubmit(form);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Operation failed');
    } finally {
      setLoading(false);
    }
  };

  const set = (field: keyof FormData) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
    setForm({ ...form, [field]: e.target.value });

  return (
    <form onSubmit={handleSubmit} className="space-y-4 rounded-xl border border-gray-200 bg-white p-6">
      {error && <div className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</div>}

      <div>
        <label className="mb-1 block text-sm font-medium text-gray-700">Title</label>
        <input type="text" value={form.title} onChange={set('title')} required
          className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500" />
      </div>

      <div>
        <label className="mb-1 block text-sm font-medium text-gray-700">Description</label>
        <textarea value={form.description} onChange={set('description')} rows={4}
          className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500" />
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Severity</label>
          <select value={form.severity} onChange={set('severity')}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none">
            <option value="P1">P1</option>
            <option value="P2">P2</option>
            <option value="P3">P3</option>
            <option value="P4">P4</option>
          </select>
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Type</label>
          <select value={form.type} onChange={set('type')}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none">
            <option value="SECURITY">Security</option>
            <option value="INFRASTRUCTURE">Infrastructure</option>
            <option value="SERVICE_DEGRADATION">Service Degradation</option>
          </select>
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Assigned Team</label>
          <select value={form.assignedTeamId} onChange={set('assignedTeamId')}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none">
            <option value="">None</option>
            {teams.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
          </select>
        </div>
      </div>

      <button type="submit" disabled={loading}
        className="rounded-lg bg-blue-600 px-6 py-2 font-medium text-white hover:bg-blue-700 disabled:opacity-50">
        {loading ? 'Saving...' : submitLabel}
      </button>
    </form>
  );
}
