import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class ReportGenerator {

    /**
     * Helper method to execute a simple COUNT query on any table.
     * @param tableName The table to count records from (e.g., "patients").
     * @return The total number of records.
     */
    private static int getCount(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Database Count Error for table " + tableName + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Report 1: Gets the total count of registered patients.
     * @return Total patient count.
     */
    public static int getTotalPatientCount() {
        return getCount("patients");
    }

    /**
     * Report 2: Gets the total count of all recorded consultations.
     * @return Total consultation count.
     */
    public static int getTotalConsultationCount() {
        return getCount("consultations");
    }

    /**
     * Report 3: Executes an aggregate query to find the top N most common diagnoses.
     * This is crucial for monitoring community health trends.
     * @param limit The number of top diagnoses to return (e.g., 5).
     * @return DefaultTableModel containing Diagnosis and Count.
     */
    public static DefaultTableModel getTopDiagnoses(int limit) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Diagnosis");
        model.addColumn("Count");

        // SQL using GROUP BY and ORDER BY to aggregate data
        String sql = "SELECT diagnosis, COUNT(diagnosis) as diagnosis_count " +
                     "FROM consultations " +
                     "WHERE diagnosis IS NOT NULL AND diagnosis != '' " + // Filter out empty entries
                     "GROUP BY diagnosis " +
                     "ORDER BY diagnosis_count DESC " +
                     "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("diagnosis"));
                row.add(rs.getInt("diagnosis_count"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Top Diagnoses Report Error: " + e.getMessage());
        }
        return model;
    }
}