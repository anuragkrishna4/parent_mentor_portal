import { useState } from "react";
import api from "../services/api";
import Navbar from "../components/Navbar";
import "./AdminMasterDashboard.css";

const AdminMasterDashboard = () => {
  const [activeTab, setActiveTab] = useState("overview");
  const [message, setMessage] = useState("");
  const [selectedFile, setSelectedFile] = useState(null);

  // Form states
  const [studentForm, setStudentForm] = useState({ name: "", rollNumber: "" });
  const [subjectForm, setSubjectForm] = useState({ subjectName: "" });
  const [assignSub, setAssignSub] = useState({ rollNumber: "", subjectId: "" });
  const [parentForm, setParentForm] = useState({ username: "", parentName: "" });
  const [assignParent, setAssignParent] = useState({ parentUsername: "", rollNumber: "" });

  // Generic handler for single API calls
  const handleAction = async (endpoint, data, method = "post") => {
    try {
      const res = await api[method](endpoint, data);
      setMessage(`✅ Success: ${typeof res.data === 'string' ? res.data : 'Operation Completed'}`);
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      setMessage("❌ Error: Operation failed.");
      console.error(err);
    }
  };

  // Bulk CSV Upload Handler
  const handleBulkUpload = async () => {
    if (!selectedFile) {
      setMessage("⚠️ Please select a CSV file first!");
      return;
    }
    const formData = new FormData();
    formData.append("file", selectedFile);
    setMessage("⏳ Uploading and Syncing Data...");

    try {
      const res = await api.post("/parent/bulk-upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setMessage(`✅ ${res.data}`);
      setSelectedFile(null);
    } catch (err) {
      setMessage("❌ Bulk Upload Failed. Check file format.");
    }
  };

  // Global Sync Student Registration (Mentor + Parent DB)
  const handleDualStudentRegistration = async () => {
    if (!studentForm.name || !studentForm.rollNumber) {
      setMessage("⚠️ Name and Roll Number are required!");
      return;
    }
    setMessage("⏳ Syncing student to databases...");
    try {
      const payload = { name: studentForm.name, rollNumber: studentForm.rollNumber };
      await Promise.all([
        api.post("/mentor/student/create", payload),
        api.post("/parent/student/create", payload)
      ]);
      setMessage("✅ Success: Student created in Mentor & Parent systems!");
      setStudentForm({ name: "", rollNumber: "" });
    } catch (err) {
      setMessage("❌ Sync Failed: Check if Roll Number already exists.");
    }
  };

  return (
    <div className="admin-master-container">
      <Navbar />
      <div className="admin-layout">
        <aside className="admin-sidebar">
          <h3>Admin Control</h3>
          <ul>
            <li className={activeTab === "overview" ? "active" : ""} onClick={() => setActiveTab("overview")}>📊 Overview</li>
            <li className={activeTab === "students" ? "active" : ""} onClick={() => setActiveTab("students")}>🎓 Student Mgmt</li>
            <li className={activeTab === "subjects" ? "active" : ""} onClick={() => setActiveTab("subjects")}>📚 Subject Mgmt</li>
            <li className={activeTab === "parents" ? "active" : ""} onClick={() => setActiveTab("parents")}>👪 Parent Mgmt</li>
          </ul>
        </aside>

        <main className="admin-main-content">
          {message && <div className="status-toast">{message}</div>}

          {/* STUDENT MANAGEMENT */}
          {activeTab === "students" && (
            <div className="admin-card">
              <h2>Register New Student (Global Sync)</h2>
              <div className="admin-form-group">
                <input type="text" placeholder="Student Name" value={studentForm.name} onChange={(e) => setStudentForm({...studentForm, name: e.target.value})} />
                <input type="text" placeholder="Roll Number" value={studentForm.rollNumber} onChange={(e) => setStudentForm({...studentForm, rollNumber: e.target.value})} />
                <button onClick={handleDualStudentRegistration}>Create & Sync Student</button>
              </div>
            </div>
          )}

          {/* SUBJECT MANAGEMENT */}
          {activeTab === "subjects" && (
            <div className="admin-sections">
              <div className="admin-card">
                <h2>Add New Subject</h2>
                <input type="text" placeholder="Subject Name" value={subjectForm.subjectName} onChange={(e) => setSubjectForm({subjectName: e.target.value})} />
                <button onClick={() => handleAction("/mentor/subject/add", subjectForm)}>Add Subject</button>
              </div>

              <div className="admin-card" style={{marginTop: '20px'}}>
                <h2>Assign Subject to Student</h2>
                <input type="text" placeholder="Student Roll Number" onChange={(e) => setAssignSub({...assignSub, rollNumber: e.target.value})} />
                <input type="text" placeholder="Subject ID" onChange={(e) => setAssignSub({...assignSub, subjectId: e.target.value})} />
                <button onClick={() => handleAction("/mentor/assign", assignSub)}>Assign Subject</button>
              </div>
            </div>
          )}

          {/* PARENT MANAGEMENT */}
          {activeTab === "parents" && (
            <div className="admin-sections">
              <div className="admin-card bulk-upload-section">
                <h2>📦 CSV Bulk Upload</h2>
                <p style={{fontSize: '0.85rem', color: '#666', marginBottom: '10px'}}>Syncs Students, Parents, and Mappings at once.</p>
                <input type="file" accept=".csv" onChange={(e) => setSelectedFile(e.target.files[0])} />
                <button onClick={handleBulkUpload} style={{backgroundColor: '#27ae60', marginTop: '10px'}}>Upload & Sync CSV</button>
              </div>

              <div className="admin-card">
                <h2>Register Single Parent</h2>
                <input type="text" placeholder="Phone Number (Username)" value={parentForm.username} onChange={(e) => setParentForm({...parentForm, username: e.target.value})} />
                <input type="text" placeholder="Parent Name" value={parentForm.parentName} onChange={(e) => setParentForm({...parentForm, parentName: e.target.value})} />
                <button onClick={() => handleAction("/parent/create", parentForm)}>Create Parent</button>
              </div>

              <div className="admin-card" style={{marginTop: '20px'}}>
                <h2>Manual Link Child</h2>
                <input type="text" placeholder="Parent Username (Phone)" onChange={(e) => setAssignParent({...assignParent, parentUsername: e.target.value})} />
                <input type="text" placeholder="Student Roll Number" onChange={(e) => setAssignParent({...assignParent, rollNumber: e.target.value})} />
                <button onClick={() => handleAction(`/parent/assign?parentUsername=${assignParent.parentUsername}&rollNumber=${assignParent.rollNumber}`, null)}>Link Child Profile</button>
              </div>
            </div>
          )}

          {/* OVERVIEW */}
          {activeTab === "overview" && (
            <div className="overview-grid">
               <div className="stat-box"><h3>System Status</h3><p>Operational</p></div>
               <div className="stat-box"><h3>Sync Status</h3><p>Bulk CSV Sync Enabled</p></div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default AdminMasterDashboard;