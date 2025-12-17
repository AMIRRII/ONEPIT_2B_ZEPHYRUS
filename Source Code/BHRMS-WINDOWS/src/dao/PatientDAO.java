package bhrms.dao;

import bhrms.DatabaseConnection;
import bhrms.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PatientDAO {
    
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (first_name, last_name, middle_name, birth_date, age, gender, " +
                    "address, contact_number, barangay, blood_type, pre_existing_conditions, allergies, " +
                    "emergency_contact_name, emergency_contact_number, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setString(3, patient.getMiddleName());
            pstmt.setDate(4, patient.getBirthDate());
            pstmt.setInt(5, patient.getAge());
            pstmt.setString(6, patient.getGender());
            pstmt.setString(7, patient.getAddress());
            pstmt.setString(8, patient.getContactNumber());
            pstmt.setString(9, patient.getBarangay());
            pstmt.setString(10, patient.getBloodType());
            pstmt.setString(11, patient.getPreExistingConditions());
            pstmt.setString(12, patient.getAllergies());
            pstmt.setString(13, patient.getEmergencyContactName());
            pstmt.setString(14, patient.getEmergencyContactNumber());
            pstmt.setInt(15, patient.getCreatedBy());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        patient.setPatientId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? " +
                    "OR barangay LIKE ? OR contact_number LIKE ? ORDER BY last_name, first_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
    
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, middle_name = ?, " +
                    "birth_date = ?, age = ?, gender = ?, address = ?, contact_number = ?, " +
                    "barangay = ?, blood_type = ?, pre_existing_conditions = ?, allergies = ?, " +
                    "emergency_contact_name = ?, emergency_contact_number = ? WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setString(3, patient.getMiddleName());
            pstmt.setDate(4, patient.getBirthDate());
            pstmt.setInt(5, patient.getAge());
            pstmt.setString(6, patient.getGender());
            pstmt.setString(7, patient.getAddress());
            pstmt.setString(8, patient.getContactNumber());
            pstmt.setString(9, patient.getBarangay());
            pstmt.setString(10, patient.getBloodType());
            pstmt.setString(11, patient.getPreExistingConditions());
            pstmt.setString(12, patient.getAllergies());
            pstmt.setString(13, patient.getEmergencyContactName());
            pstmt.setString(14, patient.getEmergencyContactNumber());
            pstmt.setInt(15, patient.getPatientId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deactivatePatient(int patientId) {
        String sql = "UPDATE patients SET is_active = FALSE WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(int patientId) {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction
        
        // First, delete all medical records for this patient
        String deleteRecordsSQL = "DELETE FROM medical_records WHERE patient_id = ?";
        try (PreparedStatement pstmt1 = conn.prepareStatement(deleteRecordsSQL)) {
            pstmt1.setInt(1, patientId);
            pstmt1.executeUpdate();
        }
        
        // Then delete the patient
        String deletePatientSQL = "DELETE FROM patients WHERE patient_id = ?";
        try (PreparedStatement pstmt2 = conn.prepareStatement(deletePatientSQL)) {
            pstmt2.setInt(1, patientId);
            int affectedRows = pstmt2.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback(); // Rollback if patient not found
                return false;
            }
        }
    } catch (SQLException e) {
        try {
            if (conn != null) conn.rollback(); // Rollback on error
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.err.println("Error deleting patient: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true); // Reset auto-commit
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }
    
    public int getTotalPatientsCount() {
        String sql = "SELECT COUNT(*) as total FROM patients WHERE is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total patients count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getPatientsCountByBarangay(String barangay) {
        String sql = "SELECT COUNT(*) as total FROM patients WHERE barangay = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, barangay);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting patients count by barangay: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<String> getAllBarangays() {
        List<String> barangays = new ArrayList<>();
        String sql = "SELECT DISTINCT barangay FROM patients WHERE barangay IS NOT NULL AND barangay != '' ORDER BY barangay";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                barangays.add(rs.getString("barangay"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all barangays: " + e.getMessage());
            e.printStackTrace();
        }
        return barangays;
    }

    // Add these methods to PatientDAO.java:

public Map<String, Integer> getAgeGroupDistribution() {
    Map<String, Integer> ageGroups = new LinkedHashMap<>();
    
    String sql = "SELECT " +
                 "SUM(CASE WHEN age BETWEEN 0 AND 12 THEN 1 ELSE 0 END) as child, " +
                 "SUM(CASE WHEN age BETWEEN 13 AND 19 THEN 1 ELSE 0 END) as teen, " +
                 "SUM(CASE WHEN age BETWEEN 20 AND 59 THEN 1 ELSE 0 END) as adult, " +
                 "SUM(CASE WHEN age >= 60 THEN 1 ELSE 0 END) as senior " +
                 "FROM patients WHERE is_active = TRUE";
    
    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery(sql)) {
        
        if (rs.next()) {
            ageGroups.put("0-12 years", rs.getInt("child"));
            ageGroups.put("13-19 years", rs.getInt("teen"));
            ageGroups.put("20-59 years", rs.getInt("adult"));
            ageGroups.put("60+ years", rs.getInt("senior"));
        }
    } catch (SQLException e) {
        System.err.println("Error getting age group distribution: " + e.getMessage());
        e.printStackTrace();
    }
    return ageGroups;
}

public Map<String, Integer> getGenderDistribution() {
    Map<String, Integer> genderDist = new LinkedHashMap<>();
    
    String sql = "SELECT gender, COUNT(*) as count FROM patients " +
                 "WHERE is_active = TRUE AND gender IS NOT NULL " +
                 "GROUP BY gender ORDER BY gender";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            genderDist.put(rs.getString("gender"), rs.getInt("count"));
        }
    } catch (SQLException e) {
        System.err.println("Error getting gender distribution: " + e.getMessage());
        e.printStackTrace();
    }
    return genderDist;
}

public Map<String, Integer> getBarangayDistribution() {
    Map<String, Integer> barangayDist = new LinkedHashMap<>();
    
    String sql = "SELECT barangay, COUNT(*) as count FROM patients " +
                 "WHERE is_active = TRUE AND barangay IS NOT NULL AND barangay != '' " +
                 "GROUP BY barangay ORDER BY count DESC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            barangayDist.put(rs.getString("barangay"), rs.getInt("count"));
        }
    } catch (SQLException e) {
        System.err.println("Error getting barangay distribution: " + e.getMessage());
        e.printStackTrace();
    }
    return barangayDist;
}

