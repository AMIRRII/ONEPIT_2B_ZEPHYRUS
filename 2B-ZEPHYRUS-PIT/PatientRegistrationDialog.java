import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// NOTE: This dialog needs the Patient class (with its addPatient and updatePatient methods)
// to be fully functional.

public class PatientRegistrationDialog extends JDialog {
    private JTextField nameField, ageField, contactField;
    private JTextArea addressArea;
    private JComboBox<String> sexComboBox;
    
    // Optional: Hold the ID if we are editing an existing patient
    private int patientId = -1; 

    /**
     * Constructor for Registration (Create) and Editing (Update)
     * @param parent The parent JFrame (MainDashboardGUI).
     * @param patientToEdit Pass null for new registration, or a Patient object for editing.
     */
    public PatientRegistrationDialog(JFrame parent, Patient patientToEdit) {
        super(parent, (patientToEdit == null ? "Register New Patient" : "Edit Patient Record"), true);
        setLayout(new BorderLayout(10, 10));
        setSize(400, 450);
        
        // Check if we are in Edit mode
        if (patientToEdit != null) {
            this.patientId = patientToEdit.getPatient_id();
        }

        // --- Form Panel (Center) ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Components
        nameField = new JTextField(20);
        ageField = new JTextField(5);
        contactField = new JTextField(15);
        addressArea = new JTextArea(3, 20);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        sexComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        
        formPanel.add(new JLabel("Sex:"));
        formPanel.add(sexComboBox);
        
        formPanel.add(new JLabel("Contact No.:"));
        formPanel.add(contactField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressScroll);
        
        // --- Populate fields if in Edit mode ---
        if (patientToEdit != null) {
            nameField.setText(patientToEdit.getFullName());
            ageField.setText(String.valueOf(patientToEdit.getAge()));
            contactField.setText(patientToEdit.getContactNumber());
            addressArea.setText(patientToEdit.getAddress());
            sexComboBox.setSelectedItem(patientToEdit.getSex());
        }

        // --- Button Panel (South) ---
        JButton saveButton = new JButton(patientId == -1 ? "Register Patient" : "Update Record");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // --- Event Handlers ---
        saveButton.addActionListener(createSaveListener(parent));
        cancelButton.addActionListener(e -> dispose());

        // --- Final Assembly ---
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    // --- Method to handle the saving/updating logic ---
    private ActionListener createSaveListener(JFrame parent) {
        return e -> {
            // 1. Get input and Validation
            String fullName = nameField.getText().trim();
            String address = addressArea.getText().trim();
            String sex = (String) sexComboBox.getSelectedItem();
            String contact = contactField.getText().trim();
            int age;

            if (fullName.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Address are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                age = Integer.parseInt(ageField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Perform CRUD Operation
            // NOTE: This relies on the Patient class being complete!
            Patient patient = new Patient(patientId, fullName, age, sex, address, contact);
            boolean success = false;
            
            if (patientId == -1) {
                // CREATE new patient
                success = patient.addPatient();
            } else {
                // UPDATE existing patient
                success = patient.updatePatient();
            }

            // 3. Feedback and Completion
            if (success) {
                String action = (patientId == -1 ? "Registered" : "Updated");
                JOptionPane.showMessageDialog(this, "Patient record " + action + " successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // You would typically add code here to refresh the patient table in MainDashboardGUI
                
                dispose(); // Close the dialog
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save the record. Check database logs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
    }
}