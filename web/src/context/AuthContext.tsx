import { createContext, useState, useEffect, type ReactNode } from 'react';
import type { AuthResponse } from '../types';

interface AuthUser {
  username: string;
  roles: string[];
  token: string;
}

interface AuthContextType {
  user: AuthUser | null;
  login: (data: AuthResponse) => void;
  logout: () => void;
  hasRole: (role: string) => boolean;
  hasAnyRole: (...roles: string[]) => boolean;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  login: () => {},
  logout: () => {},
  hasRole: () => false,
  hasAnyRole: () => false,
});

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
      localStorage.setItem('token', user.token);
    } else {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
  }, [user]);

  const login = (data: AuthResponse) => {
    setUser({ username: data.username, roles: data.roles, token: data.token });
  };

  const logout = () => setUser(null);

  const hasRole = (role: string) => user?.roles.includes(role) ?? false;
  const hasAnyRole = (...roles: string[]) => roles.some((r) => hasRole(r));

  return (
    <AuthContext.Provider value={{ user, login, logout, hasRole, hasAnyRole }}>
      {children}
    </AuthContext.Provider>
  );
}
