import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import RoleSelection from "./pages/RoleSelection";
import MentorLogin from "./pages/MentorLogin";
import ProtectedRoute from "./components/ProtectedRoute";
import ParentLogin from "./pages/ParentLogin";
import AttendanceDashboard from "./pages/AttendanceDashboard"; 
import AdminLogin from "./pages/AdminLogin";
import AdminMasterDashboard from "./pages/AdminMasterDashboard"; 
import MentorNewDashboard from "./pages/MentorNewDashboard"; 


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/role-selection" />} />
        <Route path="/role-selection" element={<RoleSelection />} />

        {/* Mentor Routes */}
        <Route path="/mentor" element={<MentorLogin />} />
        <Route
          path="/mentor/dashboard"
          element={
            <ProtectedRoute>
              <MentorNewDashboard />
            </ProtectedRoute>
          }
        />

        {/* Parent Routes */}
        <Route path="/parent" element={<ParentLogin />} />
        
        <Route
          path="/parent/dashboard"
          element={
            <ProtectedRoute>
              <AttendanceDashboard />
            </ProtectedRoute>
          }
        />

        {/* Admin Routes */}
        <Route path="/admin" element={<AdminLogin />} />
        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute>
              <AdminMasterDashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;