import { useState } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom";

const AdminLogin = () => {

  const [form, setForm] = useState({ username: "", password: "" });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await api.post("/auth/login", form);

      // Store token
      localStorage.setItem("token", res.data);

      // Redirect to admin dashboard
      navigate("/admin/dashboard");

    } catch (err) {
      alert("Invalid Admin Credentials");
    }
  };

  return (
    <div className="auth-container">
      <h2>Admin Login</h2>

      <form onSubmit={handleSubmit} className="auth-form">
        <input
          type="text"
          placeholder="Username"
          onChange={(e) =>
            setForm({ ...form, username: e.target.value })
          }
        />

        <input
          type="password"
          placeholder="Password"
          onChange={(e) =>
            setForm({ ...form, password: e.target.value })
          }
        />

        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default AdminLogin;