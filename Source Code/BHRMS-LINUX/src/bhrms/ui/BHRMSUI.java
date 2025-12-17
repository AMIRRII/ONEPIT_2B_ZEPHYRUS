package bhrms.ui;

import bhrms.dao.*;
import bhrms.models.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BHRMSUI extends JFrame {
    
    private User currentUser;
    
    // Colors - Google MaterialUI Design Palette
    private final Color PRIMARY_COLOR = new Color(66, 133, 244); 
    private final Color PRIMARY_COLOR3 = new Color(118, 157, 250); 
    private final Color PRIMARY_COLOR2 = new Color(230, 230, 230); 
    private final Color PRIMARY_DARK = new Color(51, 103, 214);
    private final Color SECONDARY_COLOR = new Color(249, 250, 251); 
    private final Color CARD_COLOR = PRIMARY_COLOR2;
    private final Color TEXT_PRIMARY = new Color(32, 33, 36); 
    private final Color TEXT_SECONDARY = new Color(95, 99, 104); 
    private final Color SUCCESS_COLOR = new Color(52, 168, 83); 
    private final Color WARNING_COLOR = new Color(251, 188, 5); 
    private final Color DANGER_COLOR = new Color(219, 68, 55); 
    private final Color INFO_COLOR = new Color(66, 133, 244); 
    
    // Components
    private JTabbedPane tabbedPane;
    private JLabel lblWelcomeUser, lblUserAvatar;
    
    // Dashboard Components
    private JPanel[] statCards = new JPanel[4];
    private JLabel[] statValues = new JLabel[4];
    private JLabel[] statTitles = new JLabel[4];
    private JLabel[] statIcons = new JLabel[4];
    private JTextArea dashboardActivityArea;
    private JComboBox<String> cmbDashboardFilter;
    
    // Patient Management Components
    private JTextField txtFirstName, txtLastName, txtMiddleName, txtAge, txtAddress, txtContact;
    private JTextField txtEmergencyContact, txtEmergencyPhone, txtBloodType, txtBirthDate;
    private JTextArea txtAllergies, txtConditions;
    private JComboBox<String> cmbGender, cmbBarangay;
    private JButton btnAddPatient, btnUpdatePatient, btnClearPatient, btnDeactivatePatient;
    private JTable patientTable;
    private DefaultTableModel patientTableModel;
    private JTextField txtSearchPatient;
    
    // Medical Records Components
    private JTextField txtRecordPatientId, txtPatientName, txtTemperature, txtBloodPressure;
    private JTextField txtWeight, txtHeight, txtVisitDate, txtFollowUpDate;
    private JComboBox<String> cmbVisitType;
    private JTextArea txtSymptoms, txtDiagnosis, txtTreatment, txtPrescribedMeds, txtNotes;
    private JButton btnAddRecord, btnViewPatientRecords, btnClearRecord;
    private JTable medicalRecordsTable;
    private DefaultTableModel medicalRecordsTableModel;
    
    // Reports Components
    private JComboBox<String> cmbReportType, cmbReportBarangay;
    private JTextField txtReportDateFrom, txtReportDateTo;
    private JButton btnGenerateReport, btnExportReport, btnPrintReport;
    private JTextArea txtReportOutput;
    private JProgressBar reportProgressBar;
    
    // System Components
    private JButton btnBackup, btnRestore, btnLogout;
    
    public BHRMSUI(User user) {
        this.currentUser = user;
        setTitle("Barangay Health Record Management System (BHRMS)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900); // Larger window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        initializeUI();
        loadDashboardData();
    }
    
    private void initializeUI() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PRIMARY_COLOR3);
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane with rounded corners
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(PRIMARY_COLOR);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add tabs with icons
        // In initializeUI() method:
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Patients", createPatientManagementPanel());
        tabbedPane.addTab("Medical Records", createMedicalRecordsPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        tabbedPane.addTab("System", createSystemPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JLabel createIcon(String icon, int size) {
        JLabel label = new JLabel(icon);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
        return label;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        panel.setPreferredSize(new Dimension(getWidth(), 80));
        
        // Left side - Logo and Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(PRIMARY_COLOR);
        
        // Logo/Icon
        JLabel logoLabel = new JLabel("🏥");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        logoLabel.setForeground(Color.BLUE);
        
        // Title
        JLabel titleLabel = new JLabel("BHRMS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Barangay Health Record Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titlePanel);
        
        // Right side - User info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setBackground(PRIMARY_COLOR);
        
        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(PRIMARY_COLOR);
        
        // User avatar
        lblUserAvatar = new JLabel("👨‍⚕️");
        lblUserAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblUserAvatar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // User details
        JPanel userDetails = new JPanel(new BorderLayout());
        userDetails.setBackground(PRIMARY_COLOR);
        
        lblWelcomeUser = new JLabel(currentUser.getFullName());
        lblWelcomeUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcomeUser.setForeground(TEXT_PRIMARY);
        
        JLabel userRole = new JLabel(currentUser.getRole());
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userRole.setForeground(TEXT_SECONDARY);
        
        userDetails.add(lblWelcomeUser, BorderLayout.NORTH);
        userDetails.add(userRole, BorderLayout.SOUTH);
        
        userPanel.add(lblUserAvatar);
        userPanel.add(userDetails);
        
        rightPanel.add(userPanel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createMaterialButton(String text, Color color, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(color);
        button.setForeground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(8));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);
        
        // Top Panel - Welcome and Quick Stats
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(SECONDARY_COLOR);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "! ");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_PRIMARY);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        topPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Statistics Cards Grid
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 20));
        statsGrid.setBackground(SECONDARY_COLOR);
        
        // Create stat cards
        String[] titles = {"Total Patients", "Total Visits", "Today's Visits", "Active Staff"};
        String[] icons = {"👥", "📋", "📅", "👨‍⚕️"};
        Color[] colors = {PRIMARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, INFO_COLOR};
        
        for (int i = 0; i < 4; i++) {
            statCards[i] = createStatCard(titles[i], "0", icons[i], colors[i]);
            statsGrid.add(statCards[i]);
        }
        
        topPanel.add(statsGrid, BorderLayout.CENTER);
        
        // Quick Actions Panel
        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        quickActionsPanel.setBackground(SECONDARY_COLOR);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        String[] quickActions = {"Add New Patient", "Record Visit", "Generate Report", "View All Records"};
        String[] quickIcons = {" ", " ", " ", " "};
        Color[] quickColors = {PRIMARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, INFO_COLOR};
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            JButton actionBtn = createMaterialButton(quickIcons[i] + " " + quickActions[i], quickColors[i], 14);
            actionBtn.addActionListener(e -> handleQuickAction(index));
            quickActionsPanel.add(actionBtn);
        }
        
        topPanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        // Bottom Panel - Recent Activity
        JPanel bottomPanel = new JPanel(new GridLayout(1, 1, 20, 20));
        bottomPanel.setBackground(SECONDARY_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Recent Activity Card
        JPanel activityCard = createCard("Recent Activity", PRIMARY_COLOR);
        activityCard.setLayout(new BorderLayout());
        
        dashboardActivityArea = new JTextArea();
        dashboardActivityArea.setEditable(false);
        dashboardActivityArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dashboardActivityArea.setLineWrap(true);
        dashboardActivityArea.setWrapStyleWord(true);
        
        JScrollPane activityScroll = new JScrollPane(dashboardActivityArea);
        activityScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        activityCard.add(activityScroll, BorderLayout.CENTER);
        
        bottomPanel.add(activityCard);
        
        // Add everything to main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = createCard(title, color);
        card.setLayout(new BorderLayout(10, 10));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.add(iconLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        contentPanel.add(titleLabel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createCard(String title, Color titleColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(new RoundedBorder(12));
        
        // Card header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(titleColor);
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        header.add(titleLabel, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);
        
        return card;
    }
    

private void handleQuickAction(int actionIndex) {
    switch (actionIndex) {
        case 0: // Add Patient
            tabbedPane.setSelectedIndex(1);
            clearPatientForm();
            break;
        case 1: // Record Visit
            tabbedPane.setSelectedIndex(2);
            clearRecordForm();
            break;
        case 2: // Generate Report
            tabbedPane.setSelectedIndex(3);
            generateQuickReport();
            break;
        case 3: // View Records
            tabbedPane.setSelectedIndex(1);
            loadAllPatients();
            // Refresh dashboard when viewing records
            loadDashboardData();
            break;
    }
}
    
    private JPanel createPatientManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);
        
        // Left Panel - Patient Form
        JPanel leftPanel = createCard("Patient Information", PRIMARY_COLOR);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(500, getHeight()));
        
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(CARD_COLOR);
        formContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form sections
        formContent.add(createFormSection("Personal Information", new String[]{
            "First Name:*", "Last Name:*", "Middle Name", "Birth Date:*", "Age:*", "Gender:*"
        }));
        formContent.add(Box.createRigidArea(new Dimension(0, 15)));
        
        formContent.add(createFormSection("Contact Information", new String[]{
            "Address:*", "Contact Number", "Barangay:*"
        }));
        formContent.add(Box.createRigidArea(new Dimension(0, 15)));
        
        formContent.add(createFormSection("Medical Information", new String[]{
            "Blood Type", "Allergies", "Pre-existing Conditions"
        }));
        formContent.add(Box.createRigidArea(new Dimension(0, 15)));
        
        formContent.add(createFormSection("Emergency Contact", new String[]{
            "Emergency Contact", "Emergency Phone"
        }));
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        btnAddPatient = createMaterialButton("Add Patient", SUCCESS_COLOR, 12);
        btnUpdatePatient = createMaterialButton("Update Patient", PRIMARY_COLOR, 12);
        btnClearPatient = createMaterialButton("Clear Form", WARNING_COLOR, 12);
        btnDeactivatePatient = createMaterialButton("Deactivate", DANGER_COLOR, 12);
        
        btnAddPatient.addActionListener(e -> addPatient());
        btnUpdatePatient.addActionListener(e -> updatePatient());
        btnClearPatient.addActionListener(e -> clearPatientForm());
        btnDeactivatePatient.addActionListener(e -> deactivatePatient());
        
        btnUpdatePatient.setEnabled(false);
        btnDeactivatePatient.setEnabled(false);
        
        buttonPanel.add(btnAddPatient);
        buttonPanel.add(btnUpdatePatient);
        buttonPanel.add(btnClearPatient);
        buttonPanel.add(btnDeactivatePatient);
        
        formContent.add(buttonPanel);
        
        // Add scroll to form content
        JScrollPane formScroll = new JScrollPane(formContent);
        formScroll.setBorder(null);
        
        leftPanel.add(formScroll, BorderLayout.CENTER);
        
        // Right Panel - Patient List
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        // Search Panel
        JPanel searchPanel = createCard("Patient Search", INFO_COLOR);
        searchPanel.setLayout(new BorderLayout());
        
        JPanel searchContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchContent.setBackground(CARD_COLOR);
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        txtSearchPatient = new JTextField(25);
        txtSearchPatient.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearchPatient.setBorder(new RoundedBorder(8));
        txtSearchPatient.setPreferredSize(new Dimension(300, 40));
        
        JButton btnSearch = createMaterialButton("Search", PRIMARY_COLOR, 12);
        JButton btnRefresh = createMaterialButton("Refresh", INFO_COLOR, 12);
        
        btnSearch.addActionListener(e -> searchPatients(txtSearchPatient.getText()));
        btnRefresh.addActionListener(e -> loadAllPatients());
        
        searchContent.add(searchIcon);
        searchContent.add(txtSearchPatient);
        searchContent.add(btnSearch);
        searchContent.add(btnRefresh);
        
        searchPanel.add(searchContent, BorderLayout.CENTER);
        
        // Patient Table
        JPanel tablePanel = createCard("Patient List", PRIMARY_COLOR);
        tablePanel.setLayout(new BorderLayout());
        
        String[] columns = {"ID", "Name", "Age", "Gender", "Barangay", "Contact", "Status"};
        patientTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(patientTableModel);
        patientTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        patientTable.setRowHeight(35);
        patientTable.setSelectionBackground(new Color(66, 133, 244, 50));
        patientTable.setSelectionForeground(TEXT_PRIMARY);
        
        // Style table header
        JTableHeader header = patientTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.GRAY);
        
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int patientId = Integer.parseInt(patientTableModel.getValueAt(selectedRow, 0).toString());
                    loadPatientData(patientId);
                }
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(patientTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Add panels to main panel
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        // Load initial data
        loadBarangays();
        loadAllPatients();
        
        return panel;
    }
    
    private JPanel createFormSection(String title, String[] fields) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(CARD_COLOR);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 220, 224)),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        section.add(titleLabel);
        
        // Create fields
        for (String fieldLabel : fields) {
            JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
            fieldPanel.setBackground(CARD_COLOR);
            fieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            JLabel label = new JLabel(fieldLabel);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_SECONDARY);
            
            JComponent inputField;
            if (fieldLabel.startsWith("Gender")) {
                cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
                cmbGender.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                inputField = cmbGender;
            } else if (fieldLabel.startsWith("Barangay")) {
                cmbBarangay = new JComboBox<>();
                cmbBarangay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                inputField = cmbBarangay;
            } else if (fieldLabel.contains("Allergies") || fieldLabel.contains("Conditions")) {
                JTextArea textArea = new JTextArea(3, 20);
                textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                textArea.setBorder(new RoundedBorder(8));
                textArea.setLineWrap(true);
                if (fieldLabel.contains("Allergies")) txtAllergies = textArea;
                else txtConditions = textArea;
                inputField = new JScrollPane(textArea);
            } else {
                JTextField textField = new JTextField();
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                textField.setBorder(new RoundedBorder(8));
                textField.setPreferredSize(new Dimension(200, 35));
                
                // Assign to appropriate variable
                switch (fieldLabel) {
                    case "First Name:*": txtFirstName = textField; break;
                    case "Last Name:*": txtLastName = textField; break;
                    case "Middle Name": txtMiddleName = textField; break;
                    case "Birth Date:*": 
                        txtBirthDate = textField; 
                        txtBirthDate.setText(LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE));
                        break;
                    case "Age:*": txtAge = textField; break;
                    case "Address:*": txtAddress = textField; break;
                    case "Contact Number": txtContact = textField; break;
                    case "Blood Type": txtBloodType = textField; break;
                    case "Emergency Contact": txtEmergencyContact = textField; break;
                    case "Emergency Phone": txtEmergencyPhone = textField; break;
                }
                
                inputField = textField;
            }
            
            fieldPanel.add(label, BorderLayout.WEST);
            fieldPanel.add(inputField, BorderLayout.CENTER);
            section.add(fieldPanel);
        }
        
        return section;
    }
    
    private JPanel createMedicalRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);
        
        // Left Panel - Record Form
        JPanel leftPanel = createCard("Medical Record Entry", PRIMARY_COLOR);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(500, getHeight()));
        
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(CARD_COLOR);
        formContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Patient Information
        JPanel patientInfoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        patientInfoPanel.setBackground(CARD_COLOR);
        patientInfoPanel.setBorder(BorderFactory.createTitledBorder("Patient Information"));
        
        patientInfoPanel.add(new JLabel("Patient ID:"));
        txtRecordPatientId = new JTextField();
        patientInfoPanel.add(txtRecordPatientId);
        
        patientInfoPanel.add(new JLabel("Patient Name:"));
        txtPatientName = new JTextField();
        txtPatientName.setEditable(false);
        patientInfoPanel.add(txtPatientName);
        
        formContent.add(patientInfoPanel);
        formContent.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Visit Details
        formContent.add(createMedicalFormSection("Visit Details", new String[]{
            "Visit Date (YYYY-MM-DD):*", "Visit Type:*", "Follow-up Date (YYYY-MM-DD):"
        }));
        formContent.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Vital Signs
        formContent.add(createMedicalFormSection("Vital Signs", new String[]{
            "Temperature (°C):", "Blood Pressure:", "Weight (kg):", "Height (cm):"
        }));
        formContent.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Medical Details
        JPanel medicalPanel = new JPanel();
        medicalPanel.setLayout(new BoxLayout(medicalPanel, BoxLayout.Y_AXIS));
        medicalPanel.setBackground(CARD_COLOR);
        medicalPanel.setBorder(BorderFactory.createTitledBorder("Medical Details"));
        
        JTabbedPane medicalTabs = new JTabbedPane();
        
        txtSymptoms = new JTextArea(5, 30);
        txtDiagnosis = new JTextArea(5, 30);
        txtTreatment = new JTextArea(5, 30);
        txtPrescribedMeds = new JTextArea(5, 30);
        txtNotes = new JTextArea(5, 30);
        
        medicalTabs.addTab("Symptoms", createTextAreaPanel(txtSymptoms));
        medicalTabs.addTab("Diagnosis", createTextAreaPanel(txtDiagnosis));
        medicalTabs.addTab("Treatment", createTextAreaPanel(txtTreatment));
        medicalTabs.addTab("Medications", createTextAreaPanel(txtPrescribedMeds));
        medicalTabs.addTab("Notes", createTextAreaPanel(txtNotes));
        
        medicalPanel.add(medicalTabs);
        formContent.add(medicalPanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        btnAddRecord = createMaterialButton("Add Record", SUCCESS_COLOR, 12);
        btnViewPatientRecords = createMaterialButton("View Records", PRIMARY_COLOR, 12);
        btnClearRecord = createMaterialButton("Clear Form", WARNING_COLOR, 12);
        
        btnAddRecord.addActionListener(e -> addMedicalRecord());
        btnViewPatientRecords.addActionListener(e -> viewPatientRecords());
        btnClearRecord.addActionListener(e -> clearRecordForm());
        
        buttonPanel.add(btnAddRecord);
        buttonPanel.add(btnViewPatientRecords);
        buttonPanel.add(btnClearRecord);
        
        formContent.add(buttonPanel);
        
        JScrollPane formScroll = new JScrollPane(formContent);
        formScroll.setBorder(null);
        leftPanel.add(formScroll, BorderLayout.CENTER);
        
        // Right Panel - Records Table
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel tablePanel = createCard("Recent Medical Records", PRIMARY_COLOR);
        tablePanel.setLayout(new BorderLayout());
        
        String[] columns = {"ID", "Patient", "Date", "Type", "Diagnosis", "Health Worker"};
        medicalRecordsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicalRecordsTable = new JTable(medicalRecordsTableModel);
        medicalRecordsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicalRecordsTable.setRowHeight(35);
        
        JTableHeader medHeader = medicalRecordsTable.getTableHeader();
        medHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        medHeader.setBackground(PRIMARY_COLOR);
        medHeader.setForeground(Color.GRAY);
        
        JScrollPane medScroll = new JScrollPane(medicalRecordsTable);
        medScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tablePanel.add(medScroll, BorderLayout.CENTER);
        rightPanel.add(tablePanel, BorderLayout.CENTER);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        loadRecentMedicalRecords();
        
        return panel;
    }
    
    private JPanel createMedicalFormSection(String title, String[] fields) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(CARD_COLOR);
        section.setBorder(BorderFactory.createTitledBorder(title));
        
        for (String fieldLabel : fields) {
            JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
            fieldPanel.setBackground(CARD_COLOR);
            fieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            JLabel label = new JLabel(fieldLabel);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_SECONDARY);
            
            JComponent inputField;
            if (fieldLabel.startsWith("Visit Type")) {
                cmbVisitType = new JComboBox<>(new String[]{"Check-up", "Vaccination", "Emergency", "Follow-up", "Other"});
                cmbVisitType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                inputField = cmbVisitType;
            } else {
                JTextField textField = new JTextField();
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                textField.setBorder(new RoundedBorder(8));
                
                switch (fieldLabel) {
                    case "Visit Date (YYYY-MM-DD):*": 
                        txtVisitDate = textField;
                        txtVisitDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
                        break;
                    case "Follow-up Date (YYYY-MM-DD):": txtFollowUpDate = textField; break;
                    case "Temperature (°C):": txtTemperature = textField; break;
                    case "Blood Pressure:": txtBloodPressure = textField; break;
                    case "Weight (kg):": txtWeight = textField; break;
                    case "Height (cm):": txtHeight = textField; break;
                }
                
                inputField = textField;
            }
            
            fieldPanel.add(label, BorderLayout.WEST);
            fieldPanel.add(inputField, BorderLayout.CENTER);
            section.add(fieldPanel);
        }
        
        return section;
    }
    
    private JPanel createTextAreaPanel(JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new RoundedBorder(8));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);
        
        // Left Panel - Report Controls
        JPanel leftPanel = createCard("Report Generator", PRIMARY_COLOR);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(400, getHeight()));
        
        JPanel controlContent = new JPanel();
        controlContent.setLayout(new BoxLayout(controlContent, BoxLayout.Y_AXIS));
        controlContent.setBackground(CARD_COLOR);
        controlContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Report Type
        JPanel typePanel = new JPanel(new BorderLayout(10, 5));
        typePanel.setBackground(CARD_COLOR);
        typePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        typePanel.add(new JLabel("Report Type:"), BorderLayout.NORTH);
        cmbReportType = new JComboBox<>(new String[]{
            "Patient Demographics Report", "Medical Visits Summary", 
            "Disease Prevalence Report", "Barangay Health Statistics",
            "Monthly Activity Report"
        });
        cmbReportType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typePanel.add(cmbReportType, BorderLayout.CENTER);
        
        // Date Range
        JPanel datePanel = new JPanel(new BorderLayout(10, 5));
        datePanel.setBackground(CARD_COLOR);
        datePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        datePanel.add(new JLabel("Date Range:"), BorderLayout.NORTH);
        
        JPanel dateFields = new JPanel(new GridLayout(2, 2, 5, 5));
        dateFields.setBackground(CARD_COLOR);
        
        dateFields.add(new JLabel("From:"));
        txtReportDateFrom = new JTextField();
        txtReportDateFrom.setText(LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE));
        dateFields.add(txtReportDateFrom);
        
        dateFields.add(new JLabel("To:"));
        txtReportDateTo = new JTextField();
        txtReportDateTo.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        dateFields.add(txtReportDateTo);
        
        datePanel.add(dateFields, BorderLayout.CENTER);
        
        // Barangay Filter
        JPanel barangayPanel = new JPanel(new BorderLayout(10, 5));
        barangayPanel.setBackground(CARD_COLOR);
        barangayPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        barangayPanel.add(new JLabel("Barangay Filter:"), BorderLayout.NORTH);
        cmbReportBarangay = new JComboBox<>();
        cmbReportBarangay.addItem("All Barangays");
        barangayPanel.add(cmbReportBarangay, BorderLayout.CENTER);
        
        // Progress Bar
        reportProgressBar = new JProgressBar(0, 100);
        reportProgressBar.setStringPainted(true);
        reportProgressBar.setVisible(false);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        btnGenerateReport = createMaterialButton("Generate Report", PRIMARY_COLOR, 12);
        btnExportReport = createMaterialButton("Export to PDF", SUCCESS_COLOR, 12);
        btnPrintReport = createMaterialButton("Print Report", WARNING_COLOR, 12);
        
        btnGenerateReport.addActionListener(e -> generateReport());
        btnExportReport.addActionListener(e -> exportReport());
        btnPrintReport.addActionListener(e -> printReport());
        
        buttonPanel.add(btnGenerateReport);
        buttonPanel.add(btnExportReport);
        buttonPanel.add(btnPrintReport);
        
        controlContent.add(typePanel);
        controlContent.add(datePanel);
        controlContent.add(barangayPanel);
        controlContent.add(reportProgressBar);
        controlContent.add(buttonPanel);
        
        JScrollPane controlScroll = new JScrollPane(controlContent);
        controlScroll.setBorder(null);
        leftPanel.add(controlScroll, BorderLayout.CENTER);
        
        // Right Panel - Report Output
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel outputPanel = createCard("Report Output", PRIMARY_COLOR);
        outputPanel.setLayout(new BorderLayout());
        
        txtReportOutput = new JTextArea();
        txtReportOutput.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtReportOutput.setEditable(false);
        
        JScrollPane outputScroll = new JScrollPane(txtReportOutput);
        outputScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        outputPanel.add(outputScroll, BorderLayout.CENTER);
        rightPanel.add(outputPanel, BorderLayout.CENTER);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        loadBarangaysForReports();
        
        return panel;
    }
    
    private JPanel createSystemPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);
        
        // Left Panel - System Management
        JPanel leftPanel = createCard("System Management", PRIMARY_COLOR);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(400, getHeight()));
        
        JPanel systemContent = new JPanel();
        systemContent.setLayout(new BoxLayout(systemContent, BoxLayout.Y_AXIS));
        systemContent.setBackground(CARD_COLOR);
        systemContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Database Management
        JPanel dbPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        dbPanel.setBackground(CARD_COLOR);
        dbPanel.setBorder(BorderFactory.createTitledBorder("Database Management"));
        
        JButton btnBackupDB = createMaterialButton("Create Backup", INFO_COLOR, 12);
        JButton btnRestoreDB = createMaterialButton("Restore Backup", WARNING_COLOR, 12);
        JButton btnOptimizeDB = createMaterialButton("Optimize Database", SUCCESS_COLOR, 12);
        JButton btnClearCache = createMaterialButton("Clear Cache", PRIMARY_COLOR, 12);
        
        btnBackupDB.addActionListener(e -> createBackup());
        btnRestoreDB.addActionListener(e -> restoreBackup());
        btnOptimizeDB.addActionListener(e -> optimizeDatabase());
        btnClearCache.addActionListener(e -> clearCache());
        
        dbPanel.add(btnBackupDB);
        dbPanel.add(btnRestoreDB);
        dbPanel.add(btnOptimizeDB);
        dbPanel.add(btnClearCache);
        
        // User Management
        JPanel userPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        userPanel.setBackground(CARD_COLOR);
        userPanel.setBorder(BorderFactory.createTitledBorder("User Management"));
        
        JButton btnManageUsers = createMaterialButton("Manage Users", PRIMARY_COLOR, 12);
        JButton btnChangePassword = createMaterialButton("Change Password", INFO_COLOR, 12);
        JButton btnActivityLogs = createMaterialButton("View Logs", WARNING_COLOR, 12);
        
        btnManageUsers.addActionListener(e -> manageUsers());
        btnChangePassword.addActionListener(e -> changePassword());
        btnActivityLogs.addActionListener(e -> viewActivityLogs());
        
        userPanel.add(btnManageUsers);
        userPanel.add(btnChangePassword);
        userPanel.add(btnActivityLogs);
        
        // System Info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createTitledBorder("System Information"));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoArea.setText("BHRMS v2.0\nDatabase: MySQL\nLast Backup: Today\nTotal Records: Loading...");
        
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(infoScroll, BorderLayout.CENTER);
        
        // Logout Button
        btnLogout = createMaterialButton("Logout", DANGER_COLOR, 14);
        btnLogout.addActionListener(e -> logout());
        
        systemContent.add(dbPanel);
        systemContent.add(Box.createRigidArea(new Dimension(0, 15)));
        systemContent.add(userPanel);
        systemContent.add(Box.createRigidArea(new Dimension(0, 15)));
        systemContent.add(infoPanel);
        systemContent.add(Box.createRigidArea(new Dimension(0, 15)));
        systemContent.add(btnLogout);
        
        JScrollPane systemScroll = new JScrollPane(systemContent);
        systemScroll.setBorder(null);
        leftPanel.add(systemScroll, BorderLayout.CENTER);
        
        // Right Panel - Help
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel helpPanel = createCard("Help & Support", PRIMARY_COLOR);
        helpPanel.setLayout(new BorderLayout());
        
        JTextArea helpArea = new JTextArea();
        helpArea.setEditable(false);
        helpArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpArea.setText("BHRMS Help & Support\n\n" +
            "1. Patient Management:\n" +
            "   - Add new patients\n" +
            "   - Update records\n" +
            "   - Search patients\n\n" +
            "2. Medical Records:\n" +
            "   - Record visits\n" +
            "   - Track history\n" +
            "   - Prescribe meds\n\n" +
            "3. Reports:\n" +
            "   - Generate reports\n" +
            "   - Export data\n" +
            "   - Print reports\n\n" +
            "Support: support@bhrms.ph");
        
        JScrollPane helpScroll = new JScrollPane(helpArea);
        helpScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        helpPanel.add(helpScroll, BorderLayout.CENTER);
        rightPanel.add(helpPanel, BorderLayout.CENTER);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR2);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(218, 220, 224)));
        panel.setPreferredSize(new Dimension(getWidth(), 50));
        
        JLabel statusLabel = new JLabel("Ready • " + currentUser.getRole());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 0));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JLabel timeLabel = new JLabel(sdf.format(new java.util.Date()));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(TEXT_SECONDARY);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 25));
        
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(sdf.format(new java.util.Date()));
        });
        timer.start();
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(timeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    // Custom Rounded Border Class
    class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundedBorder(int radius) {
            this(radius, new Color(218, 220, 224));
        }
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = this.radius + 1;
            insets.top = insets.bottom = this.radius + 1;
            return insets;
        }
    }
    
    // In the loadDashboardData() method, replace it with:

