import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/layout/ProtectedRoute';
import AppLayout from './components/layout/AppLayout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import IncidentListPage from './pages/IncidentListPage';
import IncidentDetailPage from './pages/IncidentDetailPage';
import IncidentCreatePage from './pages/IncidentCreatePage';
import IncidentEditPage from './pages/IncidentEditPage';
import TeamManagementPage from './pages/TeamManagementPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route element={<ProtectedRoute><AppLayout /></ProtectedRoute>}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/incidents" element={<IncidentListPage />} />
            <Route path="/incidents/new" element={
              <ProtectedRoute roles={['ADMIN', 'RESPONDER']}><IncidentCreatePage /></ProtectedRoute>
            } />
            <Route path="/incidents/:id" element={<IncidentDetailPage />} />
            <Route path="/incidents/:id/edit" element={
              <ProtectedRoute roles={['ADMIN', 'RESPONDER']}><IncidentEditPage /></ProtectedRoute>
            } />
            <Route path="/teams" element={
              <ProtectedRoute roles={['ADMIN']}><TeamManagementPage /></ProtectedRoute>
            } />
          </Route>

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
