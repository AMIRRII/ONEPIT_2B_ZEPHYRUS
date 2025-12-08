/*
Barangay Health Record Management System (Single-file Java Swing application)

WBS Covered:
4.1 Front-End Development
4.1.1 Login interface
4.1.2 Dashboard layout
4.1.3 Patient registration form
4.1.4 Patient search & list interface
4.1.5 Check-up details form
4.1.6 Reports interface

Dependencies:
 - sqlite-jdbc (https://github.com/xerial/sqlite-jdbc) or the appropriate JDBC driver if you want MySQL (adjust JDBC URL accordingly)

How to compile & run (with sqlite-jdbc jar on classpath):
javac -cp .;sqlite-jdbc-<version>.jar BHRMSApp.java
java -cp .;sqlite-jdbc-<version>.jar BHRMSApp

This single-file app creates a local SQLite database file "bhrms.db" in the working directory and provides a Swing GUI with CardLayout.

Notes: This is a simple prototype focusing on the front-end flows and basic persistence. You can expand validation, security, and error handling as needed.
*/

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Vector;

public class BHRMSApp {
    private JFrame frame;
    private CardLayout cards;
    private JPanel cardPanel;
    private Connection conn;
    private String loggedInUser = "admin"; // simple demo user

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                BHRMSApp app = new BHRMSApp();
                app.initDB();
                app.buildUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initDB() throws SQLException {
        try {
            // SQLite JDBC URL
            String url = "jdbc:sqlite:bhrms.db";
            conn = DriverManager.getConnection(url);
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY AUTOINCREMENT, barangay_id TEXT, first_name TEXT, last_name TEXT, gender TEXT, dob TEXT, contact TEXT, address TEXT)");
                st.execute("CREATE TABLE IF NOT EXISTS checkups (id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER, date TEXT, complaint TEXT, diagnosis TEXT, notes TEXT, FOREIGN KEY(patient_id) REFERENCES patients(id))");
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    private void buildUI() {
        frame = new JFrame("Barangay Health Record Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        cards = new CardLayout();
        cardPanel = new JPanel(cards);

        cardPanel.add(new LoginPanel(), "login");
        cardPanel.add(new DashboardPanel(), "dashboard");
        cardPanel.add(new RegistrationPanel(), "register");
        cardPanel.add(new SearchPanel(), "search");
        cardPanel.add(new CheckupPanel(), "checkup");
        cardPanel.add(new ReportsPanel(), "reports");

        frame.add(cardPanel);
        frame.setVisible(true);

        cards.show(cardPanel, "login");
    }

    // -------------------- Panels --------------------
    class LoginPanel extends JPanel {
        public LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            JLabel title = new JLabel("BHRMS Login");
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10,10,20,10);
            add(title, gbc);

            gbc.gridwidth = 1; gbc.insets = new Insets(5,5,5,5);
            add(new JLabel("Username:"), gbc);
            JTextField userField = new JTextField(15);
            gbc.gridx = 1; add(userField, gbc);

            gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Password:"), gbc);
            JPasswordField passField = new JPasswordField(15);
            gbc.gridx = 1; add(passField, gbc);

            JButton loginBtn = new JButton("Login");
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; add(loginBtn, gbc);

            loginBtn.addActionListener(e -> {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                // Demo authentication (replace with proper auth)
                if (u.equals("admin") && p.equals("admin")) {
                    loggedInUser = u;
                    cards.show(cardPanel, "dashboard");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Try admin/admin for demo.");
                }
            });
        }
    }

    class DashboardPanel extends JPanel {
        public DashboardPanel() {
            setLayout(new BorderLayout());
            JPanel top = new JPanel(new BorderLayout());
            JLabel title = new JLabel("Dashboard - Barangay Health Record Management System");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            top.add(title, BorderLayout.WEST);

            JPanel nav = new JPanel();
            JButton btnRegister = new JButton("Register Patient");
            JButton btnSearch = new JButton("Search / List Patients");
            JButton btnCheckup = new JButton("Check-up Details");
            JButton btnReports = new JButton("Reports");
            JButton btnLogout = new JButton("Logout");
            nav.add(btnRegister); nav.add(btnSearch); nav.add(btnCheckup); nav.add(btnReports); nav.add(btnLogout);
            top.add(nav, BorderLayout.SOUTH);
            add(top, BorderLayout.NORTH);

            // Summary area
            JPanel center = new JPanel(new GridLayout(1,3,10,10));
            center.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            center.add(createSummaryCard("Total Patients", String.valueOf(getTotalPatients())));
            center.add(createSummaryCard("Checkups Today", String.valueOf(getCheckupsToday())));
            center.add(createSummaryCard("Last Registered", getLastRegisteredName()));
            add(center, BorderLayout.CENTER);

            btnRegister.addActionListener(e -> cards.show(cardPanel, "register"));
            btnSearch.addActionListener(e -> {
                ((SearchPanel)getPanelByName("search")).refreshTable();
                cards.show(cardPanel, "search");
            });
            btnCheckup.addActionListener(e -> {
                ((CheckupPanel)getPanelByName("checkup")).reloadPatientList();
                cards.show(cardPanel, "checkup");
            });
            btnReports.addActionListener(e -> cards.show(cardPanel, "reports"));
            btnLogout.addActionListener(e -> cards.show(cardPanel, "login"));
        }

