import type { IncidentStatus, Severity } from '../../types';

interface Props {
  status: string;
  severity: string;
  onStatusChange: (v: string) => void;
  onSeverityChange: (v: string) => void;
}

const statuses: IncidentStatus[] = ['OPEN', 'ACKNOWLEDGED', 'RESOLVED', 'CLOSED'];
const severities: Severity[] = ['P1', 'P2', 'P3', 'P4'];

export default function IncidentFilters({ status, severity, onStatusChange, onSeverityChange }: Props) {
  return (
    <div className="flex gap-3">
      <select
        value={status}
        onChange={(e) => onStatusChange(e.target.value)}
        className="rounded-lg border border-gray-300 px-3 py-1.5 text-sm focus:border-blue-500 focus:outline-none"
      >
        <option value="">All Statuses</option>
        {statuses.map((s) => <option key={s} value={s}>{s}</option>)}
      </select>
      <select
        value={severity}
        onChange={(e) => onSeverityChange(e.target.value)}
        className="rounded-lg border border-gray-300 px-3 py-1.5 text-sm focus:border-blue-500 focus:outline-none"
      >
        <option value="">All Severities</option>
        {severities.map((s) => <option key={s} value={s}>{s}</option>)}
      </select>
    </div>
  );
}
