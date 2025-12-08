import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
// Assume Patient.java and DBConnection.java are available

public class RegistrationDialog extends JDialog {

    private JTextField nameField, addressField, ageField, contactField;
    private JComboBox<String> sexComboBox;
    private JButton saveButton;
    private MainDashboardGUI parent; // Reference to the main window for refresh

    public RegistrationDialog(MainDashboardGUI parent) {
        // JDialog setup
        super(parent, "Register New Patient", true); // 'true' makes it a modal dialog
        this.parent = parent;
        setLayout(new BorderLayout());
        setSize(450, 350);
        
        // --- Input Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        // 1. Initialize Components
        nameField = new JTextField(20);
        addressField = new JTextField(20);
        ageField = new JTextField(5);
        contactField = new JTextField(15);
        
        sexComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        
        // 2. Add Labels and Fields
        formPanel.add(new JLabel("Full Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Address:")); formPanel.add(addressField);
        formPanel.add(new JLabel("Age:")); formPanel.add(ageField);
        formPanel.add(new JLabel("Sex:")); formPanel.add(sexComboBox);
        formPanel.add(new JLabel("Contact No.:")); formPanel.add(contactField);

        // --- Save Button Panel ---
        saveButton = new JButton("Save Record");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ----------------------------------------------------
        // 3. Event Handling: Save Button Action
        // ----------------------------------------------------
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePatientRecord();
            }
        });
        
        setLocationRelativeTo(parent); // Center relative to main window
    }

    private void savePatientRecord() {
        String fullName = nameField.getText().trim();
        String address = addressField.getText().trim();
        String sex = (String) sexComboBox.getSelectedItem();
        String contact = contactField.getText().trim();
        int age = -1; // Default for validation

        // --- Input Validation (IF-ELSE and Try-Catch) ---
        if (fullName.isEmpty() || address.isEmpty() || ageField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (Name, Address, Age).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Object Instantiation (Constructor) and Database Call ---
        // 1. Create a Patient object
        Patient newPatient = new Patient(fullName, age, sex, address, contact);
        
        // 2. Call the CRUD Create method
        if (newPatient.addPatient()) {
            JOptionPane.showMessageDialog(this, "Patient " + fullName + " successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // 3. Refresh the JTable on the Main Dashboard
            // parent.refreshPatientTable(); // Assume MainDashboardGUI has this method
            
            this.dispose(); // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save record. Check database connection or logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}