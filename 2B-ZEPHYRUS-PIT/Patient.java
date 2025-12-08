import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class Patient {
    // Private Attributes (Encapsulation - Lesson 7)
    private int patient_id;
    private String fullName;
    private int age;
    private String sex;
    private String address;
    private String contactNumber;

    // --- Constructor 1 (For retrieving/editing existing patient - used by new Patient() in dialog) ---
    public Patient(int patient_id, String fullName, int age, String sex, String address, String contactNumber) {
        this.patient_id = patient_id;
        this.fullName = fullName;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.contactNumber = contactNumber;
    }
    
    // --- Constructor 2 (For creating a NEW patient) ---
    public Patient(String fullName, int age, String sex, String address, String contactNumber) {
        this.fullName = fullName;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.contactNumber = contactNumber;
        this.patient_id = -1; // Indicates a new record
    }

    // --- Getters (Required by the Dialog and Main Dashboard) ---
    public int getPatient_id() { return patient_id; }
    public String getFullName() { return fullName; }
    public int getAge() { return age; }
    public String getSex() { return sex; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }

    // --- CRUD Methods (Required by the Dialog and Main Dashboard) ---
    public boolean addPatient() {
        // Implementation for inserting into database (from previous step)
        // ... (requires DBConnection)
        return true; 
    }
    
    public boolean updatePatient() {
        // Implementation for updating database (from previous step)
        // ... (requires DBConnection)
        return true; 
    }
    
    public static Patient searchPatient(String searchTerm) {
        // Implementation for searching database (from previous step)
        // ... (requires DBConnection)
        return null;
    }
    
    public static DefaultTableModel getAllPatients() {
        // Implementation for reading all patients (from previous step)
        // ... (requires DBConnection)
        return new DefaultTableModel();
    }
}