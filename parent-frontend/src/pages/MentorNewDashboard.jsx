import { useState, useEffect } from "react";
import api from "../services/api";
import Navbar from "../components/Navbar";
import "./MentorNewDashboard.css"; 

const MentorNewDashboard = () => {
  const [activeTab, setActiveTab] = useState("upload");
  const [message, setMessage] = useState("");
  const [myStudents, setMyStudents] = useState([]);
  const [file, setFile] = useState(null);

  // Form state for individual progress
  const [progressForm, setProgressForm] = useState({
    rollNumber: "",
    subjectName: "",
    marks: "",
    remarks: ""
  });

  // Fetch only students assigned to this mentor
  const fetchMyStudents = async () => {
    try {
      const res = await api.get("/mentor/my");
      setMyStudents(res.data);
    } catch (err) {
      console.error("Error fetching students", err);
    }
  };

  useEffect(() => {
    if (activeTab === "myStudents") fetchMyStudents();
  }, [activeTab]);

  // --- NEW: Handle Semester Mapping Reset ---
  const handleSemesterReset = async () => {
    const confirmReset = window.confirm(
      "⚠️ DANGER: This will delete ALL current student-subject mappings. Only do this at the start of a NEW SEMESTER. Proceed?"
    );

    if (confirmReset) {
      try {
        setMessage("⏳ Wiping old mappings...");
        await api.delete("/mentor/attendance/reset-mappings");
        setMessage("✅ Success! Old mappings cleared. Please upload the new CSV now.");
        setMyStudents([]); // Clear local state as well
      } catch (err) {
        setMessage("❌ Error resetting mappings. Please check server logs.");
      }
    }
  };

  // Handle Attendance File Upload
  const handleFileUpload = async (e) => {
    e.preventDefault();
    if (!file) return setMessage("⚠️ Please select a file first!");

    const formData = new FormData();
    formData.append("file", file);

    try {
      setMessage("⏳ Processing Attendance...");
      await api.post("/mentor/attendance/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      setMessage("✅ Attendance Uploaded & Parents Notified!");
      setFile(null);
      if (activeTab === "myStudents") fetchMyStudents(); // Refresh list
    } catch (err) {
      setMessage("❌ Upload Failed. Check file format.");
    }
  };

  const handleAddProgress = async () => {
    if(!progressForm.rollNumber || !progressForm.marks) {
        setMessage("⚠️ Roll Number and Marks are required!");
        return;
    }
    try {
      await api.post("/mentor/add", progressForm);
      setMessage("✅ Progress added successfully!");
      setProgressForm({ rollNumber: "", subjectName: "", marks: "", remarks: "" });
    } catch (err) {
      setMessage("❌ Failed to add progress.");
    }
  };

  return (
    <div className="mentor-dashboard-container">
      <Navbar />
      <div className="mentor-layout">
        <aside className="mentor-sidebar">
          <h3>Mentor Portal</h3>
          <ul>
            <li className={activeTab === "upload" ? "active" : ""} onClick={() => setActiveTab("upload")}>📤 Upload Attendance</li>
            <li className={activeTab === "progress" ? "active" : ""} onClick={() => setActiveTab("progress")}>📝 Student Progress</li>
            <li className={activeTab === "myStudents" ? "active" : ""} onClick={() => setActiveTab("myStudents")}>👨‍🎓 My Students</li>
          </ul>
        </aside>

        <main className="mentor-main-content">
          {message && <div className="status-toast">{message}</div>}

          {/* ATTENDANCE UPLOAD & RESET SECTION */}
          {activeTab === "upload" && (
            <div className="mentor-sections">
              
              {/* NEW SEMESTER RESET BLOCK */}
              <div className="mentor-card" style={{ border: '2px solid #e74c3c', backgroundColor: '#fff9f9', marginBottom: '30px' }}>
                <h2 style={{ color: '#c0392b' }}>Semester Transition</h2>
                <p style={{ marginBottom: '15px' }}>Clicking this button will wipe all current student-subject links to prepare for a new semester's data.</p>
                <button onClick={handleSemesterReset} className="primary-btn" style={{ backgroundColor: '#e74c3c', maxWidth: '300px' }}>
                  🗑️ Reset All Mappings
                </button>
              </div>

              <div className="mentor-card">
                <h2>Upload New Attendance CSV</h2>
                <p style={{color: '#666', marginBottom: '20px'}}>
                  Upload the master attendance/mapping CSV to establish current semester links.
                </p>
                
                <div className="sync-section">
                    <form onSubmit={handleFileUpload} className="mentor-form">
                      <label style={{fontSize: '0.85rem', fontWeight: '600', display: 'block', marginBottom: '5px'}}>Select CSV File:</label>
                      <input type="file" accept=".csv" onChange={(e) => setFile(e.target.files[0])} />
                      <button type="submit" className="primary-btn">Sync New Mappings</button>
                    </form>
                </div>
              </div>
            </div>
          )}

          {/* ADD PROGRESS SECTION */}
          {activeTab === "progress" && (
            <div className="mentor-card">
              <h2>Add Student Progress</h2>
              <div className="mentor-form">
                <input type="text" placeholder="Student Roll Number" value={progressForm.rollNumber} onChange={(e) => setProgressForm({...progressForm, rollNumber: e.target.value})} />
                <input type="text" placeholder="Subject Name" value={progressForm.subjectName} onChange={(e) => setProgressForm({...progressForm, subjectName: e.target.value})} />
                <input type="number" placeholder="Marks Obtained" value={progressForm.marks} onChange={(e) => setProgressForm({...progressForm, marks: e.target.value})} />
                <textarea rows="4" placeholder="Faculty Remarks" value={progressForm.remarks} onChange={(e) => setProgressForm({...progressForm, remarks: e.target.value})} />
                <button onClick={handleAddProgress} className="primary-btn">Submit to Parent Portal</button>
              </div>
            </div>
          )}

          {/* MY STUDENTS TABLE SECTION */}
          {activeTab === "myStudents" && (
            <div className="mentor-card" style={{maxWidth: '100%'}}>
              <h2>My Assigned Students</h2>
              <table className="mentor-table">
                <thead>
                  <tr>
                    <th>Roll Number</th>
                    <th>Subject</th>
                    <th>Attendance %</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {myStudents.length > 0 ? (
                    myStudents.map((s, idx) => (
                      <tr key={idx}>
                        <td><strong>{s.rollNumber}</strong></td>
                        <td>{s.subjectName}</td>
                        <td>{s.attendancePercent}%</td>
                        <td>
                            <span style={{ 
                                padding: '4px 8px', 
                                borderRadius: '4px', 
                                fontSize: '0.8rem',
                                backgroundColor: s.attendancePercent < 75 ? '#ffebee' : '#e8f5e9',
                                color: s.attendancePercent < 75 ? '#c62828' : '#2e7d32'
                            }}>
                                {s.attendancePercent < 75 ? 'Low' : 'Good'}
                            </span>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="4" style={{textAlign: 'center', padding: '30px'}}>No active mappings found. Use the Upload tab to sync new data.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default MentorNewDashboard;