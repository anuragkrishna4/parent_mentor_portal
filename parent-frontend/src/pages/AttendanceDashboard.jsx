import { useEffect, useState } from "react";
import api from "../services/api";
import Navbar from "../components/Navbar";

const AttendanceDashboard = () => {
  const [attendanceData, setAttendanceData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAttendance = async () => {
      try {
        const res = await api.get("/parent/report");
        setAttendanceData(res.data);
      } catch (err) {
        console.error("Error fetching attendance", err);
      } finally {
        setLoading(false);
      }
    };
    fetchAttendance();
  }, []);

  // 1. Group records by Roll Number
  const groupedData = attendanceData.reduce((acc, record) => {
    const roll = record.rollNumber;
    if (!acc[roll]) {
      acc[roll] = {
        name: record.studentSubject.student.name,
        rollNumber: roll,
        subjects: [],
      };
    }
    acc[roll].subjects.push(record);
    return acc;
  }, {});

  if (loading) return <h3 className="loading">Loading...</h3>;

  return (
    <div className="attendance-container" style={{ padding: '20px' }}>
      <Navbar />
      <h2 className="page-title">Child Attendance Report</h2>

      {Object.values(groupedData).map((student) => (
        <div key={student.rollNumber} className="student-report-section" style={{ marginBottom: '40px', background: '#fff', borderRadius: '8px', padding: '20px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)' }}>
          
          {/* 2. Student Header (Shows once only) */}
          <div className="student-header" style={{ borderBottom: '2px solid #eee', marginBottom: '15px', paddingBottom: '10px' }}>
            <h3 style={{ margin: 0, color: '#333' }}>{student.name}</h3>
            <p style={{ margin: '5px 0', color: '#666' }}>Roll Number: <strong>{student.rollNumber}</strong></p>
          </div>

          {/* 3. List Format Table */}
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ textAlign: 'left', background: '#f8f9fa' }}>
                <th style={{ padding: '12px' }}>Subject</th>
                <th>Total</th>
                <th>Present</th>
                <th>Absent</th>
                <th>Percentage</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {student.subjects.map((sub) => (
                <tr key={sub.id} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '12px' }}>{sub.subjectName}</td>
                  <td>{sub.totalClasses}</td>
                  <td>{sub.present}</td>
                  <td style={{ color: sub.absences > 5 ? '#dc3545' : 'inherit' }}>{sub.absences}</td>
                  <td style={{ fontWeight: 'bold' }}>{sub.attendancePercent}%</td>
                  <td>
                    <span style={{
                      padding: '4px 8px',
                      borderRadius: '12px',
                      fontSize: '0.8rem',
                      background: sub.attendancePercent >= 75 ? '#e6f4ea' : '#fce8e6',
                      color: sub.attendancePercent >= 75 ? '#1e7e34' : '#d93025'
                    }}>
                      {sub.attendancePercent >= 75 ? 'On Track' : 'Shortage'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
};

export default AttendanceDashboard;