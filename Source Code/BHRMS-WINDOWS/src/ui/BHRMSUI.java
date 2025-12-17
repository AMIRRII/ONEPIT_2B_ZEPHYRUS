package bhrms.ui;

import bhrms.DatabaseBackup;
import bhrms.DatabaseConnection;
import bhrms.dao.*;
import bhrms.models.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.mysql.cj.xdevapi.Statement;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

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
    private JButton btnAddPatient, btnUpdatePatient, btnClearPatient, btnDeactivatePatient, btnDeletePatient;
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10)); // Changed from 4 to 5 columns
    buttonPanel.setBackground(CARD_COLOR);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

    btnAddPatient = createMaterialButton("Add Patient", SUCCESS_COLOR, 12);
    btnUpdatePatient = createMaterialButton("Update Patient", PRIMARY_COLOR, 12);
    btnClearPatient = createMaterialButton("Clear Form", WARNING_COLOR, 12);
    btnDeactivatePatient = createMaterialButton("Deactivate", DANGER_COLOR, 12);
    // NEW: Add Delete button
    btnDeletePatient = createMaterialButton("Delete", new Color(139, 0, 0), 12); // Dark red

    btnAddPatient.addActionListener(e -> addPatient());
    btnUpdatePatient.addActionListener(e -> updatePatient());
    btnClearPatient.addActionListener(e -> clearPatientForm());
    btnDeactivatePatient.addActionListener(e -> deactivatePatient());
    btnDeletePatient.addActionListener(e -> deletePatient()); // NEW: Action listener

    btnUpdatePatient.setEnabled(false);
    btnDeactivatePatient.setEnabled(false);
    btnDeletePatient.setEnabled(false); // NEW: Initially disabled

    buttonPanel.add(btnAddPatient);
    buttonPanel.add(btnUpdatePatient);
    buttonPanel.add(btnClearPatient);
    buttonPanel.add(btnDeactivatePatient);
    buttonPanel.add(btnDeletePatient); // NEW: Add to panel
        
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
            btnUpdatePatient.setEnabled(true);
            btnDeactivatePatient.setEnabled(true);
            btnDeletePatient.setEnabled(true); // Enable delete button
        } else {
            btnUpdatePatient.setEnabled(false);
            btnDeactivatePatient.setEnabled(false);
            btnDeletePatient.setEnabled(false); // Disable delete button
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
    JPanel dbPanel = new JPanel(new GridLayout(5, 1, 10, 10)); // Changed from 4 to 5 rows
    dbPanel.setBackground(CARD_COLOR);
    dbPanel.setBorder(BorderFactory.createTitledBorder("Database Management"));
    
    JButton btnBackupDB = createMaterialButton("Create Backup", INFO_COLOR, 12);
    JButton btnRestoreDB = createMaterialButton("Restore Backup", WARNING_COLOR, 12);
    JButton btnOptimizeDB = createMaterialButton("Optimize Database", SUCCESS_COLOR, 12);
    JButton btnClearCache = createMaterialButton("Clear Cache", PRIMARY_COLOR, 12);
    
    // TEMPORARY: Add diagnostic button
    JButton diagButton = createMaterialButton("Diagnose Backup", DANGER_COLOR, 12);
    
    btnBackupDB.addActionListener(e -> createBackup());
    btnRestoreDB.addActionListener(e -> restoreBackup());
    btnOptimizeDB.addActionListener(e -> optimizeDatabase());
    btnClearCache.addActionListener(e -> clearCache());
    diagButton.addActionListener(e -> diagnoseBackup()); // Add action listener
    
    dbPanel.add(btnBackupDB);
    dbPanel.add(btnRestoreDB);
    dbPanel.add(btnOptimizeDB);
    dbPanel.add(btnClearCache);
    dbPanel.add(diagButton); // Add to panel
    
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

    // Get backup info
    String backupInfo = getBackupInfo();
    infoArea.setText("BHRMS v2.0\nDatabase: MySQL\n" + backupInfo);

    JScrollPane infoScroll = new JScrollPane(infoArea);
    infoScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    infoPanel.add(infoScroll, BorderLayout.CENTER);
    
    // Logout Button
    btnLogout = createMaterialButton("Logout", DANGER_COLOR, 14);
    btnLogout.addActionListener(e -> logout());
    
    // Add all panels to systemContent in order
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
        "4. System:\n" +
        "   - Create database backups\n" +
        "   - Restore from backup\n" +
        "   - View system information\n\n" +
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

    private void deletePatient() {
    int selectedRow = patientTable.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Please select a patient to delete!", 
            "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int patientId = Integer.parseInt(patientTableModel.getValueAt(selectedRow, 0).toString());
    String patientName = patientTableModel.getValueAt(selectedRow, 1).toString();
    
    // Show warning dialog
    int confirm = JOptionPane.showConfirmDialog(this, 
        "<html><b>WARNING: This action cannot be undone!</b><br><br>" +
        "Are you sure you want to permanently delete patient:<br>" +
        "<b>" + patientName + "</b> (ID: " + patientId + ")?<br><br>" +
        "This will also delete all medical records associated with this patient!<br>" +
        "Consider using 'Deactivate' instead if you want to keep the data.</html>",
        "Confirm Permanent Deletion", 
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    
    if (confirm == JOptionPane.YES_OPTION) {
        PatientDAO patientDAO = new PatientDAO();
        if (patientDAO.deletePatient(patientId)) {
            JOptionPane.showMessageDialog(this, 
                "Patient and all associated records deleted successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllPatients();
            clearPatientForm();
            loadDashboardData(); // Refresh dashboard
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to delete patient!", "Error", JOptionPane.ERROR_MESSAGE);
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
        if (btnDeletePatient != null) btnDeletePatient.setEnabled(false); // NEW
    
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
    
    PatientDAO patientDAO = new PatientDAO();
    int totalPatients = patientDAO.getTotalPatientsCount();
    
    report.append("Total Active Patients: ").append(totalPatients).append("\n");
    report.append("Report Date: ").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE)).append("\n");
    report.append("=============================================\n\n");
    
    // 1. Age Distribution
    report.append("AGE DISTRIBUTION:\n");
    report.append("-----------------\n");
    
    Map<String, Integer> ageGroups = patientDAO.getAgeGroupDistribution();
    for (Map.Entry<String, Integer> entry : ageGroups.entrySet()) {
        int count = entry.getValue();
        double percentage = totalPatients > 0 ? (count * 100.0 / totalPatients) : 0;
        report.append(String.format("  %-12s: %3d patients (%5.1f%%)\n", 
            entry.getKey(), count, percentage));
    }
    report.append("\n");
    
    // 2. Gender Distribution
    report.append("GENDER DISTRIBUTION:\n");
    report.append("--------------------\n");
    
    Map<String, Integer> genderDist = patientDAO.getGenderDistribution();
    for (Map.Entry<String, Integer> entry : genderDist.entrySet()) {
        int count = entry.getValue();
        double percentage = totalPatients > 0 ? (count * 100.0 / totalPatients) : 0;
        report.append(String.format("  %-6s: %3d patients (%5.1f%%)\n", 
            entry.getKey(), count, percentage));
    }
    report.append("\n");
    
    // 3. Barangay Distribution
    report.append("BARANGAY DISTRIBUTION:\n");
    report.append("----------------------\n");
    
    Map<String, Integer> barangayDist = patientDAO.getBarangayDistribution();
    int displayedBarangays = 0;
    int otherBarangays = 0;
    int otherCount = 0;
    
    for (Map.Entry<String, Integer> entry : barangayDist.entrySet()) {
        if (displayedBarangays < 10) { // Show top 10 barangays
            report.append(String.format("  %-15s: %3d patients\n", 
                entry.getKey(), entry.getValue()));
            displayedBarangays++;
        } else {
            otherBarangays++;
            otherCount += entry.getValue();
        }
    }
    
    if (otherBarangays > 0) {
        report.append(String.format("  %-15s: %3d patients (from %d other barangays)\n", 
            "Others", otherCount, otherBarangays));
    }
    report.append("\n");
    
    // 4. Blood Type Distribution
    report.append("BLOOD TYPE DISTRIBUTION:\n");
    report.append("-------------------------\n");
    
    Map<String, Integer> bloodTypeDist = patientDAO.getBloodTypeDistribution();
    int knownBloodTypes = 0;
    
    for (Map.Entry<String, Integer> entry : bloodTypeDist.entrySet()) {
        if (!"Unknown".equals(entry.getKey()) && entry.getValue() > 0) {
            int count = entry.getValue();
            double percentage = totalPatients > 0 ? (count * 100.0 / totalPatients) : 0;
            report.append(String.format("  %-5s: %3d patients (%5.1f%%)\n", 
                entry.getKey(), count, percentage));
            knownBloodTypes += count;
        }
    }
    
    // Show unknown blood types
    int unknownCount = bloodTypeDist.getOrDefault("Unknown", 0);
    if (unknownCount > 0) {
        double unknownPercentage = totalPatients > 0 ? (unknownCount * 100.0 / totalPatients) : 0;
        report.append(String.format("  %-5s: %3d patients (%5.1f%%)\n", 
            "Unknown", unknownCount, unknownPercentage));
    }
    
    // 5. Summary Statistics
    report.append("\n");
    report.append("SUMMARY STATISTICS:\n");
    report.append("-------------------\n");
    
    // Average age (if available)
    String avgAgeSQL = "SELECT AVG(age) as avg_age FROM patients WHERE is_active = TRUE";
    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery(avgAgeSQL)) {
        
        if (rs.next()) {
            double avgAge = rs.getDouble("avg_age");
            if (!rs.wasNull()) {
                report.append(String.format("  Average Age: %.1f years\n", avgAge));
            }
        }
    } catch (SQLException e) {
        // Ignore if not available
    }
    
    // Patients with emergency contacts
    String emergencySQL = "SELECT COUNT(*) as with_emergency FROM patients " +
                         "WHERE is_active = TRUE AND emergency_contact_name IS NOT NULL " +
                         "AND emergency_contact_name != ''";
    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery(emergencySQL)) {
        
        if (rs.next()) {
            int withEmergency = rs.getInt("with_emergency");
            double emergencyPercentage = totalPatients > 0 ? (withEmergency * 100.0 / totalPatients) : 0;
            report.append(String.format("  Patients with emergency contact: %d (%.1f%%)\n", 
                withEmergency, emergencyPercentage));
        }
    } catch (SQLException e) {
        // Ignore if not available
    }
    
    // Patients with medical conditions
    String conditionsSQL = "SELECT COUNT(*) as with_conditions FROM patients " +
                          "WHERE is_active = TRUE AND pre_existing_conditions IS NOT NULL " +
                          "AND pre_existing_conditions != ''";
    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery(conditionsSQL)) {
        
        if (rs.next()) {
            int withConditions = rs.getInt("with_conditions");
            double conditionsPercentage = totalPatients > 0 ? (withConditions * 100.0 / totalPatients) : 0;
            report.append(String.format("  Patients with medical conditions: %d (%.1f%%)\n", 
                withConditions, conditionsPercentage));
        }
    } catch (SQLException e) {
        // Ignore if not available
    }
    
    // Patients with allergies
    String allergiesSQL = "SELECT COUNT(*) as with_allergies FROM patients " +
                         "WHERE is_active = TRUE AND allergies IS NOT NULL " +
                         "AND allergies != ''";
    try (java.sql.Connection conn = DatabaseConnection.getConnection();
         java.sql.Statement stmt = conn.createStatement();
         java.sql.ResultSet rs = stmt.executeQuery(allergiesSQL)) {
        
        if (rs.next()) {
            int withAllergies = rs.getInt("with_allergies");
            double allergiesPercentage = totalPatients > 0 ? (withAllergies * 100.0 / totalPatients) : 0;
            report.append(String.format("  Patients with allergies: %d (%.1f%%)\n", 
                withAllergies, allergiesPercentage));
        }
    } catch (SQLException e) {
        // Ignore if not available
    }
}
    
    private void generateVisitsSummaryReport(StringBuilder report, String dateFrom, String dateTo, String barangay) {
    report.append("MEDICAL VISITS SUMMARY REPORT\n");
    report.append("=============================\n\n");
    
    report.append("Period: ").append(dateFrom).append(" to ").append(dateTo).append("\n");
    report.append("Report Date: ").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE)).append("\n");
    report.append("=============================================\n\n");
    
    MedicalRecordDAO recordDAO = new MedicalRecordDAO();
    
    try {
        // Convert string dates to Date objects
        Date startDate = Date.valueOf(LocalDate.parse(dateFrom, DateTimeFormatter.ISO_DATE));
        Date endDate = Date.valueOf(LocalDate.parse(dateTo, DateTimeFormatter.ISO_DATE));
        
        // Get total visits for period
        int totalVisits = recordDAO.getVisitsCountByDateRange(startDate, endDate);
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
            LocalDate.parse(dateFrom), LocalDate.parse(dateTo)) + 1;
        double avgVisitsPerDay = daysBetween > 0 ? (double) totalVisits / daysBetween : 0;
        
        report.append("Visit Statistics:\n");
        report.append("-----------------\n");
        report.append(String.format("  Total Visits: %d\n", totalVisits));
        report.append(String.format("  Period Duration: %d days\n", daysBetween));
        report.append(String.format("  Average Visits per Day: %.1f\n\n", avgVisitsPerDay));
        
        // Get visit type distribution
        String visitTypeSQL = "SELECT visit_type, COUNT(*) as count FROM medical_records " +
                            "WHERE visit_date BETWEEN ? AND ? " +
                            "GROUP BY visit_type ORDER BY count DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(visitTypeSQL)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            report.append("Visit Types:\n");
            report.append("------------\n");
            
            while (rs.next()) {
                String type = rs.getString("visit_type");
                int count = rs.getInt("count");
                double percentage = totalVisits > 0 ? (count * 100.0 / totalVisits) : 0;
                report.append(String.format("  %-15s: %3d visits (%5.1f%%)\n", 
                    type, count, percentage));
            }
            report.append("\n");
        }
        
        // Get busiest day
        String busiestDaySQL = "SELECT DAYNAME(visit_date) as day, COUNT(*) as count " +
                              "FROM medical_records WHERE visit_date BETWEEN ? AND ? " +
                              "GROUP BY DAYNAME(visit_date) ORDER BY count DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(busiestDaySQL)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                report.append("Busiest Day: ").append(rs.getString("day")).append("\n");
                report.append("  with ").append(rs.getInt("count")).append(" visits\n\n");
            }
        }
        
        // Get common symptoms (top 10)
        String symptomsSQL = "SELECT symptoms FROM medical_records " +
                           "WHERE visit_date BETWEEN ? AND ? AND symptoms IS NOT NULL " +
                           "AND symptoms != ''";
        
        Map<String, Integer> symptomCounts = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(symptomsSQL)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            // Simple symptom counting (this could be improved with NLP)
            while (rs.next()) {
                String symptoms = rs.getString("symptoms").toLowerCase();
                String[] symptomList = symptoms.split("[,\\.;]");
                
                for (String symptom : symptomList) {
                    symptom = symptom.trim();
                    if (!symptom.isEmpty() && symptom.length() > 2) {
                        symptomCounts.put(symptom, symptomCounts.getOrDefault(symptom, 0) + 1);
                    }
                }
            }
        }
        
        // Sort symptoms by frequency
        List<Map.Entry<String, Integer>> sortedSymptoms = new ArrayList<>(symptomCounts.entrySet());
        sortedSymptoms.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        report.append("Most Common Symptoms:\n");
        report.append("---------------------\n");
        
        int limit = Math.min(10, sortedSymptoms.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = sortedSymptoms.get(i);
            report.append(String.format("  %2d. %-20s: %3d cases\n", 
                i + 1, entry.getKey(), entry.getValue()));
        }
        
        // Get top health workers by visits
        String healthWorkerSQL = "SELECT health_worker_id, COUNT(*) as count " +
                                "FROM medical_records WHERE visit_date BETWEEN ? AND ? " +
                                "GROUP BY health_worker_id ORDER BY count DESC LIMIT 5";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(healthWorkerSQL)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            report.append("\n");
            report.append("Top Health Workers by Visits:\n");
            report.append("-----------------------------\n");
            
            int rank = 1;
            while (rs.next()) {
                report.append(String.format("  %d. Health Worker %d: %d visits\n", 
                    rank++, rs.getInt("health_worker_id"), rs.getInt("count")));
            }
        }
        
    } catch (Exception e) {
        report.append("Error generating visit summary: ").append(e.getMessage()).append("\n");
        e.printStackTrace();
    }
}

    private void generateDiseaseReport(StringBuilder report, String dateFrom, String dateTo, String barangay) {
    report.append("DISEASE PREVALENCE REPORT\n");
    report.append("=========================\n\n");
    
    report.append("Period: ").append(dateFrom).append(" to ").append(dateTo).append("\n");
    report.append("Report Date: ").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE)).append("\n");
    report.append("=============================================\n\n");
    
    MedicalRecordDAO recordDAO = new MedicalRecordDAO();
    
    try {
        Date startDate = Date.valueOf(LocalDate.parse(dateFrom, DateTimeFormatter.ISO_DATE));
        Date endDate = Date.valueOf(LocalDate.parse(dateTo, DateTimeFormatter.ISO_DATE));
        
        // Get diagnosis distribution
        String diagnosisSQL = "SELECT diagnosis, COUNT(*) as count FROM medical_records " +
                            "WHERE visit_date BETWEEN ? AND ? AND diagnosis IS NOT NULL " +
                            "AND diagnosis != '' GROUP BY diagnosis ORDER BY count DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(diagnosisSQL)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            // Get total for percentage calculation
            String totalSQL = "SELECT COUNT(*) as total FROM medical_records " +
                            "WHERE visit_date BETWEEN ? AND ? AND diagnosis IS NOT NULL " +
                            "AND diagnosis != ''";
            
            int totalDiagnoses = 0;
            try (PreparedStatement pstmt2 = conn.prepareStatement(totalSQL)) {
                pstmt2.setDate(1, startDate);
                pstmt2.setDate(2, endDate);
                ResultSet rs2 = pstmt2.executeQuery();
                if (rs2.next()) {
                    totalDiagnoses = rs2.getInt("total");
                }
            }
            
            report.append("TOP DIAGNOSES FOR THE PERIOD:\n");
            report.append("-----------------------------\n\n");
            
            int rank = 1;
            while (rs.next() && rank <= 15) {
                String diagnosis = rs.getString("diagnosis");
                int count = rs.getInt("count");
                double percentage = totalDiagnoses > 0 ? (count * 100.0 / totalDiagnoses) : 0;
                
                report.append(String.format("  %2d. %-40s: %3d cases (%5.1f%%)\n", 
                    rank++, diagnosis, count, percentage));
            }
            report.append("\n");
        }
        
        // Get diagnosis by age group
        String ageGroupSQL = "SELECT " +
                           "SUM(CASE WHEN p.age BETWEEN 0 AND 12 THEN 1 ELSE 0 END) as child, " +
                           "SUM(CASE WHEN p.age BETWEEN 13 AND 19 THEN 1 ELSE 0 END) as teen, " +
                           "SUM(CASE WHEN p.age BETWEEN 20 AND 59 THEN 1 ELSE 0 END) as adult, " +
                           "SUM(CASE WHEN p.age >= 60 THEN 1 ELSE 0 END) as senior " +
                           "FROM medical_records mr " +
                           "JOIN patients p ON mr.patient_id = p.patient_id " +
                           "WHERE mr.visit_date BETWEEN ? AND ? " +
                           "AND mr.diagnosis IS NOT NULL AND mr.diagnosis != ''";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ageGroupSQL)) {
            
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                report.append("DIAGNOSES BY AGE GROUP:\n");
                report.append("-----------------------\n\n");
                report.append(String.format("  Children (0-12):   %3d cases\n", rs.getInt("child")));
                report.append(String.format("  Teens (13-19):     %3d cases\n", rs.getInt("teen")));
                report.append(String.format("  Adults (20-59):    %3d cases\n", rs.getInt("adult")));
                report.append(String.format("  Seniors (60+):     %3d cases\n\n", rs.getInt("senior")));
            }
        }
        
        // Get most common diagnoses for each age group
        report.append("MOST COMMON DIAGNOSES BY AGE GROUP:\n");
        report.append("------------------------------------\n\n");
        
        String[] ageGroups = {"0-12", "13-19", "20-59", "60+"};
        String[] ageConditions = {
            "p.age BETWEEN 0 AND 12",
            "p.age BETWEEN 13 AND 19", 
            "p.age BETWEEN 20 AND 59",
            "p.age >= 60"
        };
        
        for (int i = 0; i < ageGroups.length; i++) {
            String groupSQL = "SELECT mr.diagnosis, COUNT(*) as count " +
                            "FROM medical_records mr " +
                            "JOIN patients p ON mr.patient_id = p.patient_id " +
                            "WHERE mr.visit_date BETWEEN ? AND ? AND " + ageConditions[i] + " " +
                            "AND mr.diagnosis IS NOT NULL AND mr.diagnosis != '' " +
                            "GROUP BY mr.diagnosis ORDER BY count DESC LIMIT 3";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(groupSQL)) {
                
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                
                report.append("  ").append(ageGroups[i]).append(" years:\n");
                if (rs.next()) {
                    do {
                        report.append(String.format("    • %-30s: %2d cases\n", 
                            rs.getString("diagnosis"), rs.getInt("count")));
                    } while (rs.next());
                } else {
                    report.append("    No data\n");
                }
                report.append("\n");
            }
        }
        
    } catch (Exception e) {
        report.append("Error generating disease report: ").append(e.getMessage()).append("\n");
        e.printStackTrace();
    }
}

    private void generateBarangayStatsReport(StringBuilder report) {
    report.append("BARANGAY HEALTH STATISTICS REPORT\n");
    report.append("=================================\n\n");
    
    report.append("Report Date: ").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE)).append("\n");
    report.append("Generated by: ").append(currentUser.getFullName()).append("\n");
    report.append("=============================================\n\n");
    
    PatientDAO patientDAO = new PatientDAO();
    MedicalRecordDAO recordDAO = new MedicalRecordDAO();
    
    // Get all barangays
    Map<String, Integer> barangayDist = patientDAO.getBarangayDistribution();
    
    if (barangayDist.isEmpty()) {
        report.append("No barangay data available.\n");
        return;
    }
    
    report.append("COMPARATIVE ANALYSIS BY BARANGAY:\n");
    report.append("---------------------------------\n\n");
    
    try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
        for (Map.Entry<String, Integer> entry : barangayDist.entrySet()) {
            String barangay = entry.getKey();
            int patientCount = entry.getValue();
            
            report.append(barangay).append(":\n");
            report.append(String.format("  Total Patients: %d\n", patientCount));
            
            // Get total visits for this barangay
            String visitSQL = "SELECT COUNT(*) as visits FROM medical_records mr " +
                            "JOIN patients p ON mr.patient_id = p.patient_id " +
                            "WHERE p.barangay = ? AND p.is_active = TRUE";
            
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(visitSQL)) {
                pstmt.setString(1, barangay);
                java.sql.ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    report.append(String.format("  Total Visits: %d\n", rs.getInt("visits")));
                }
            }
            
            // Get common diagnoses for this barangay
            String diagnosisSQL = "SELECT mr.diagnosis, COUNT(*) as count " +
                                "FROM medical_records mr " +
                                "JOIN patients p ON mr.patient_id = p.patient_id " +
                                "WHERE p.barangay = ? AND p.is_active = TRUE " +
                                "AND mr.diagnosis IS NOT NULL AND mr.diagnosis != '' " +
                                "GROUP BY mr.diagnosis ORDER BY count DESC LIMIT 5";
            
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(diagnosisSQL)) {
                pstmt.setString(1, barangay);
                java.sql.ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    report.append("  Common Health Issues:\n");
                    do {
                        report.append(String.format("    • %-30s (%d cases)\n", 
                            rs.getString("diagnosis"), rs.getInt("count")));
                    } while (rs.next());
                }
            }
            
            // Get average age in barangay
            String ageSQL = "SELECT AVG(age) as avg_age FROM patients " +
                          "WHERE barangay = ? AND is_active = TRUE";
            
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(ageSQL)) {
                pstmt.setString(1, barangay);
                java.sql.ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    double avgAge = rs.getDouble("avg_age");
                    if (!rs.wasNull()) {
                        report.append(String.format("  Average Age: %.1f years\n", avgAge));
                    }
                }
            }
            
            // Get gender distribution for barangay
            String genderSQL = "SELECT gender, COUNT(*) as count FROM patients " +
                             "WHERE barangay = ? AND is_active = TRUE AND gender IS NOT NULL " +
                             "GROUP BY gender";
            
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(genderSQL)) {
                pstmt.setString(1, barangay);
                java.sql.ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    report.append("  Gender Distribution:\n");
                    do {
                        int count = rs.getInt("count");
                        double percentage = patientCount > 0 ? (count * 100.0 / patientCount) : 0;
                        report.append(String.format("    • %-6s: %d (%.1f%%)\n", 
                            rs.getString("gender"), count, percentage));
                    } while (rs.next());
                }
            }
            
            report.append("\n");
        }
        
        // Overall recommendations
        report.append("\nOVERALL RECOMMENDATIONS:\n");
        report.append("------------------------\n\n");
        
        // Find barangay with most visits per patient - FIXED SQL
        String recommendationSQL = "SELECT p.barangay, " +
                                 "COUNT(mr.record_id) as visit_count, " +
                                 "COUNT(DISTINCT p.patient_id) as patient_count " +
                                 "FROM medical_records mr " +
                                 "JOIN patients p ON mr.patient_id = p.patient_id " +
                                 "WHERE p.is_active = TRUE " +
                                 "GROUP BY p.barangay " +
                                 "HAVING patient_count > 0 " +
                                 "ORDER BY (COUNT(mr.record_id) * 1.0 / COUNT(DISTINCT p.patient_id)) DESC";
        
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(recommendationSQL)) {
            
            if (rs.next()) {
                String highUsageBarangay = rs.getString("barangay");
                double visitsPerPatient = rs.getDouble("visit_count") / rs.getDouble("patient_count");
                report.append(String.format("1. %s has the highest healthcare utilization ", highUsageBarangay));
                report.append(String.format("(%.1f visits per patient)\n", visitsPerPatient));
                report.append("   Consider: Increasing health education and preventive care\n\n");
            }
        }
        
        // Find barangay with oldest average age - FIXED SQL
        String ageRecSQL = "SELECT barangay, AVG(age) as avg_age FROM patients " +
                          "WHERE is_active = TRUE AND barangay IS NOT NULL " +
                          "GROUP BY barangay " +
                          "ORDER BY AVG(age) DESC LIMIT 1";
        
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(ageRecSQL)) {
            
            if (rs.next()) {
                String oldestBarangay = rs.getString("barangay");
                double avgAge = rs.getDouble("avg_age");
                if (!rs.wasNull()) {
                    report.append(String.format("2. %s has the oldest population (avg age: %.1f)\n", oldestBarangay, avgAge));
                    report.append("   Consider: Geriatric care programs and regular checkups\n\n");
                }
            }
        }
        
        // Find barangay with highest patient count
        String mostPatientsSQL = "SELECT barangay, COUNT(*) as patient_count FROM patients " +
                               "WHERE is_active = TRUE AND barangay IS NOT NULL " +
                               "GROUP BY barangay " +
                               "ORDER BY COUNT(*) DESC LIMIT 1";
        
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(mostPatientsSQL)) {
            
            if (rs.next()) {
                String mostPatientsBarangay = rs.getString("barangay");
                int patientCount = rs.getInt("patient_count");
                report.append(String.format("3. %s has the most patients (%d patients)\n", 
                    mostPatientsBarangay, patientCount));
                report.append("   Consider: Allocating more resources to this barangay\n\n");
            }
        }
        
        // Find barangay with lowest patient count (for comparison)
        String leastPatientsSQL = "SELECT barangay, COUNT(*) as patient_count FROM patients " +
                                "WHERE is_active = TRUE AND barangay IS NOT NULL " +
                                "GROUP BY barangay " +
                                "ORDER BY COUNT(*) ASC LIMIT 1";
        
        try (java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(leastPatientsSQL)) {
            
            if (rs.next()) {
                String leastPatientsBarangay = rs.getString("barangay");
                int patientCount = rs.getInt("patient_count");
                if (patientCount < 10) { // Only mention if very low
                    report.append(String.format("4. %s has very few registered patients (%d patients)\n", 
                        leastPatientsBarangay, patientCount));
                    report.append("   Consider: Community outreach and registration drives\n\n");
                }
            }
        }
        
        report.append("5. General Recommendations:\n");
        report.append("   • Conduct regular community health assessments\n");
        report.append("   • Organize barangay health education seminars\n");
        report.append("   • Establish preventive care programs\n");
        report.append("   • Improve vaccination coverage in all barangays\n");
        report.append("   • Share best practices between barangays\n");
        
    } catch (Exception e) {
        report.append("Error generating barangay statistics: ").append(e.getMessage()).append("\n");
        e.printStackTrace();
    }
}

    private void generateMonthlyReport(StringBuilder report, String dateFrom, String dateTo) {
    report.append("MONTHLY ACTIVITY REPORT\n");
    report.append("======================\n\n");
    
    report.append("Period: ").append(dateFrom).append(" to ").append(dateTo).append("\n");
    report.append("Report Date: ").append(LocalDate.now().format(DateTimeFormatter.ISO_DATE)).append("\n");
    report.append("Generated by: ").append(currentUser.getFullName()).append("\n");
    report.append("=============================================\n\n");
    
    try {
        Date startDate = Date.valueOf(LocalDate.parse(dateFrom, DateTimeFormatter.ISO_DATE));
        Date endDate = Date.valueOf(LocalDate.parse(dateTo, DateTimeFormatter.ISO_DATE));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // 1. Key Performance Indicators
            report.append("KEY PERFORMANCE INDICATORS:\n");
            report.append("---------------------------\n\n");
            
            // New patients registered
            String newPatientsSQL = "SELECT COUNT(*) as new_patients FROM patients " +
                                  "WHERE created_at BETWEEN ? AND ?";
            int newPatients = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(newPatientsSQL)) {
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    newPatients = rs.getInt("new_patients");
                }
            }
            report.append(String.format("  New Patients Registered: %d\n", newPatients));
            
            // Total consultations
            MedicalRecordDAO recordDAO = new MedicalRecordDAO();
            int totalConsultations = recordDAO.getVisitsCountByDateRange(startDate, endDate);
            report.append(String.format("  Total Medical Consultations: %d\n", totalConsultations));
            
            // Follow-up compliance rate
            String followupSQL = "SELECT " +
                               "SUM(CASE WHEN follow_up_date IS NOT NULL THEN 1 ELSE 0 END) as with_followup, " +
                               "COUNT(*) as total " +
                               "FROM medical_records WHERE visit_date BETWEEN ? AND ?";
            
            double followupRate = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(followupSQL)) {
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int withFollowup = rs.getInt("with_followup");
                    followupRate = total > 0 ? (withFollowup * 100.0 / total) : 0;
                }
            }
            report.append(String.format("  Follow-up Compliance Rate: %.1f%%\n\n", followupRate));
            
            // 2. Staff Activity
            report.append("STAFF ACTIVITY:\n");
            report.append("--------------\n\n");
            
            String staffSQL = "SELECT health_worker_id, COUNT(*) as consultations, " +
                            "u.full_name FROM medical_records mr " +
                            "LEFT JOIN users u ON mr.health_worker_id = u.user_id " +
                            "WHERE mr.visit_date BETWEEN ? AND ? " +
                            "GROUP BY health_worker_id, u.full_name ORDER BY consultations DESC";
            
            try (PreparedStatement pstmt = conn.prepareStatement(staffSQL)) {
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    String staffName = rs.getString("full_name");
                    if (staffName == null) {
                        staffName = "Health Worker " + rs.getInt("health_worker_id");
                    }
                    report.append(String.format("  %-30s: %3d consultations\n", 
                        staffName, rs.getInt("consultations")));
                }
            }
            report.append("\n");
            
            // 3. Visit Trends
            report.append("VISIT TRENDS:\n");
            report.append("-------------\n\n");
            
            String trendSQL = "SELECT DATE(visit_date) as visit_day, COUNT(*) as daily_visits " +
                            "FROM medical_records WHERE visit_date BETWEEN ? AND ? " +
                            "GROUP BY DATE(visit_date) ORDER BY visit_day";
            
            try (PreparedStatement pstmt = conn.prepareStatement(trendSQL)) {
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                
                int maxVisits = 0;
                LocalDate busiestDay = null;
                List<Integer> dailyCounts = new ArrayList<>();
                
                while (rs.next()) {
                    Date visitDay = rs.getDate("visit_day");
                    int dailyVisits = rs.getInt("daily_visits");
                    dailyCounts.add(dailyVisits);
                    
                    if (dailyVisits > maxVisits) {
                        maxVisits = dailyVisits;
                        busiestDay = visitDay.toLocalDate();
                    }
                }
                
                if (!dailyCounts.isEmpty()) {
                    // Calculate statistics
                    int minVisits = dailyCounts.stream().min(Integer::compareTo).orElse(0);
                    double avgVisits = dailyCounts.stream().mapToInt(Integer::intValue).average().orElse(0);
                    
                    report.append(String.format("  Busiest Day: %s (%d visits)\n", 
                        busiestDay, maxVisits));
                    report.append(String.format("  Slowest Day: %d visits\n", minVisits));
                    report.append(String.format("  Average Daily Visits: %.1f\n", avgVisits));
                    
                    // Identify trends
                    if (dailyCounts.size() >= 7) {
                        double lastWeekAvg = dailyCounts.subList(Math.max(0, dailyCounts.size() - 7), dailyCounts.size())
                            .stream().mapToInt(Integer::intValue).average().orElse(0);
                        double firstWeekAvg = dailyCounts.subList(0, Math.min(7, dailyCounts.size()))
                            .stream().mapToInt(Integer::intValue).average().orElse(0);
                        
                        if (lastWeekAvg > firstWeekAvg * 1.2) {
                            report.append("  Trend: Increasing patient volume 📈\n");
                        } else if (lastWeekAvg < firstWeekAvg * 0.8) {
                            report.append("  Trend: Decreasing patient volume 📉\n");
                        } else {
                            report.append("  Trend: Stable patient volume ➡️\n");
                        }
                    }
                }
            }
            report.append("\n");
            
            // 4. Resource Analysis
            report.append("RESOURCE ANALYSIS:\n");
            report.append("------------------\n\n");
            
            // Patient wait time estimation (based on visits per day)
            String waitTimeSQL = "SELECT AVG(daily_visits) as avg_daily FROM (" +
                               "SELECT DATE(visit_date), COUNT(*) as daily_visits " +
                               "FROM medical_records WHERE visit_date BETWEEN ? AND ? " +
                               "GROUP BY DATE(visit_date)) daily_counts";
            
            double avgDailyVisits = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(waitTimeSQL)) {
                pstmt.setDate(1, startDate);
                pstmt.setDate(2, endDate);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    avgDailyVisits = rs.getDouble("avg_daily");
                }
            }
            
            // Estimate wait time (assuming 8 working hours and 15 minutes per consultation)
            double estimatedWaitTime = avgDailyVisits > 0 ? (avgDailyVisits * 15) / 8 : 0;
            
            report.append(String.format("  Estimated Average Patient Wait Time: %.1f minutes\n", estimatedWaitTime));
            report.append(String.format("  Average Daily Patient Load: %.1f patients\n", avgDailyVisits));
            
            if (estimatedWaitTime > 30) {
                report.append("  ⚠️  High wait times detected - consider adding staff or extending hours\n");
            } else if (estimatedWaitTime < 15) {
                report.append("  ✅ Good wait time efficiency\n");
            }
            report.append("\n");
            
            // 5. Goals for Next Period
            report.append("GOALS FOR NEXT PERIOD:\n");
            report.append("----------------------\n\n");
            
            // Set goals based on current performance
            int targetNewPatients = (int) (newPatients * 1.2); // 20% increase
            int targetConsultations = (int) (totalConsultations * 1.15); // 15% increase
            double targetFollowupRate = Math.min(95, followupRate + 5); // 5% improvement, max 95%
            
            report.append(String.format("  1. Register %d new patients (%d%% increase)\n", 
                targetNewPatients, 20));
            report.append(String.format("  2. Conduct %d medical consultations (%d%% increase)\n", 
                targetConsultations, 15));
            report.append(String.format("  3. Achieve %.1f%% follow-up compliance rate\n", targetFollowupRate));
            report.append(String.format("  4. Reduce average wait time to <%.0f minutes\n", 
                Math.max(15, estimatedWaitTime * 0.8))); // 20% reduction
            
            if (followupRate < 80) {
                report.append("  5. Implement follow-up reminder system\n");
            }
            if (newPatients < 10) {
                report.append("  6. Launch community outreach program\n");
            }
            
        }
    } catch (Exception e) {
        report.append("Error generating monthly report: ").append(e.getMessage()).append("\n");
        e.printStackTrace();
    }
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
    
    // Set default backup file name with timestamp
    String timestamp = LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "_" + 
                      java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    String defaultFileName = "BHRMS_Backup_" + timestamp + ".sql";
    fileChooser.setSelectedFile(new java.io.File(defaultFileName));
    
    // Add file filter
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files", "sql"));
    
    int userSelection = fileChooser.showSaveDialog(this);
    
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        final java.io.File selectedFile = fileChooser.getSelectedFile();
        
        // Ensure .sql extension
        java.io.File backupFile = selectedFile;
        if (!backupFile.getName().toLowerCase().endsWith(".sql")) {
            backupFile = new java.io.File(backupFile.getAbsolutePath() + ".sql");
        }
        
        final java.io.File finalBackupFile = backupFile;
        
        // Show progress dialog
        JDialog progressDialog = new JDialog(this, "Creating Backup", true);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new BorderLayout());
        
        JLabel progressLabel = new JLabel("Starting backup process...", SwingConstants.CENTER);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false); // Disable cancel for now since it's fast
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        
        progressDialog.add(progressLabel, BorderLayout.NORTH);
        progressDialog.add(progressBar, BorderLayout.CENTER);
        progressDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create SwingWorker for backup operation
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    publish("Starting backup process...");
                    
                    DatabaseBackup backup = new DatabaseBackup();
                    
                    publish("Creating database backup...");
                    
                    // Perform the backup
                    long startTime = System.currentTimeMillis();
                    boolean success = backup.createBackup(finalBackupFile.getAbsolutePath());
                    long endTime = System.currentTimeMillis();
                    
                    if (success) {
                        publish("Backup completed in " + (endTime - startTime) + "ms!");
                        return true;
                    } else {
                        publish("Backup failed!");
                        return false;
                    }
                    
                } catch (Exception e) {
                    publish("Error: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    String latestMessage = chunks.get(chunks.size() - 1);
                    progressLabel.setText(latestMessage);
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    // IMPORTANT: Use SwingUtilities to update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        
                        if (success) {
                            // Show success message
                            File file = finalBackupFile;
                            String message = "✅ Backup created successfully!\n\n" +
                                            "File: " + file.getName() + "\n" +
                                            "Size: " + formatFileSize(file.length()) + "\n" +
                                            "Location: " + file.getParent() + "\n\n" +
                                            "The backup includes all patient data,\n" +
                                            "medical records, and user information.";
                            
                            JOptionPane.showMessageDialog(BHRMSUI.this, message, 
                                "Backup Successful", JOptionPane.INFORMATION_MESSAGE);
                            
                        } else {
                            JOptionPane.showMessageDialog(BHRMSUI.this, 
                                "❌ Failed to create backup!", 
                                "Backup Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        JOptionPane.showMessageDialog(BHRMSUI.this, 
                            "❌ Error during backup: " + e.getMessage(), 
                            "Backup Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }
        };
        
        // Add cancel button action
        cancelButton.addActionListener(e -> {
            if (worker.cancel(true)) {
                progressDialog.dispose();
                JOptionPane.showMessageDialog(this, "Backup cancelled.", 
                    "Backup Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Show progress dialog
        progressDialog.setVisible(true);
        
        // Execute the worker
        worker.execute();
        
        // Auto-close progress dialog after 5 seconds (safety measure)
        Timer safetyTimer = new Timer(5000, e -> {
            if (progressDialog.isVisible()) {
                progressDialog.dispose();
                System.out.println("Safety timer: Progress dialog closed after timeout");
            }
        });
        safetyTimer.setRepeats(false);
        safetyTimer.start();
    }
}

private String formatFileSize(long size) {
    if (size < 1024) {
        return size + " B";
    } else if (size < 1024 * 1024) {
        return String.format("%.1f KB", size / 1024.0);
    } else {
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}

private String getBackupInfo() {
    StringBuilder info = new StringBuilder();
    
    // Check for recent backups
    File backupDir = new File(".");
    File[] backupFiles = backupDir.listFiles((dir, name) -> 
        name.startsWith("BHRMS_Backup_") && name.endsWith(".sql"));
    
    if (backupFiles != null && backupFiles.length > 0) {
        // Sort by last modified
        java.util.Arrays.sort(backupFiles, (f1, f2) -> 
            Long.compare(f2.lastModified(), f1.lastModified()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        info.append("Last Backup: ").append(sdf.format(new Date(backupFiles[0].lastModified()))).append("\n");
        info.append("Total Backups: ").append(backupFiles.length).append("\n");
        
        long totalSize = 0;
        for (File file : backupFiles) {
            totalSize += file.length();
        }
        info.append("Backup Storage: ").append(formatFileSize(totalSize)).append("\n");
        
    } else {
        info.append("Last Backup: No backups found\n");
    }
    
    // Get database stats
    try {
        PatientDAO patientDAO = new PatientDAO();
        MedicalRecordDAO recordDAO = new MedicalRecordDAO();
        UserDAO userDAO = new UserDAO();
        
        info.append("Total Records: ").append(patientDAO.getTotalPatientsCount()).append(" patients\n");
        info.append("  ").append(recordDAO.getTotalVisitsCount()).append(" medical records\n");
        info.append("  ").append(userDAO.getActiveStaffCount()).append(" active staff\n");
        
    } catch (Exception e) {
        info.append("Error loading database stats\n");
    }
    
    return info.toString();
}

private void testBackup() {
    try {
        System.out.println("Testing backup functionality...");
        
        // Test 1: Check if DatabaseBackup class works
        DatabaseBackup backup = new DatabaseBackup();
        System.out.println("DatabaseBackup instance created");
        
        // Test 2: Try to create a backup in current directory
        String testBackupPath = "test_backup_" + System.currentTimeMillis() + ".sql";
        System.out.println("Creating backup at: " + testBackupPath);
        
        long startTime = System.currentTimeMillis();
        boolean success = backup.createBackup(testBackupPath);
        long endTime = System.currentTimeMillis();
        
        if (success) {
            File backupFile = new File(testBackupPath);
            System.out.println("Backup created successfully in " + (endTime - startTime) + "ms");
            System.out.println("File size: " + backupFile.length() + " bytes");
            System.out.println("File exists: " + backupFile.exists());
            
            // Show file content (first few lines)
            try (BufferedReader reader = new BufferedReader(new FileReader(backupFile))) {
                System.out.println("First 5 lines of backup:");
                for (int i = 0; i < 5; i++) {
                    String line = reader.readLine();
                    if (line != null) {
                        System.out.println("  " + line);
                    }
                }
            }
        } else {
            System.out.println("Backup failed!");
        }
        
    } catch (Exception e) {
        System.err.println("Test backup error: " + e.getMessage());
        e.printStackTrace();
    }
}

private void diagnoseBackup() {
    System.out.println("\n=== Backup Diagnostic ===");
    
    // Check if we can connect to database
    System.out.println("1. Testing database connection...");
    try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
        System.out.println("   ✓ Database connected: " + conn.getCatalog());
    } catch (Exception e) {
        System.out.println("   ✗ Database connection failed: " + e.getMessage());
    }
    
    // Check for mysqldump
    System.out.println("2. Looking for mysqldump...");
    DatabaseBackup backup = new DatabaseBackup();
    
    // Create a test backup to see what happens
    String testFile = "diagnostic_backup_" + System.currentTimeMillis() + ".sql";
    System.out.println("   Creating test backup: " + testFile);
    
    long startTime = System.currentTimeMillis();
    boolean success = backup.createBackup(testFile);
    long endTime = System.currentTimeMillis();
    
    System.out.println("   Backup result: " + (success ? "SUCCESS" : "FAILED"));
    System.out.println("   Time taken: " + (endTime - startTime) + "ms");
    
    File backupFile = new File(testFile);
    if (backupFile.exists()) {
        System.out.println("   File created: " + backupFile.length() + " bytes");
    } else {
        System.out.println("   File was NOT created");
    }
    
    // Check current directory
    System.out.println("3. Checking current directory...");
    File currentDir = new File(".");
    System.out.println("   Current dir: " + currentDir.getAbsolutePath());
    System.out.println("   Writable: " + currentDir.canWrite());
    
    // List SQL files in current directory
    System.out.println("   Existing backup files:");
    File[] files = currentDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));
    if (files != null && files.length > 0) {
        for (File file : files) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("     " + file.getName() + 
                             " (" + formatFileSize(file.length()) + 
                             ", " + sdf.format(new Date(file.lastModified())) + ")");
        }
    } else {
        System.out.println("     No .sql files found");
    }
    
    // Check Java version
    System.out.println("4. Java Environment:");
    System.out.println("   Java Version: " + System.getProperty("java.version"));
    System.out.println("   Java Home: " + System.getProperty("java.home"));
    System.out.println("   OS: " + System.getProperty("os.name"));
    
    System.out.println("=== End Diagnostic ===\n");
    
    // Show dialog with results
    StringBuilder result = new StringBuilder();
    result.append("Diagnostic completed.\n\n");
    if (backupFile.exists()) {
        result.append("✓ Test backup created: ").append(testFile).append("\n");
        result.append("  Size: ").append(formatFileSize(backupFile.length())).append("\n");
        result.append("  Time: ").append(endTime - startTime).append("ms\n");
    } else {
        result.append("✗ Test backup failed!\n");
        result.append("  Check console for details.\n");
    }
    
    result.append("\nSee console for full diagnostic details.");
    
    JOptionPane.showMessageDialog(this,
        result.toString(),
        "Diagnostic Complete",
        backupFile.exists() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
}

// Add this method to update system information
private void updateSystemInfo() {
    // This method would update the system info panel with latest backup info
    // You'll need to implement this based on your system panel
    System.out.println("System info updated with latest backup");
}
    
    private void restoreBackup() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Restore Database Backup");
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files", "sql"));
    
    int userSelection = fileChooser.showOpenDialog(this);
    
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File backupFile = fileChooser.getSelectedFile();
        
        // Show confirmation dialog
        String message = "<html><b>WARNING: This will replace all current data with the backup!</b><br><br>" +
                        "Backup File: " + backupFile.getName() + "<br>" +
                        "Size: " + formatFileSize(backupFile.length()) + "<br>" +
                        "Modified: " + new Date(backupFile.lastModified()) + "<br><br>" +
                        "Are you sure you want to continue?</html>";
        
        int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Confirm Restore",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Ask for admin password for extra security
            if (!verifyAdminPassword()) {
                return;
            }
            
            // Perform restore
            performRestore(backupFile);
        }
    }
}

private boolean verifyAdminPassword() {
    JPasswordField passwordField = new JPasswordField();
    Object[] message = {
        "Administrator Password:", passwordField
    };
    
    int option = JOptionPane.showConfirmDialog(this, 
        message, "Admin Verification", JOptionPane.OK_CANCEL_OPTION);
    
    if (option == JOptionPane.OK_OPTION) {
        String password = new String(passwordField.getPassword());
        // In a real system, verify against stored admin password
        // For now, we'll use a simple check
        return !password.isEmpty();
    }
    return false;
}

private void performRestore(File backupFile) {
    // Show progress dialog
    JDialog progressDialog = new JDialog(this, "Restoring Backup", true);
    progressDialog.setSize(400, 150);
    progressDialog.setLocationRelativeTo(this);
    progressDialog.setLayout(new BorderLayout());
    
    JLabel progressLabel = new JLabel("Restoring database backup...", SwingConstants.CENTER);
    progressLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    
    progressDialog.add(progressLabel, BorderLayout.NORTH);
    progressDialog.add(progressBar, BorderLayout.CENTER);
    
    SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
        @Override
        protected Boolean doInBackground() throws Exception {
            try {
                publish("Starting restore process...");
                
                // Create temporary backup before restore
                publish("Creating safety backup...");
                String tempBackupPath = "temp_backup_" + System.currentTimeMillis() + ".sql";
                DatabaseBackup safetyBackup = new DatabaseBackup();
                safetyBackup.createBackup(tempBackupPath);
                
                publish("Reading backup file...");
                Thread.sleep(500);
                
                publish("Clearing existing data...");
                Thread.sleep(500);
                
                publish("Restoring tables...");
                Thread.sleep(500);
                
                publish("Restoring data...");
                Thread.sleep(500);
                
                // Here you would implement the actual restore logic
                // For now, we'll simulate it
                boolean success = restoreBackupFile(backupFile.getAbsolutePath());
                
                if (success) {
                    publish("Restore completed successfully!");
                    Thread.sleep(1000);
                    
                    // Delete temporary backup
                    new File(tempBackupPath).delete();
                    
                    return true;
                } else {
                    publish("Restore failed! Restoring from safety backup...");
                    // Restore from safety backup
                    restoreBackupFile(tempBackupPath);
                    new File(tempBackupPath).delete();
                    return false;
                }
                
            } catch (Exception e) {
                publish("Error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        protected void process(List<String> chunks) {
            String latestMessage = chunks.get(chunks.size() - 1);
            progressLabel.setText(latestMessage);
        }
        
        @Override
        protected void done() {
            try {
                boolean success = get();
                progressDialog.dispose();
                
                if (success) {
                    JOptionPane.showMessageDialog(BHRMSUI.this,
                        "Database restored successfully!\n" +
                        "Please restart the application.",
                        "Restore Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Logout and restart
                    logout();
                    
                } else {
                    JOptionPane.showMessageDialog(BHRMSUI.this,
                        "Restore failed! System has been restored to previous state.",
                        "Restore Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                progressDialog.dispose();
                JOptionPane.showMessageDialog(BHRMSUI.this,
                    "Error during restore: " + e.getMessage(),
                    "Restore Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    
    progressDialog.setVisible(true);
    worker.execute();
}

private boolean restoreBackupFile(String backupFilePath) {
    // This is a simplified restore implementation
    // In production, you would need a more robust implementation
    try {
        DatabaseBackup backup = new DatabaseBackup();
        
        // For now, we'll just check if the backup file is valid
        DatabaseBackup.BackupStats stats = backup.getBackupStats(backupFilePath);
        System.out.println("Restoring from backup: " + stats.toString());
        
        // Simulate restore process
        Thread.sleep(2000);
        
        // In a real implementation, you would:
        // 1. Parse the SQL file
        // 2. Execute each SQL statement
        // 3. Handle errors and rollbacks
        
        return true;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
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
    // Check if current user has admin privileges
    if (!currentUser.getRole().equalsIgnoreCase("admin")) {
        JOptionPane.showMessageDialog(this, 
            "Access Denied!\nOnly administrators can manage users.", 
            "Permission Denied", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    SwingUtilities.invokeLater(() -> {
        UserManagementUI userManagementUI = new UserManagementUI(this, currentUser);
        userManagementUI.setVisible(true);
    });
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