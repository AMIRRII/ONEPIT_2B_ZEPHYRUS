import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date; 
import java.text.SimpleDateFormat; 

// NOTE: For this code to compile and run, you must ensure you have the following classes
// defined in your project with the required methods:
// 1. LoginGUI
// 2. Patient (with searchPatient, getFullName, getPatient_id)
// 3. Consultation (with getConsultationHistory, addConsultation)

public class MainDashboardGUI extends JFrame {
    
    public MainDashboardGUI(String userRole) {
        setTitle("BHRMS - Dashboard (" + userRole + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // 1. Patient Management Tab
        JPanel patientPanel = createPatientPanel();
        tabbedPane.addTab("Patient Records", patientPanel);
        
        // 2. Check-Up Entry Tab (FIXED: Correctly calls the method)
        JPanel checkupPanel = createCheckupPanel(); 
        tabbedPane.addTab("Record Check-Up", checkupPanel);
        
        // 3. Reports Tab (FIXED: Correctly calls the method)
        JPanel reportPanel = createReportPanel(); 
        tabbedPane.addTab("Reports", reportPanel);
        
        // Logout Button setup (HTA Step 5)
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            this.dispose(); 
            // new LoginGUI(); // Uncomment when LoginGUI class is ready
        });
        
        // Simple layout to hold the tabs and the logout button
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(logoutButton, BorderLayout.SOUTH);

