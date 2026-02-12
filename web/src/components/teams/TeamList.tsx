import type { TeamResponse } from '../../types';

interface Props {
  teams: TeamResponse[];
}

export default function TeamList({ teams }: Props) {
  if (teams.length === 0) {
    return <p className="py-8 text-center text-gray-500">No teams yet.</p>;
  }

  return (
    <div className="overflow-hidden rounded-xl border border-gray-200 bg-white">
      <table className="w-full text-left text-sm">
        <thead className="border-b border-gray-200 bg-gray-50">
          <tr>
            <th className="px-4 py-3 font-medium text-gray-700">Name</th>
            <th className="px-4 py-3 font-medium text-gray-700">Members</th>
            <th className="px-4 py-3 font-medium text-gray-700">Created</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {teams.map((team) => (
            <tr key={team.id} className="hover:bg-gray-50">
              <td className="px-4 py-3 font-medium text-gray-900">{team.name}</td>
              <td className="px-4 py-3 text-gray-600">
                {team.memberUsernames.length > 0 ? team.memberUsernames.join(', ') : '-'}
              </td>
              <td className="px-4 py-3 text-gray-500">{new Date(team.createdAt).toLocaleDateString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
