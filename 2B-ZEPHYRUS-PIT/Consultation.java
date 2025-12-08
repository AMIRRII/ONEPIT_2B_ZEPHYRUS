import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class Consultation {
    // Private Attributes (Matching the database table columns)
    private int consultation_id;
    private int patient_id; // Foreign Key to the patients table
    private Date checkupDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    
    // Date formatter for consistent SQL interaction (YYYY-MM-DD)
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // --- Constructor ---
    /**
     * Initializes a new Consultation object (typically for recording a new entry).
     */
    public Consultation(int patient_id, Date checkupDate, String symptoms, String diagnosis, String treatment) {
        this.patient_id = patient_id;
        this.checkupDate = checkupDate;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.consultation_id = -1; // Indicates a new record
    }

    // --- CRUD Operations ---

    /**
     * CREATE: Inserts a new consultation record into the database.
     * (Called by MainDashboardGUI in createCheckupPanel)
     * @return true if the record was successfully added, false otherwise.
     */
    public boolean addConsultation() {
        // SQL statement to insert the data
        String sql = "INSERT INTO consultations (patient_id, checkupDate, symptoms, diagnosis, treatment) VALUES (?, ?, ?, ?, ?)";
        String dateString = SQL_DATE_FORMAT.format(this.checkupDate);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patient_id);
            pstmt.setString(2, dateString);
            pstmt.setString(3, symptoms);
            pstmt.setString(4, diagnosis);
            pstmt.setString(5, treatment);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database Error during consultation entry: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * READ (History): Retrieves a patient's entire consultation history for display.
     * (Called by MainDashboardGUI in createCheckupPanel)
     * @param pId The ID of the patient whose history is needed.
     * @return A DefaultTableModel ready to be displayed in a JTable.
     */
    public static DefaultTableModel getConsultationHistory(int pId) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Date");
        model.addColumn("Symptoms");
        model.addColumn("Diagnosis");
        model.addColumn("Treatment");

        // Order by date descending (newest entry first)
        String sql = "SELECT checkupDate, symptoms, diagnosis, treatment FROM consultations WHERE patient_id = ? ORDER BY checkupDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                // Format the SQL Date object to a readable string
                row.add(SQL_DATE_FORMAT.format(rs.getDate("checkupDate"))); 
                row.add(rs.getString("symptoms"));
                row.add(rs.getString("diagnosis"));
                row.add(rs.getString("treatment"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving consultation history: " + e.getMessage());
        }
        return model;
    }
}