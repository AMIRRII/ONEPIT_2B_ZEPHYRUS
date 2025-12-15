package bhrms.dao;

import bhrms.DatabaseConnection;
import bhrms.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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