        private JPanel createSummaryCard(String label, String value) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(10,10,10,10)));
            JLabel l = new JLabel(label);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JLabel v = new JLabel(value);
            v.setFont(new Font("Segoe UI", Font.BOLD, 20));
            p.add(l, BorderLayout.NORTH);
            p.add(v, BorderLayout.CENTER);
            return p;
        }

        private int getTotalPatients() {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM patients"); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            } catch (SQLException e) { }
            return 0;
        }

        private int getCheckupsToday() {
            String today = LocalDate.now().toString();
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM checkups WHERE date = ?")) {
                ps.setString(1, today);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
            } catch (SQLException e) { }
            return 0;
        }

        private String getLastRegisteredName() {
            try (PreparedStatement ps = conn.prepareStatement("SELECT first_name, last_name FROM patients ORDER BY id DESC LIMIT 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("first_name") + " " + rs.getString("last_name");
                }
            } catch (SQLException e) {}
            return "-";
        }
    }

    class RegistrationPanel extends JPanel {
        JTextField barangayIdFld, firstNameFld, lastNameFld, dobFld, contactFld, addressFld;
        JComboBox<String> genderBox;

        public RegistrationPanel() {
            setLayout(new BorderLayout());
            JLabel title = new JLabel("Patient Registration");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5); gbc.anchor = GridBagConstraints.WEST;

            int y=0;
            gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Barangay ID:"), gbc);
            barangayIdFld = new JTextField(15); gbc.gridx = 1; form.add(barangayIdFld, gbc);

            y++; gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("First name:"), gbc);
            firstNameFld = new JTextField(15); gbc.gridx=1; form.add(firstNameFld, gbc);

            y++; gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Last name:"), gbc);
            lastNameFld = new JTextField(15); gbc.gridx=1; form.add(lastNameFld, gbc);

            y++; gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Gender:"), gbc);
            genderBox = new JComboBox<>(new String[]{"Male","Female","Other"}); gbc.gridx=1; form.add(genderBox, gbc);

            y++; gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("DOB (YYYY-MM-DD):"), gbc);
            dobFld = new JTextField(15); gbc.gridx=1; form.add(dobFld, gbc);

            y++; gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Contact:"), gbc);
            contactFld = new JTextField(15); gbc.gridx=1; form.add(contactFld, gbc);

            y++; gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Address:"), gbc);
            addressFld = new JTextField(30); gbc.gridx=1; form.add(addressFld, gbc);

            add(form, BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            JButton saveBtn = new JButton("Save");
            JButton backBtn = new JButton("Back to Dashboard");
            bottom.add(saveBtn); bottom.add(backBtn);
            add(bottom, BorderLayout.SOUTH);

            saveBtn.addActionListener(e -> savePatient());
            backBtn.addActionListener(e -> {
                cards.show(cardPanel, "dashboard");
            });
        }

        private void savePatient() {
            String barangayId = barangayIdFld.getText().trim();
            String fn = firstNameFld.getText().trim();
            String ln = lastNameFld.getText().trim();
            String gender = (String)genderBox.getSelectedItem();
            String dob = dobFld.getText().trim();
            String contact = contactFld.getText().trim();
            String address = addressFld.getText().trim();

            if (fn.isEmpty() || ln.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter at least first and last name.");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO patients(barangay_id, first_name, last_name, gender, dob, contact, address) VALUES(?,?,?,?,?,?,?)")) {
                ps.setString(1, barangayId);
                ps.setString(2, fn);
                ps.setString(3, ln);
                ps.setString(4, gender);
                ps.setString(5, dob);
                ps.setString(6, contact);
                ps.setString(7, address);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Patient registered successfully.");
                // clear
                barangayIdFld.setText(""); firstNameFld.setText(""); lastNameFld.setText(""); dobFld.setText(""); contactFld.setText(""); addressFld.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving patient: " + ex.getMessage());
            }
        }
    }

    class SearchPanel extends JPanel {
        DefaultTableModel model;
        JTable table;
        JTextField searchFld;

        public SearchPanel() {
            setLayout(new BorderLayout());
            JLabel title = new JLabel("Patient Search & List");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);

            JPanel top = new JPanel();
            top.add(new JLabel("Search (name or barangay id):"));
            searchFld = new JTextField(20);
            JButton searchBtn = new JButton("Search");
            JButton refreshBtn = new JButton("Refresh");
            JButton backBtn = new JButton("Back");
            top.add(searchFld); top.add(searchBtn); top.add(refreshBtn); top.add(backBtn);
            add(top, BorderLayout.SOUTH);

            model = new DefaultTableModel(new String[]{"ID","Barangay ID","First","Last","Gender","DOB","Contact","Address"}, 0) {
                public boolean isCellEditable(int r,int c){return false;}
            };
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel right = new JPanel(new GridLayout(3,1,5,5));
            JButton btnView = new JButton("View Details");
            JButton btnEdit = new JButton("Edit");
            JButton btnDelete = new JButton("Delete");
            right.add(btnView); right.add(btnEdit); right.add(btnDelete);
            add(right, BorderLayout.EAST);

            searchBtn.addActionListener(e -> search());
            refreshBtn.addActionListener(e -> refreshTable());
            backBtn.addActionListener(e -> cards.show(cardPanel, "dashboard"));

            btnView.addActionListener(e -> viewSelected(false));
            btnEdit.addActionListener(e -> viewSelected(true));
            btnDelete.addActionListener(e -> deleteSelected());

            refreshTable();
        }

        public void refreshTable() {
            model.setRowCount(0);
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients ORDER BY last_name, first_name")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("id"));
                        row.add(rs.getString("barangay_id"));
                        row.add(rs.getString("first_name"));
                        row.add(rs.getString("last_name"));
                        row.add(rs.getString("gender"));
                        row.add(rs.getString("dob"));
                        row.add(rs.getString("contact"));
                        row.add(rs.getString("address"));
                        model.addRow(row);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }

        private void search() {
            String q = searchFld.getText().trim();
            model.setRowCount(0);
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR barangay_id LIKE ? ORDER BY last_name, first_name")) {
                String pattern = "%" + q + "%";
                ps.setString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("id"));
                        row.add(rs.getString("barangay_id"));
                        row.add(rs.getString("first_name"));
                        row.add(rs.getString("last_name"));
                        row.add(rs.getString("gender"));
                        row.add(rs.getString("dob"));
                        row.add(rs.getString("contact"));
                        row.add(rs.getString("address"));
                        model.addRow(row);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }

        private void viewSelected(boolean editable) {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select a patient first."); return; }
            int id = (int)model.getValueAt(r,0);
            showPatientDialog(id, editable);
        }

        private void showPatientDialog(int id, boolean editable) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        JTextField barangayId = new JTextField(rs.getString("barangay_id"));
                        JTextField fn = new JTextField(rs.getString("first_name"));
                        JTextField ln = new JTextField(rs.getString("last_name"));
                        JTextField gender = new JTextField(rs.getString("gender"));
                        JTextField dob = new JTextField(rs.getString("dob"));
                        JTextField contact = new JTextField(rs.getString("contact"));
                        JTextField address = new JTextField(rs.getString("address"));

                        barangayId.setEditable(editable); fn.setEditable(editable); ln.setEditable(editable); gender.setEditable(editable); dob.setEditable(editable); contact.setEditable(editable); address.setEditable(editable);

                        JPanel p = new JPanel(new GridLayout(0,2,5,5));
                        p.add(new JLabel("Barangay ID:")); p.add(barangayId);
                        p.add(new JLabel("First:")); p.add(fn);
                        p.add(new JLabel("Last:")); p.add(ln);
                        p.add(new JLabel("Gender:")); p.add(gender);
                        p.add(new JLabel("DOB:")); p.add(dob);
                        p.add(new JLabel("Contact:")); p.add(contact);
                        p.add(new JLabel("Address:")); p.add(address);

                        int option = JOptionPane.showConfirmDialog(this, p, editable?"Edit Patient":"Patient Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        if (option == JOptionPane.OK_OPTION && editable) {
                            try (PreparedStatement up = conn.prepareStatement("UPDATE patients SET barangay_id=?, first_name=?, last_name=?, gender=?, dob=?, contact=?, address=? WHERE id=?")) {
                                up.setString(1, barangayId.getText().trim());
                                up.setString(2, fn.getText().trim());
                                up.setString(3, ln.getText().trim());
                                up.setString(4, gender.getText().trim());
                                up.setString(5, dob.getText().trim());
                                up.setString(6, contact.getText().trim());
                                up.setString(7, address.getText().trim());
                                up.setInt(8, id);
                                up.executeUpdate();
                                JOptionPane.showMessageDialog(this, "Patient updated.");
                                refreshTable();
                            }
                        }
                    }
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }

        private void deleteSelected() {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select a patient first."); return; }
            int id = (int)model.getValueAt(r,0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected patient? This will also remove their checkups.", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM checkups WHERE patient_id = ?")) {
                    ps.setInt(1, id); ps.executeUpdate();
                } catch (SQLException e) {}
                try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM patients WHERE id = ?")) {
                    ps2.setInt(1, id); ps2.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Patient deleted.");
                    refreshTable();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    class CheckupPanel extends JPanel {
        JComboBox<PatientItem> patientCombo;
        JTextField dateFld;
        JTextArea complaintArea, diagnosisArea, notesArea;
        DefaultTableModel historyModel;
        JTable historyTable;

        public CheckupPanel() {
            setLayout(new BorderLayout());
            JLabel title = new JLabel("Check-up Details");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);

            JPanel left = new JPanel(new BorderLayout());
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5,5,5,5); gbc.anchor = GridBagConstraints.WEST;

            gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Patient:"), gbc);
            patientCombo = new JComboBox<>(); gbc.gridx=1; form.add(patientCombo, gbc);

            gbc.gridx=0; gbc.gridy=1; form.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
            dateFld = new JTextField(LocalDate.now().toString(), 12); gbc.gridx=1; form.add(dateFld, gbc);

            gbc.gridx=0; gbc.gridy=2; form.add(new JLabel("Complaint:"), gbc);
            complaintArea = new JTextArea(3,20); gbc.gridx=1; form.add(new JScrollPane(complaintArea), gbc);

            gbc.gridx=0; gbc.gridy=3; form.add(new JLabel("Diagnosis:"), gbc);
            diagnosisArea = new JTextArea(3,20); gbc.gridx=1; form.add(new JScrollPane(diagnosisArea), gbc);

            gbc.gridx=0; gbc.gridy=4; form.add(new JLabel("Notes:"), gbc);
            notesArea = new JTextArea(3,20); gbc.gridx=1; form.add(new JScrollPane(notesArea), gbc);

            left.add(form, BorderLayout.CENTER);
            JButton saveBtn = new JButton("Save Checkup");
            JButton backBtn = new JButton("Back");
            JPanel bottom = new JPanel(); bottom.add(saveBtn); bottom.add(backBtn);
            left.add(bottom, BorderLayout.SOUTH);

            add(left, BorderLayout.WEST);

            JPanel right = new JPanel(new BorderLayout());
            right.add(new JLabel("Checkup History"), BorderLayout.NORTH);
            historyModel = new DefaultTableModel(new String[]{"ID","Date","Complaint","Diagnosis","Notes"},0){public boolean isCellEditable(int r,int c){return false;}};
            historyTable = new JTable(historyModel);
            right.add(new JScrollPane(historyTable), BorderLayout.CENTER);
            add(right, BorderLayout.CENTER);

            saveBtn.addActionListener(e -> saveCheckup());
            backBtn.addActionListener(e -> cards.show(cardPanel, "dashboard"));

            reloadPatientList();
        }

        public void reloadPatientList() {
            patientCombo.removeAllItems();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, first_name, last_name FROM patients ORDER BY last_name, first_name")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        patientCombo.addItem(new PatientItem(rs.getInt("id"), rs.getString("first_name") + " " + rs.getString("last_name")));
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
            if (patientCombo.getItemCount()>0) patientCombo.setSelectedIndex(0);
            reloadHistory();
        }

        private void saveCheckup() {
            PatientItem pi = (PatientItem) patientCombo.getSelectedItem();
            if (pi==null) { JOptionPane.showMessageDialog(this, "No patient selected."); return; }
            String date = dateFld.getText().trim();
            String complaint = complaintArea.getText().trim();
            String diagnosis = diagnosisArea.getText().trim();
            String notes = notesArea.getText().trim();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO checkups(patient_id, date, complaint, diagnosis, notes) VALUES(?,?,?,?,?)")) {
                ps.setInt(1, pi.id);
                ps.setString(2, date);
                ps.setString(3, complaint);
                ps.setString(4, diagnosis);
                ps.setString(5, notes);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Checkup saved.");
                complaintArea.setText(""); diagnosisArea.setText(""); notesArea.setText("");
                reloadHistory();
            } catch (SQLException e) { e.printStackTrace(); }
        }

        private void reloadHistory() {
            historyModel.setRowCount(0);
            PatientItem pi = (PatientItem) patientCombo.getSelectedItem();
            if (pi==null) return;
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM checkups WHERE patient_id = ? ORDER BY date DESC")) {
                ps.setInt(1, pi.id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("id"));
                        row.add(rs.getString("date"));
                        row.add(rs.getString("complaint"));
                        row.add(rs.getString("diagnosis"));
                        row.add(rs.getString("notes"));
                        historyModel.addRow(row);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }

        class PatientItem {
            int id; String name;
            PatientItem(int id, String name){this.id=id;this.name=name;}
            public String toString(){return name + " ("+id+")";}
        }
    }

    class ReportsPanel extends JPanel {
        public ReportsPanel() {
            setLayout(new BorderLayout());
            JLabel title = new JLabel("Reports");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            add(title, BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(3,1,10,10));
            center.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            JButton btnExportPatients = new JButton("Export Patients to CSV");
            JButton btnMonthlySummary = new JButton("Monthly Checkup Summary");
            JButton backBtn = new JButton("Back");
            center.add(btnExportPatients); center.add(btnMonthlySummary); center.add(backBtn);
            add(center, BorderLayout.CENTER);

            btnExportPatients.addActionListener(e -> exportPatients());
            btnMonthlySummary.addActionListener(e -> showMonthlySummary());
            backBtn.addActionListener(e -> cards.show(cardPanel, "dashboard"));
        }

        private void exportPatients() {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients ORDER BY last_name, first_name")) {
                try (ResultSet rs = ps.executeQuery()) {
                    FileWriter fw = new FileWriter("patients_export.csv");
                    fw.write("id,barangay_id,first_name,last_name,gender,dob,contact,address\n");
                    while (rs.next()) {
                        fw.write(rs.getInt("id")+","+escapeCSV(rs.getString("barangay_id"))+","+escapeCSV(rs.getString("first_name"))+","+escapeCSV(rs.getString("last_name"))+","+escapeCSV(rs.getString("gender"))+","+escapeCSV(rs.getString("dob"))+","+escapeCSV(rs.getString("contact"))+","+escapeCSV(rs.getString("address"))+"\n");
                    }
                    fw.close();
                    JOptionPane.showMessageDialog(this, "Exported to patients_export.csv");
                }
            } catch (SQLException | IOException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage()); }
        }

        private String escapeCSV(String s) { if (s==null) return ""; return s.replaceAll(",",";"); }

        private void showMonthlySummary() {
            try (PreparedStatement ps = conn.prepareStatement("SELECT substr(date,1,7) as month, COUNT(*) as total FROM checkups GROUP BY month ORDER BY month DESC")) {
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getString("month")).append(" : ").append(rs.getInt("total")).append(" checkups\n");
                    }
                    if (sb.length()==0) sb.append("No checkups recorded.");
                    JTextArea ta = new JTextArea(sb.toString()); ta.setEditable(false);
                    JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Monthly Summary", JOptionPane.PLAIN_MESSAGE);
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // -------------------- Utilities --------------------
    private Component getPanelByName(String name) {
        for (Component c : cardPanel.getComponents()) {
            if (name.equals(cardPanel.getComponentZOrder(c) >= 0 ? null : null)) { }
        }
        // simple mapping
        switch (name) {
            case "login": return cardPanel.getComponent(0);
            case "dashboard": return cardPanel.getComponent(1);
            case "register": return cardPanel.getComponent(2);
            case "search": return cardPanel.getComponent(3);
            case "checkup": return cardPanel.getComponent(4);
            case "reports": return cardPanel.getComponent(5);
            default: return null;
        }
    }


// ================== DATABASE CONNECTION (MySQL via XAMPP) ==================
// Add this to integrate MySQL into your system
private Connection connect() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/bhrms_db", // Change DB name if needed
            "root", // Default XAMPP user
            ""      // Default password
        );
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "MySQL Connection Failed: " + e.getMessage());
        return null;
    }
}
// ==========================================================================
}