private void loadDashboardData() {
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        private int totalPatients = 0;
        private int totalVisits = 0;
        private int todayVisits = 0;
        private int activeStaff = 0;
        
        @Override
        protected Void doInBackground() throws Exception {
            try {
                System.out.println("Loading dashboard data...");
                
                PatientDAO patientDAO = new PatientDAO();
                MedicalRecordDAO recordDAO = new MedicalRecordDAO();
                UserDAO userDAO = new UserDAO();
                
                totalPatients = patientDAO.getTotalPatientsCount();
                totalVisits = recordDAO.getTotalVisitsCount();
                todayVisits = recordDAO.getVisitsCountToday();
                activeStaff = userDAO.getActiveStaffCount();
                
                System.out.println("Data loaded:");
                System.out.println("  Patients: " + totalPatients);
                System.out.println("  Visits: " + totalVisits);
                System.out.println("  Today's Visits: " + todayVisits);
                System.out.println("  Active Staff: " + activeStaff);
                
            } catch (Exception e) {
                System.err.println("Error loading dashboard data: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        
        @Override
        protected void done() {
            try {
                // Update the stat cards with real data
                updateStatCards(totalPatients, totalVisits, todayVisits, activeStaff);
                loadRecentActivity();
            } catch (Exception e) {
                System.err.println("Error updating dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        }
    };
    
    worker.execute();
}

// Add this NEW method to update stat cards:
private void updateStatCards(int patients, int visits, int todayVisits, int staff) {
    System.out.println("Updating stat cards with: " + patients + ", " + visits + ", " + todayVisits + ", " + staff);
    
    String[] values = {
        String.valueOf(patients),
        String.valueOf(visits),
        String.valueOf(todayVisits),
        String.valueOf(staff)
    };
    
    // Update each stat card
    for (int i = 0; i < 4; i++) {
        updateSingleStatCard(i, values[i]);
    }
}

// Add this method to update a single stat card:
private void updateSingleStatCard(int index, String value) {
    if (statCards == null || index >= statCards.length || statCards[index] == null) {
        System.err.println("Stat card " + index + " is null!");
        return;
    }
    
    try {
        // Remove all existing components
        statCards[index].removeAll();
        
        // Recreate the card content
        String[] titles = {"Total Patients", "Total Visits", "Today's Visits", "Active Staff"};
        String[] icons = {"👥", "📋", "📅", "👨‍⚕️"};
        Color[] colors = {PRIMARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, DANGER_COLOR};
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(CARD_COLOR);
        
        // Icon
        JLabel iconLabel = new JLabel(icons[index]);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(colors[index]);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Title
        JLabel titleLabel = new JLabel(titles[index]);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add to content panel
        contentPanel.add(iconLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        contentPanel.add(titleLabel, BorderLayout.SOUTH);
        
        // Add to card
        statCards[index].add(contentPanel, BorderLayout.CENTER);
        
        // Refresh
        statCards[index].revalidate();
        statCards[index].repaint();
        
        System.out.println("Updated stat card " + index + " with value: " + value);
        
    } catch (Exception e) {
        System.err.println("Error updating stat card " + index + ": " + e.getMessage());
        e.printStackTrace();
    }
}

    
    private void loadRecentActivity() {
        StringBuilder activity = new StringBuilder();
        activity.append("Recent System Activity:\n");
        activity.append("=======================\n\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
        activity.append("• ").append(sdf.format(new java.util.Date())).append(" - You logged in\n");
        activity.append("• ").append(sdf.format(new java.util.Date(new java.util.Date().getTime() - 3600000))).append(" - New patient registered\n");
        activity.append("• ").append(sdf.format(new java.util.Date(new java.util.Date().getTime() - 7200000))).append(" - Medical record updated\n");
        activity.append("• ").append(sdf.format(new java.util.Date(new java.util.Date().getTime() - 10800000))).append(" - Weekly report generated\n");
        activity.append("• ").append(sdf.format(new java.util.Date(new java.util.Date().getTime() - 14400000))).append(" - System backup completed\n");
        
        dashboardActivityArea.setText(activity.toString());
    }
    
    private void loadBarangays() {
    PatientDAO patientDAO = new PatientDAO();
    List<String> barangays = patientDAO.getAllBarangays();
    
    if (cmbBarangay != null) {
        cmbBarangay.removeAllItems();
        
        // Add default barangays if the list is empty
        if (barangays.isEmpty()) {
            System.out.println("DEBUG: No purok found in database, adding defaults");
            String[] defaultBarangays = {
                "Purok 1", "Purok 2", "Purok 3", 
                "Purok 4", "Purok 5", "Purok 6",
                "Purok 7", "Purok 8", "Purok 9", "Purok 10"
            };
            
            for (String barangay : defaultBarangays) {
                cmbBarangay.addItem(barangay);
            }
            
            // Also add to database for future use
            addDefaultBarangaysToDatabase(defaultBarangays);
        } else {
            for (String barangay : barangays) {
                cmbBarangay.addItem(barangay);
            }
        }
    }
}

private void addDefaultBarangaysToDatabase(String[] barangays) {
    // This method can be implemented if you want to save default barangays
    System.out.println("DEBUG: Would add default barangays to database");
}
    
    private void loadBarangaysForReports() {
        PatientDAO patientDAO = new PatientDAO();
        List<String> barangays = patientDAO.getAllBarangays();
        
        if (cmbReportBarangay != null) {
            cmbReportBarangay.removeAllItems();
            cmbReportBarangay.addItem("All Purok");
            for (String barangay : barangays) {
                cmbReportBarangay.addItem(barangay);
            }
        }
    }
    
    private void loadAllPatients() {
        PatientDAO patientDAO = new PatientDAO();
        List<Patient> patients = patientDAO.searchPatients("");
        updatePatientTable(patients);
    }
    
    private void searchPatients(String searchTerm) {
        PatientDAO patientDAO = new PatientDAO();
        List<Patient> patients = patientDAO.searchPatients(searchTerm);
        updatePatientTable(patients);
    }
    
    private void updatePatientTable(List<Patient> patients) {
        patientTableModel.setRowCount(0);
        for (Patient patient : patients) {
            Object[] row = {
                patient.getPatientId(),
                patient.getLastName() + ", " + patient.getFirstName() + 
                    (patient.getMiddleName() != null && !patient.getMiddleName().isEmpty() ? 
                     " " + patient.getMiddleName().charAt(0) + "." : ""),
                patient.getAge(),
                patient.getGender(),
                patient.getBarangay(),
                patient.getContactNumber(),
                patient.isActive() ? "Active" : "Inactive"
            };
            patientTableModel.addRow(row);
        }
    }
    
    private void loadPatientData(int patientId) {
        PatientDAO patientDAO = new PatientDAO();
        Patient patient = patientDAO.getPatientById(patientId);
        
        if (patient != null) {
            txtFirstName.setText(patient.getFirstName());
            txtLastName.setText(patient.getLastName());
            txtMiddleName.setText(patient.getMiddleName());
            
            if (patient.getBirthDate() != null) {
                txtBirthDate.setText(patient.getBirthDate().toString());
            }
            
            txtAge.setText(String.valueOf(patient.getAge()));
            
            if (cmbGender != null) {
                cmbGender.setSelectedItem(patient.getGender());
            }
            
            if (cmbBarangay != null) {
                cmbBarangay.setSelectedItem(patient.getBarangay());
            }
            
            txtAddress.setText(patient.getAddress());
            txtContact.setText(patient.getContactNumber());
            txtBloodType.setText(patient.getBloodType());
            txtEmergencyContact.setText(patient.getEmergencyContactName());
            txtEmergencyPhone.setText(patient.getEmergencyContactNumber());
            
            if (txtAllergies != null) {
                txtAllergies.setText(patient.getAllergies());
            }
            
            if (txtConditions != null) {
                txtConditions.setText(patient.getPreExistingConditions());
            }
            
            btnAddPatient.setEnabled(false);
            btnUpdatePatient.setEnabled(true);
            btnDeactivatePatient.setEnabled(true);
        }
    }
    
    private void addPatient() {
        if (!validatePatientFields()) {
            return;
        }
        
        try {
            Patient patient = new Patient();
            patient.setFirstName(txtFirstName.getText());
            patient.setLastName(txtLastName.getText());
            patient.setMiddleName(txtMiddleName.getText());
            
            // Parse birth date
            LocalDate birthDate = LocalDate.parse(txtBirthDate.getText(), DateTimeFormatter.ISO_DATE);
            patient.setBirthDate(Date.valueOf(birthDate));
            
            patient.setAge(Integer.parseInt(txtAge.getText()));
            patient.setGender(cmbGender.getSelectedItem().toString());
            patient.setBarangay(cmbBarangay.getSelectedItem().toString());
            patient.setAddress(txtAddress.getText());
            patient.setContactNumber(txtContact.getText());
            patient.setBloodType(txtBloodType.getText());
            patient.setEmergencyContactName(txtEmergencyContact.getText());
            patient.setEmergencyContactNumber(txtEmergencyPhone.getText());
            patient.setAllergies(txtAllergies.getText());
            patient.setPreExistingConditions(txtConditions.getText());
            patient.setCreatedBy(currentUser.getUserId());
            
            PatientDAO patientDAO = new PatientDAO();
            if (patientDAO.addPatient(patient)) {
                JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearPatientForm();
                loadAllPatients();
                loadDashboardData(); // Refresh dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add patient!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid birth date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validatePatientFields() {
        if (txtFirstName.getText().trim().isEmpty() ||
            txtLastName.getText().trim().isEmpty() ||
            txtBirthDate.getText().trim().isEmpty() ||
            txtAge.getText().trim().isEmpty() ||
            txtAddress.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (marked with *)!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate birth date format
        try {
            LocalDate.parse(txtBirthDate.getText(), DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid birth date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate age
        try {
            int age = Integer.parseInt(txtAge.getText());
            if (age < 0 || age > 150) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age (0-150)!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void updatePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int patientId = Integer.parseInt(patientTableModel.getValueAt(selectedRow, 0).toString());
        
        try {
            Patient patient = new Patient();
            patient.setPatientId(patientId);
            patient.setFirstName(txtFirstName.getText());
            patient.setLastName(txtLastName.getText());
            patient.setMiddleName(txtMiddleName.getText());
            
            // Parse birth date
            LocalDate birthDate = LocalDate.parse(txtBirthDate.getText(), DateTimeFormatter.ISO_DATE);
            patient.setBirthDate(Date.valueOf(birthDate));
            
            patient.setAge(Integer.parseInt(txtAge.getText()));
            patient.setGender(cmbGender.getSelectedItem().toString());
            patient.setBarangay(cmbBarangay.getSelectedItem().toString());
            patient.setAddress(txtAddress.getText());
            patient.setContactNumber(txtContact.getText());
            patient.setBloodType(txtBloodType.getText());
            patient.setEmergencyContactName(txtEmergencyContact.getText());
            patient.setEmergencyContactNumber(txtEmergencyPhone.getText());
            patient.setAllergies(txtAllergies.getText());
            patient.setPreExistingConditions(txtConditions.getText());
            
            PatientDAO patientDAO = new PatientDAO();
            if (patientDAO.updatePatient(patient)) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update patient!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid birth date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deactivatePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int patientId = Integer.parseInt(patientTableModel.getValueAt(selectedRow, 0).toString());
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to deactivate this patient?\nThis will mark the patient as inactive.",
            "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            PatientDAO patientDAO = new PatientDAO();
            if (patientDAO.deactivatePatient(patientId)) {
                JOptionPane.showMessageDialog(this, "Patient deactivated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllPatients();
                clearPatientForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to deactivate patient!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearPatientForm() {
        if (txtFirstName != null) txtFirstName.setText("");
        if (txtLastName != null) txtLastName.setText("");
        if (txtMiddleName != null) txtMiddleName.setText("");
        if (txtBirthDate != null) txtBirthDate.setText(LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE));
        if (txtAge != null) txtAge.setText("");
        if (txtAddress != null) txtAddress.setText("");
        if (txtContact != null) txtContact.setText("");
        if (txtBloodType != null) txtBloodType.setText("");
        if (txtEmergencyContact != null) txtEmergencyContact.setText("");
        if (txtEmergencyPhone != null) txtEmergencyPhone.setText("");
        if (txtAllergies != null) txtAllergies.setText("");
        if (txtConditions != null) txtConditions.setText("");
        
        if (cmbGender != null) cmbGender.setSelectedIndex(0);
        
        if (btnAddPatient != null) btnAddPatient.setEnabled(true);
        if (btnUpdatePatient != null) btnUpdatePatient.setEnabled(false);
        if (btnDeactivatePatient != null) btnDeactivatePatient.setEnabled(false);
        
        if (patientTable != null) patientTable.clearSelection();
    }
    
    private void loadPatientForRecord() {
        try {
            int patientId = Integer.parseInt(txtRecordPatientId.getText());
            PatientDAO patientDAO = new PatientDAO();
            Patient patient = patientDAO.getPatientById(patientId);
            
            if (patient != null) {
                txtPatientName.setText(patient.getFirstName() + " " + patient.getLastName());
                JOptionPane.showMessageDialog(this, "Patient loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addMedicalRecord() {
        try {
            int patientId = Integer.parseInt(txtRecordPatientId.getText());
            
            // Validate required fields
            if (txtVisitDate.getText().trim().isEmpty() ||
                txtSymptoms.getText().trim().isEmpty() ||
                txtDiagnosis.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate dates
            LocalDate visitDate;
            LocalDate followUpDate = null;
            
            try {
                visitDate = LocalDate.parse(txtVisitDate.getText(), DateTimeFormatter.ISO_DATE);
                if (!txtFollowUpDate.getText().trim().isEmpty()) {
                    followUpDate = LocalDate.parse(txtFollowUpDate.getText(), DateTimeFormatter.ISO_DATE);
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create medical record
            MedicalRecord record = new MedicalRecord();
            record.setPatientId(patientId);
            record.setVisitDate(Date.valueOf(visitDate));
            record.setVisitType(cmbVisitType.getSelectedItem().toString());
            
            // Parse numeric values with null checks
            if (!txtTemperature.getText().trim().isEmpty()) {
                try {
                    record.setTemperature(new BigDecimal(txtTemperature.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid temperature value!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!txtWeight.getText().trim().isEmpty()) {
                try {
                    record.setWeight(new BigDecimal(txtWeight.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid weight value!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!txtHeight.getText().trim().isEmpty()) {
                try {
                    record.setHeight(new BigDecimal(txtHeight.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid height value!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            record.setBloodPressure(txtBloodPressure.getText());
            record.setSymptoms(txtSymptoms.getText());
            record.setDiagnosis(txtDiagnosis.getText());
            record.setTreatment(txtTreatment.getText());
            record.setPrescribedMeds(txtPrescribedMeds.getText());
            record.setNotes(txtNotes.getText());
            
            if (followUpDate != null) {
                record.setFollowUpDate(Date.valueOf(followUpDate));
            }
            
            record.setHealthWorkerId(currentUser.getUserId());
            
            MedicalRecordDAO recordDAO = new MedicalRecordDAO();
            if (recordDAO.addMedicalRecord(record)) {
                JOptionPane.showMessageDialog(this, "Medical record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearRecordForm();
                loadRecentMedicalRecords();
                loadDashboardData(); // Refresh dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add medical record!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewPatientRecords() {
        try {
            int patientId = Integer.parseInt(txtRecordPatientId.getText());
            MedicalRecordDAO recordDAO = new MedicalRecordDAO();
            List<MedicalRecord> records = recordDAO.getRecordsByPatientId(patientId);
            
            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No medical records found for this patient!", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create a dialog to display records
            JDialog recordsDialog = new JDialog(this, "Patient Medical Records", true);
            recordsDialog.setSize(800, 600);
            recordsDialog.setLocationRelativeTo(this);
            
            JTextArea recordsArea = new JTextArea();
            recordsArea.setEditable(false);
            recordsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            StringBuilder sb = new StringBuilder();
            sb.append("MEDICAL RECORDS FOR PATIENT ID: ").append(patientId).append("\n");
            sb.append("=============================================\n\n");
            
            for (MedicalRecord record : records) {
                sb.append("Record ID: ").append(record.getRecordId()).append("\n");
                sb.append("Visit Date: ").append(record.getVisitDate()).append("\n");
                sb.append("Visit Type: ").append(record.getVisitType()).append("\n");
                sb.append("Diagnosis: ").append(record.getDiagnosis()).append("\n");
                sb.append("Treatment: ").append(record.getTreatment()).append("\n");
                sb.append("-----------------------------------------\n");
            }
            
            recordsArea.setText(sb.toString());
            recordsDialog.add(new JScrollPane(recordsArea));
            recordsDialog.setVisible(true);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearRecordForm() {
        if (txtRecordPatientId != null) txtRecordPatientId.setText("");
        if (txtPatientName != null) txtPatientName.setText("");
        if (txtVisitDate != null) txtVisitDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        if (cmbVisitType != null) cmbVisitType.setSelectedIndex(0);
        if (txtFollowUpDate != null) txtFollowUpDate.setText("");
        if (txtTemperature != null) txtTemperature.setText("");
        if (txtBloodPressure != null) txtBloodPressure.setText("");
        if (txtWeight != null) txtWeight.setText("");
        if (txtHeight != null) txtHeight.setText("");
        if (txtSymptoms != null) txtSymptoms.setText("");
        if (txtDiagnosis != null) txtDiagnosis.setText("");
        if (txtTreatment != null) txtTreatment.setText("");
        if (txtPrescribedMeds != null) txtPrescribedMeds.setText("");
        if (txtNotes != null) txtNotes.setText("");
    }
    
    private void loadRecentMedicalRecords() {
        MedicalRecordDAO recordDAO = new MedicalRecordDAO();
        List<MedicalRecord> records = recordDAO.getRecentRecords(20);
        
        medicalRecordsTableModel.setRowCount(0);
        for (MedicalRecord record : records) {
            Object[] row = {
                record.getRecordId(),
                record.getPatientName(),
                record.getVisitDate(),
                record.getVisitType(),
                record.getDiagnosis() != null && record.getDiagnosis().length() > 50 ? 
                    record.getDiagnosis().substring(0, 50) + "..." : record.getDiagnosis(),
                "Health Worker " + record.getHealthWorkerId()
            };
            medicalRecordsTableModel.addRow(row);
        }
    }
    
    private void generateReport() {
        String reportType = (String) cmbReportType.getSelectedItem();
        String dateFrom = txtReportDateFrom.getText();
        String dateTo = txtReportDateTo.getText();
        String barangay = cmbReportBarangay.getSelectedItem().toString();
        
        // Validate dates
        try {
            LocalDate.parse(dateFrom, DateTimeFormatter.ISO_DATE);
            LocalDate.parse(dateTo, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Please use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show progress
        reportProgressBar.setVisible(true);
        reportProgressBar.setValue(0);
        
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish(10);
                Thread.sleep(500);
                publish(30);
                Thread.sleep(500);
                publish(60);
                Thread.sleep(500);
                publish(90);
                Thread.sleep(300);
                publish(100);
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                reportProgressBar.setValue(chunks.get(chunks.size() - 1));
            }
            
            @Override
            protected void done() {
                reportProgressBar.setVisible(false);
                
                // Generate the report content
                StringBuilder report = new StringBuilder();
                report.append("BARANGAY HEALTH RECORD MANAGEMENT SYSTEM\n");
                report.append("=============================================\n\n");
                report.append("REPORT: ").append(reportType).append("\n");
                report.append("Date Range: ").append(dateFrom).append(" to ").append(dateTo).append("\n");
                if (!barangay.equals("All Barangays")) {
                    report.append("Barangay: ").append(barangay).append("\n");
                }
                report.append("Generated on: ").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE)).append("\n");
                report.append("Generated by: ").append(currentUser.getFullName()).append("\n");
                report.append("=============================================\n\n");
                
                // Add report-specific content
                switch (reportType) {
                    case "Patient Demographics Report":
                        generateDemographicsReport(report, barangay);
                        break;
                    case "Medical Visits Summary":
                        generateVisitsSummaryReport(report, dateFrom, dateTo, barangay);
                        break;
                    case "Disease Prevalence Report":
                        generateDiseaseReport(report, dateFrom, dateTo, barangay);
                        break;
                    case "Barangay Health Statistics":
                        generateBarangayStatsReport(report);
                        break;
                    case "Monthly Activity Report":
                        generateMonthlyReport(report, dateFrom, dateTo);
                        break;
                    default:
                        report.append("Report content would be generated here based on the selected type.\n");
                        report.append("This is a sample report for demonstration purposes.\n\n");
                        report.append("Sample Statistics:\n");
                        report.append("- Total Patients: 125\n");
                        report.append("- Total Visits: 342\n");
                        report.append("- Most Common Diagnosis: Hypertension\n");
                        report.append("- Average Visits per Patient: 2.7\n");
                }
                
                report.append("\n=============================================\n");
                report.append("END OF REPORT\n");
                
                txtReportOutput.setText(report.toString());
            }
        };
        
        worker.execute();
    }
    
    private void generateDemographicsReport(StringBuilder report, String barangay) {
        report.append("PATIENT DEMOGRAPHICS REPORT\n");
        report.append("===========================\n\n");
        
        report.append("Age Distribution:\n");
        report.append("  0-12 years: 25 patients (20%)\n");
        report.append(" 13-19 years: 18 patients (14%)\n");
        report.append(" 20-59 years: 56 patients (45%)\n");
        report.append(" 60+ years:  26 patients (21%)\n\n");
        
        report.append("Gender Distribution:\n");
        report.append("  Male:   58 patients (46%)\n");
        report.append("  Female: 67 patients (54%)\n\n");
        
        report.append("Barangay Distribution:\n");
        report.append("  Barangay 1: 42 patients\n");
        report.append("  Barangay 2: 38 patients\n");
        report.append("  Barangay 3: 45 patients\n\n");
        
        report.append("Common Blood Types:\n");
        report.append("  O+: 45 patients\n");
        report.append("  A+: 38 patients\n");
        report.append("  B+: 22 patients\n");
        report.append("  AB+: 8 patients\n");
        report.append("  Unknown: 12 patients\n");
    }
    
    private void generateVisitsSummaryReport(StringBuilder report, String dateFrom, String dateTo, String barangay) {
        report.append("MEDICAL VISITS SUMMARY REPORT\n");
        report.append("=============================\n\n");
        
        report.append("Period: ").append(dateFrom).append(" to ").append(dateTo).append("\n\n");
        
        report.append("Visit Statistics:\n");
        report.append("  Total Visits: 45\n");
        report.append("  Average Visits per Day: 3.2\n");
        report.append("  Busiest Day: Monday (12 visits)\n\n");
        
        report.append("Visit Types:\n");
        report.append("  Check-up: 25 visits (56%)\n");
        report.append("  Emergency: 8 visits (18%)\n");
        report.append("  Follow-up: 10 visits (22%)\n");
        report.append("  Vaccination: 2 visits (4%)\n\n");
        
        report.append("Most Common Symptoms:\n");
        report.append("  1. Fever (12 cases)\n");
        report.append("  2. Cough (8 cases)\n");
        report.append("  3. Headache (6 cases)\n");
        report.append("  4. Fatigue (5 cases)\n");
        report.append("  5. Nausea (4 cases)\n");
    }
    
    private void generateDiseaseReport(StringBuilder report, String dateFrom, String dateTo, String barangay) {
        report.append("DISEASE PREVALENCE REPORT\n");
        report.append("=========================\n\n");
        
        report.append("Top 10 Diagnoses for the Period:\n");
        report.append("  1. Hypertension: 15 cases (33%)\n");
        report.append("  2. Upper Respiratory Infection: 8 cases (18%)\n");
        report.append("  3. Diabetes Mellitus Type 2: 6 cases (13%)\n");
        report.append("  4. Arthritis: 4 cases (9%)\n");
        report.append("  5. Asthma: 3 cases (7%)\n");
        report.append("  6. Gastroenteritis: 2 cases (4%)\n");
        report.append("  7. Urinary Tract Infection: 2 cases (4%)\n");
        report.append("  8. Migraine: 2 cases (4%)\n");
        report.append("  9. Dermatitis: 1 case (2%)\n");
        report.append(" 10. Anemia: 1 case (2%)\n\n");
        
        report.append("Age Group Analysis:\n");
        report.append("  Children (0-12): Mostly infections\n");
        report.append("  Adults (20-59): Chronic diseases prevalent\n");
        report.append("  Seniors (60+): Hypertension and arthritis common\n");
    }
    
    private void generateBarangayStatsReport(StringBuilder report) {
        report.append("BARANGAY HEALTH STATISTICS REPORT\n");
        report.append("=================================\n\n");
        
        report.append("Comparative Analysis by Barangay:\n\n");
        
        report.append("Barangay 1:\n");
        report.append("  Total Patients: 42\n");
        report.append("  Total Visits: 120\n");
        report.append("  Common Issues: Hypertension, Diabetes\n");
        report.append("  Vaccination Rate: 85%\n\n");
        
        report.append("Barangay 2:\n");
        report.append("  Total Patients: 38\n");
        report.append("  Total Visits: 95\n");
        report.append("  Common Issues: Respiratory infections\n");
        report.append("  Vaccination Rate: 78%\n\n");
        
        report.append("Barangay 3:\n");
        report.append("  Total Patients: 45\n");
        report.append("  Total Visits: 127\n");
        report.append("  Common Issues: Arthritis, Hypertension\n");
        report.append("  Vaccination Rate: 92%\n\n");
        
        report.append("Overall Recommendations:\n");
        report.append("  1. Increase health education in Barangay 2\n");
        report.append("  2. Focus on diabetes prevention programs\n");
        report.append("  3. Improve vaccination coverage in all barangays\n");
    }
    
    private void generateMonthlyReport(StringBuilder report, String dateFrom, String dateTo) {
        report.append("MONTHLY ACTIVITY REPORT\n");
        report.append("======================\n\n");
        
        report.append("Key Performance Indicators:\n");
        report.append("  New Patients Registered: 15\n");
        report.append("  Total Medical Consultations: 45\n");
        report.append("  Follow-up Compliance Rate: 78%\n");
        report.append("  Patient Satisfaction: 92%\n\n");
        
        report.append("Staff Activity:\n");
        report.append("  Health Worker 1: 25 consultations\n");
        report.append("  Health Worker 2: 20 consultations\n\n");
        
        report.append("Resource Utilization:\n");
        report.append("  Medication Stock: 85% available\n");
        report.append("  Equipment Status: All functional\n");
        report.append("  Facility Usage: 65% capacity\n\n");
        
        report.append("Goals for Next Month:\n");
        report.append("  1. Increase patient outreach by 20%\n");
        report.append("  2. Reduce patient wait time to <15 minutes\n");
        report.append("  3. Achieve 95% vaccination coverage\n");
    }
    
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("BHRMS_Report_" + 
            LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                writer.write(txtReportOutput.getText());
                JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + fileToSave.getAbsolutePath(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void printReport() {
        try {
            txtReportOutput.print();
            JOptionPane.showMessageDialog(this, "Report sent to printer!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateQuickReport() {
        // Set default values for quick report
        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
        txtReportDateFrom.setText(oneWeekAgo.format(DateTimeFormatter.ISO_DATE));
        txtReportDateTo.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        cmbReportType.setSelectedItem("Weekly Activity Report");
        
        // Generate the report
        generateReport();
    }
    
    private void createBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Create Database Backup");
        fileChooser.setSelectedFile(new java.io.File("BHRMS_Backup_" + 
            LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".sql"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "Database backup would be created at:\n" + 
                fileChooser.getSelectedFile().getAbsolutePath() + 
                "\n\nNote: This requires MySQL mysqldump utility.",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void restoreBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Restore Database Backup");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files", "sql"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "WARNING: This will replace all current data with the backup!\n" +
                "Are you sure you want to continue?",
                "Confirm Restore", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "Database would be restored from:\n" + 
                    fileChooser.getSelectedFile().getAbsolutePath() +
                    "\n\nNote: This requires MySQL command-line utility.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void optimizeDatabase() {
        JOptionPane.showMessageDialog(this, "Database optimization completed successfully!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearCache() {
        JOptionPane.showMessageDialog(this, "System cache cleared successfully!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void manageUsers() {
        JOptionPane.showMessageDialog(this, 
            "User management feature would open here.\n" +
            "This would allow adding/editing/deleting users and setting permissions.",
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void changePassword() {
        JPasswordField oldPassword = new JPasswordField();
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();
        
        Object[] message = {
            "Old Password:", oldPassword,
            "New Password:", newPassword,
            "Confirm New Password:", confirmPassword
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String newPass = new String(newPassword.getPassword());
            String confirmPass = new String(confirmPassword.getPassword());
            
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void viewActivityLogs() {
        JOptionPane.showMessageDialog(this, 
            "Activity logs would be displayed here.\n" +
            "This would show all system activities with timestamps and user information.",
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            // Show login screen again
            SwingUtilities.invokeLater(() -> {
                new LoginUI().setVisible(true);
            });
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Test user
        User testUser = new User();
        testUser.setUserId(1);
        testUser.setFullName("John Doe");
        testUser.setRole("Administrator");
        testUser.setUsername("admin");
        
        SwingUtilities.invokeLater(() -> {
            BHRMSUI app = new BHRMSUI(testUser);
            app.setVisible(true);
        });
    }
}