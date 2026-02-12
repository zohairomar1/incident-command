import { Link } from 'react-router-dom';
import type { IncidentResponse } from '../../types';
import { StatusBadge, SeverityBadge } from './StatusBadge';

interface Props {
  incidents: IncidentResponse[];
}

export default function IncidentTable({ incidents }: Props) {
  if (incidents.length === 0) {
    return <p className="py-8 text-center text-gray-500">No incidents found.</p>;
  }

  return (
    <div className="overflow-hidden rounded-xl border border-gray-200 bg-white">
      <table className="w-full text-left text-sm">
        <thead className="border-b border-gray-200 bg-gray-50">
          <tr>
            <th className="px-4 py-3 font-medium text-gray-700">Title</th>
            <th className="px-4 py-3 font-medium text-gray-700">Severity</th>
            <th className="px-4 py-3 font-medium text-gray-700">Status</th>
            <th className="px-4 py-3 font-medium text-gray-700">Type</th>
            <th className="px-4 py-3 font-medium text-gray-700">Assigned Team</th>
            <th className="px-4 py-3 font-medium text-gray-700">Created</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {incidents.map((inc) => (
            <tr key={inc.id} className="hover:bg-gray-50">
              <td className="px-4 py-3">
                <Link to={`/incidents/${inc.id}`} className="text-blue-600 hover:underline">
                  {inc.title}
                </Link>
              </td>
              <td className="px-4 py-3"><SeverityBadge severity={inc.severity} /></td>
              <td className="px-4 py-3"><StatusBadge status={inc.status} /></td>
              <td className="px-4 py-3 text-gray-600">{inc.type.replace('_', ' ')}</td>
              <td className="px-4 py-3 text-gray-600">{inc.assignedTeamName || '-'}</td>
              <td className="px-4 py-3 text-gray-500">{new Date(inc.createdAt).toLocaleDateString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
