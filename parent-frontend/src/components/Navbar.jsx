import { useNavigate } from "react-router-dom";
import "./Navbar.css";

const Navbar = () => {
  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/role-selection");
  };

  return (
    <div className="navbar">
      <h3>Parent-Mentor Portal</h3>
      <button className="logout-btn" onClick={logout}>Logout</button>
    </div>
  );
};

export default Navbar;