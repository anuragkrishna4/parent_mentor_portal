import { useNavigate } from "react-router-dom";
import "./RoleSelection.css";

const RoleSelection = () => {
  const navigate = useNavigate();

  const roles = [
    {
      title: "Admin",
      icon: "👑",
      desc: "Manage students, parents, and global system settings.",
      path: "/admin",
      color: "#3498db"
    },
    {
      title: "Mentor",
      icon: "👨‍🏫",
      desc: "Track student progress and upload attendance data.",
      path: "/mentor",
      color: "#27ae60"
    },
    {
      title: "Parent",
      icon: "👨‍👩‍👧",
      desc: "Monitor your child's academic performance and attendance.",
      path: "/parent",
      color: "#8e44ad"
    }
  ];

  return (
    <div className="role-selection-wrapper">
      <div className="role-header">
        <h1>Parent-Mentor Portal</h1>
        <p>Please select your role to continue to the dashboard</p>
      </div>

      <div className="role-grid">
        {roles.map((role) => (
          <div 
            key={role.title} 
            className="role-card" 
            onClick={() => navigate(role.path)}
          >
            <div className="role-icon" style={{ backgroundColor: `${role.color}15`, color: role.color }}>
              {role.icon}
            </div>
            <h3>{role.title}</h3>
            <p>{role.desc}</p>
            <div className="role-arrow">→</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RoleSelection;