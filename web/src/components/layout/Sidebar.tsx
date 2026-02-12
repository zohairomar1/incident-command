import { NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const linkClass = ({ isActive }: { isActive: boolean }) =>
  `block rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
    isActive ? 'bg-blue-50 text-blue-700' : 'text-gray-700 hover:bg-gray-100'
  }`;

export default function Sidebar() {
  const { user, hasRole, logout } = useAuth();

  return (
    <aside className="flex h-screen w-60 flex-col border-r border-gray-200 bg-white">
      <div className="border-b border-gray-200 p-4">
        <h1 className="text-lg font-bold text-gray-900">Incident Command</h1>
        <p className="mt-1 text-xs text-gray-500">{user?.username}</p>
      </div>

      <nav className="flex-1 space-y-1 p-3">
        <NavLink to="/dashboard" className={linkClass}>Dashboard</NavLink>
        <NavLink to="/incidents" className={linkClass}>Incidents</NavLink>
        {hasRole('ADMIN') && (
          <NavLink to="/teams" className={linkClass}>Teams</NavLink>
        )}
      </nav>

      <div className="border-t border-gray-200 p-3">
        <button
          onClick={logout}
          className="w-full rounded-lg px-4 py-2 text-left text-sm font-medium text-gray-700 hover:bg-gray-100"
        >
          Sign Out
        </button>
      </div>
    </aside>
  );
}