public Map<String, Integer> getBloodTypeDistribution() {
    Map<String, Integer> bloodTypeDist = new LinkedHashMap<>();
    
    String sql = "SELECT " +
                 "SUM(CASE WHEN blood_type = 'O+' THEN 1 ELSE 0 END) as O_positive, " +
                 "SUM(CASE WHEN blood_type = 'A+' THEN 1 ELSE 0 END) as A_positive, " +
                 "SUM(CASE WHEN blood_type = 'B+' THEN 1 ELSE 0 END) as B_positive, " +
                 "SUM(CASE WHEN blood_type = 'AB+' THEN 1 ELSE 0 END) as AB_positive, " +
                 "SUM(CASE WHEN blood_type = 'O-' THEN 1 ELSE 0 END) as O_negative, " +
                 "SUM(CASE WHEN blood_type = 'A-' THEN 1 ELSE 0 END) as A_negative, " +
                 "SUM(CASE WHEN blood_type = 'B-' THEN 1 ELSE 0 END) as B_negative, " +
                 "SUM(CASE WHEN blood_type = 'AB-' THEN 1 ELSE 0 END) as AB_negative, " +
                 "SUM(CASE WHEN blood_type IS NULL OR blood_type = '' THEN 1 ELSE 0 END) as unknown " +
                 "FROM patients WHERE is_active = TRUE";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        if (rs.next()) {
            bloodTypeDist.put("O+", rs.getInt("O_positive"));
            bloodTypeDist.put("A+", rs.getInt("A_positive"));
            bloodTypeDist.put("B+", rs.getInt("B_positive"));
            bloodTypeDist.put("AB+", rs.getInt("AB_positive"));
            bloodTypeDist.put("O-", rs.getInt("O_negative"));
            bloodTypeDist.put("A-", rs.getInt("A_negative"));
            bloodTypeDist.put("B-", rs.getInt("B_negative"));
            bloodTypeDist.put("AB-", rs.getInt("AB_negative"));
            bloodTypeDist.put("Unknown", rs.getInt("unknown"));
        }
    } catch (SQLException e) {
        System.err.println("Error getting blood type distribution: " + e.getMessage());
        e.printStackTrace();
    }
    return bloodTypeDist;
}
    
    // Helper method to map ResultSet to Patient (if not already there)
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setMiddleName(rs.getString("middle_name"));
        patient.setBirthDate(rs.getDate("birth_date"));
        patient.setAge(rs.getInt("age"));
        patient.setGender(rs.getString("gender"));
        patient.setAddress(rs.getString("address"));
        patient.setContactNumber(rs.getString("contact_number"));
        patient.setBarangay(rs.getString("barangay"));
        patient.setBloodType(rs.getString("blood_type"));
        patient.setPreExistingConditions(rs.getString("pre_existing_conditions"));
        patient.setAllergies(rs.getString("allergies"));
        patient.setEmergencyContactName(rs.getString("emergency_contact_name"));
        patient.setEmergencyContactNumber(rs.getString("emergency_contact_number"));
        patient.setActive(rs.getBoolean("is_active"));
        patient.setCreatedBy(rs.getInt("created_by"));
        patient.setCreatedAt(rs.getTimestamp("created_at"));
        patient.setUpdatedAt(rs.getTimestamp("updated_at"));
        return patient;
    }
}