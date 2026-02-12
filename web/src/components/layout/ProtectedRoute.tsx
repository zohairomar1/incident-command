import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface Props {
  children: React.ReactNode;
  roles?: string[];
}

export default function ProtectedRoute({ children, roles }: Props) {
  const { user, hasAnyRole } = useAuth();

  if (!user) return <Navigate to="/login" replace />;
  if (roles && !hasAnyRole(...roles)) return <Navigate to="/dashboard" replace />;

  return <>{children}</>;
}