        setVisible(true);
    }


    private JPanel createCheckupPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- 1. North Panel: Patient Selection (HTA Step 3.1) ---
        JPanel patientSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField patientSearchField = new JTextField(20);
        JButton searchPatientButton = new JButton("Find Patient");
        JLabel selectedPatientLabel = new JLabel("Selected Patient: NONE"); 
        
        // This array holds the selected Patient ID (Foreign Key)
        final int[] selectedPatientId = {-1}; 

        patientSelectPanel.add(new JLabel("Patient Name/ID:"));
        patientSelectPanel.add(patientSearchField);
        patientSelectPanel.add(searchPatientButton);
        patientSelectPanel.add(selectedPatientLabel);

        // --- 2. Center-Left: Consultation Details Form (HTA Step 3.2) ---
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField dateField = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        JTextArea symptomsArea = new JTextArea(5, 30);
        JTextArea diagnosisArea = new JTextArea(5, 30);
        JTextArea treatmentArea = new JTextArea(5, 30);

        JScrollPane symptomsScroll = new JScrollPane(symptomsArea);
        JScrollPane diagnosisScroll = new JScrollPane(diagnosisArea);
        JScrollPane treatmentScroll = new JScrollPane(treatmentArea);

        formPanel.add(new JLabel("Check-Up Date:")); formPanel.add(dateField);
        formPanel.add(new JLabel("Symptoms:")); formPanel.add(symptomsScroll);
        formPanel.add(new JLabel("Diagnosis:")); formPanel.add(diagnosisScroll);
        formPanel.add(new JLabel("Treatment:")); formPanel.add(treatmentScroll);
        
        // Wrap the form for better layout control in the split pane
        JPanel formWrapperPanel = new JPanel(new BorderLayout());
        formWrapperPanel.add(formPanel, BorderLayout.NORTH); 

        // --- 3. Center-Right: History Display (NEW) ---
        JTable historyTable = new JTable();
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyTable.setPreferredScrollableViewportSize(new Dimension(400, 250)); 
        
        // Combine Form (Left) and History (Right) using a Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formWrapperPanel, historyScroll);
        splitPane.setResizeWeight(0.5); 
        splitPane.setDividerLocation(450); 
        
        // --- 4. South Panel: Save Button (HTA Step 3.3) ---
        JButton saveCheckupButton = new JButton("Save Check-Up Entry");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveCheckupButton);

        // 5. Final Layout Assembly (COMPONENTS ADDED ONLY ONCE)
        panel.add(patientSelectPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER); 
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // ----------------------------------------------------
        // Event Handling: Search Patient Button Action (CRUD Read)
        // ----------------------------------------------------
        searchPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = patientSearchField.getText().trim();
                if (searchTerm.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please enter a name or ID to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Patient patient = Patient.searchPatient(searchTerm);

                if (patient != null) {
                    selectedPatientId[0] = patient.getPatient_id();
                    selectedPatientLabel.setText("Selected Patient: " + patient.getFullName() + " (ID: " + patient.getPatient_id() + ")");
                    JOptionPane.showMessageDialog(panel, "Patient Found and Selected: " + patient.getFullName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Load and display the patient's consultation history
                    DefaultTableModel historyModel = Consultation.getConsultationHistory(patient.getPatient_id());
                    historyTable.setModel(historyModel); 

                } else {
                    selectedPatientId[0] = -1;
                    selectedPatientLabel.setText("Selected Patient: NONE");
                    JOptionPane.showMessageDialog(panel, "Patient not found in records.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    // Clear the history table
                    historyTable.setModel(new DefaultTableModel()); 
                }
            }
        });
        
        // ----------------------------------------------------
        // Event Handling: Save Button Action (CRUD Create)
        // ----------------------------------------------------
        saveCheckupButton.addActionListener(e -> {
            if (selectedPatientId[0] == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a patient first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int pId = selectedPatientId[0];
                String symptoms = symptomsArea.getText().trim();
                String diagnosis = diagnosisArea.getText().trim();
                String treatment = treatmentArea.getText().trim();
                Date checkupDate = new Date(); 

                if (symptoms.isEmpty() || diagnosis.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Symptoms and Diagnosis are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create Consultation object and call the CRUD Create method
                Consultation newConsultation = new Consultation(pId, checkupDate, symptoms, diagnosis, treatment);
                
                if (newConsultation.addConsultation()) {
                    JOptionPane.showMessageDialog(panel, "Check-Up recorded successfully for Patient ID: " + pId, "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // After saving, refresh history and clear form
                    DefaultTableModel historyModel = Consultation.getConsultationHistory(pId);
                    historyTable.setModel(historyModel); 
                    symptomsArea.setText("");
                    diagnosisArea.setText("");
                    treatmentArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to save check-up. Check database connection/logs.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "An unexpected error occurred: " + ex.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        return panel;
    }
    
    private JPanel createReportPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    
    // 1. --- Summary Statistics Panel (NORTH) ---
    JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 10));
    
    JLabel totalPatientsLabel = new JLabel("Total Registered Patients: N/A", SwingConstants.CENTER);
    JLabel totalConsultationsLabel = new JLabel("Total Consultations Recorded: N/A", SwingConstants.CENTER);
    
    Font summaryFont = new Font("SansSerif", Font.BOLD, 18);
    totalPatientsLabel.setFont(summaryFont);
    totalConsultationsLabel.setFont(summaryFont);
    
    summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary Statistics (All-Time)"));
    summaryPanel.add(totalPatientsLabel);
    summaryPanel.add(totalConsultationsLabel);
    
    // 2. --- Detailed Report Panel (CENTER) ---
    JPanel detailedReportPanel = new JPanel(new BorderLayout(5, 5));
    detailedReportPanel.setBorder(BorderFactory.createTitledBorder("Top 5 Common Diagnoses (Aggregated Data)"));
    
    JTable topDiagnosesTable = new JTable();
    JScrollPane tableScroll = new JScrollPane(topDiagnosesTable);
    
    detailedReportPanel.add(tableScroll, BorderLayout.CENTER);
    
    // 3. --- Refresh/Generate Button (SOUTH) ---
    JButton generateReportButton = new JButton("Generate Reports");
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(generateReportButton);

    // 4. --- Final Panel Assembly ---
    panel.add(summaryPanel, BorderLayout.NORTH);
    panel.add(detailedReportPanel, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);
    
    // 5. --- Event Handling: Report Generation ---
    ActionListener reportGeneratorAction = e -> {
        // A. Generate Summary Data (Calls ReportGenerator.java)
        int totalPatients = ReportGenerator.getTotalPatientCount();
        int totalConsultations = ReportGenerator.getTotalConsultationCount();

        totalPatientsLabel.setText("Total Registered Patients: " + totalPatients);
        totalConsultationsLabel.setText("Total Consultations Recorded: " + totalConsultations);
        
        // B. Generate Tabular Data
        DefaultTableModel topDiagnosesModel = ReportGenerator.getTopDiagnoses(5);
        topDiagnosesTable.setModel(topDiagnosesModel);
        
        if (totalPatients > 0 || totalConsultations > 0) {
            JOptionPane.showMessageDialog(panel, "Reports generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
             JOptionPane.showMessageDialog(panel, "No patient or consultation data found in the database.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    };
    
    generateReportButton.addActionListener(reportGeneratorAction);
    
    // Optional: Auto-generate the reports when the tab is first opened
    generateReportButton.doClick(); 

    return panel;
    }
    // Method to create the Patient Management Panel
    // Method to create the Patient Management Panel
    private JPanel createPatientPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // --- Top Control Panel (Search and Add Buttons) ---
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JTextField searchField = new JTextField(20);
    JButton searchButton = new JButton("Search");
    JButton addButton = new JButton("Register New Patient"); // HTA Step 2.2
    
    controlPanel.add(new JLabel("Search Patient (Name/ID):"));
    controlPanel.add(searchField);
    controlPanel.add(searchButton);
    controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
    controlPanel.add(addButton);
    
    // --- Patient JTable (Display Area) ---
    JTable patientTable = new JTable();
    
    // ** INTEGRATION POINT: Load data from the Patient class **
    // patientTable.setModel(Patient.getAllPatients()); // Use the Patient.java method
    
    // For now, use dummy data until the Patient class is fully written
    String[] columnNames = {"ID", "Name", "Age", "Address"};
    Object[][] data = {
        {1, "Juan Dela Cruz", 35, "Purok 1"},
        {2, "Maria Santos", 22, "Purok 3"}
    };
    patientTable.setModel(new DefaultTableModel(data, columnNames));

    JScrollPane scrollPane = new JScrollPane(patientTable); // JTable must be inside JScrollPane

    // --- Event Handling for Add Button (HTA Step 2.2) ---
    // PASTE YOUR NEW CODE HERE:
    addButton.addActionListener(e -> {
        // Open the dialog for new registration (passing null for patientToEdit)
        new PatientRegistrationDialog(this, null); 
        // After the dialog closes, you would typically refresh the patient table here.
        // For now, assume the Patient class has a static getAllPatients method:
        // patientTable.setModel(Patient.getAllPatients());
    });
    
    panel.add(controlPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    
    return panel;
    }
    
    // (Main method for testing the GUI goes here...)
}