package bhrms.dao;

import bhrms.DatabaseConnection;
import bhrms.models.MedicalRecord;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {
    
    public boolean addMedicalRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (patient_id, visit_date, visit_type, temperature, " +
                    "blood_pressure, weight, height, symptoms, diagnosis, treatment, prescribed_meds, " +
                    "notes, follow_up_date, health_worker_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, record.getPatientId());
            pstmt.setDate(2, record.getVisitDate());
            pstmt.setString(3, record.getVisitType());
            
            if (record.getTemperature() != null) {
                pstmt.setBigDecimal(4, record.getTemperature());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }
            
            pstmt.setString(5, record.getBloodPressure());
            
            if (record.getWeight() != null) {
                pstmt.setBigDecimal(6, record.getWeight());
            } else {
                pstmt.setNull(6, Types.DECIMAL);
            }
            
            if (record.getHeight() != null) {
                pstmt.setBigDecimal(7, record.getHeight());
            } else {
                pstmt.setNull(7, Types.DECIMAL);
            }
            
            pstmt.setString(8, record.getSymptoms());
            pstmt.setString(9, record.getDiagnosis());
            pstmt.setString(10, record.getTreatment());
            pstmt.setString(11, record.getPrescribedMeds());
            pstmt.setString(12, record.getNotes());
            
            if (record.getFollowUpDate() != null) {
                pstmt.setDate(13, record.getFollowUpDate());
            } else {
                pstmt.setNull(13, Types.DATE);
            }
            
            pstmt.setInt(14, record.getHealthWorkerId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setRecordId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<MedicalRecord> getRecordsByPatientId(int patientId) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT mr.*, p.first_name, p.last_name FROM medical_records mr " +
                    "JOIN patients p ON mr.patient_id = p.patient_id " +
                    "WHERE mr.patient_id = ? ORDER BY mr.visit_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToMedicalRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    public List<MedicalRecord> getRecentRecords(int limit) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT mr.*, p.first_name, p.last_name FROM medical_records mr " +
                    "JOIN patients p ON mr.patient_id = p.patient_id " +
                    "ORDER BY mr.visit_date DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToMedicalRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public int getTotalVisitsCount() {
        String sql = "SELECT COUNT(*) as total FROM medical_records";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total visits count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getVisitsCountToday() {
        String sql = "SELECT COUNT(*) as total FROM medical_records WHERE DATE(visit_date) = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's visits count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getVisitsCountByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT COUNT(*) as total FROM medical_records WHERE visit_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting visits count by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<String> getCommonDiagnoses(int limit) {
        List<String> diagnoses = new ArrayList<>();
        String sql = "SELECT diagnosis, COUNT(*) as count FROM medical_records " +
                    "WHERE diagnosis IS NOT NULL AND diagnosis != '' " +
                    "GROUP BY diagnosis ORDER BY count DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                diagnoses.add(rs.getString("diagnosis") + " (" + rs.getInt("count") + " cases)");
            }
        } catch (SQLException e) {
            System.err.println("Error getting common diagnoses: " + e.getMessage());
            e.printStackTrace();
        }
        return diagnoses;
    }
    
    // Helper method (if not already there)
    private MedicalRecord mapResultSetToMedicalRecord(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setPatientId(rs.getInt("patient_id"));
        record.setVisitDate(rs.getDate("visit_date"));
        record.setVisitType(rs.getString("visit_type"));
        
        BigDecimal temp = rs.getBigDecimal("temperature");
        if (!rs.wasNull()) {
            record.setTemperature(temp);
        }
        
        record.setBloodPressure(rs.getString("blood_pressure"));
        
        BigDecimal weight = rs.getBigDecimal("weight");
        if (!rs.wasNull()) {
            record.setWeight(weight);
        }
        
        BigDecimal height = rs.getBigDecimal("height");
        if (!rs.wasNull()) {
            record.setHeight(height);
        }
        
        record.setSymptoms(rs.getString("symptoms"));
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setTreatment(rs.getString("treatment"));
        record.setPrescribedMeds(rs.getString("prescribed_meds"));
        record.setNotes(rs.getString("notes"));
        
        Date followUpDate = rs.getDate("follow_up_date");
        if (!rs.wasNull()) {
            record.setFollowUpDate(followUpDate);
        }
        
        record.setHealthWorkerId(rs.getInt("health_worker_id"));
        
        // Try to get patient name if included in query
        try {
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            if (firstName != null && lastName != null) {
                record.setPatientName(firstName + " " + lastName);
            }
        } catch (SQLException e) {
            // Column not in result set, ignore
        }
        
        record.setCreatedAt(rs.getTimestamp("created_at"));
        return record;
    }
}