import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class EmployeeSearchFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDatabase;
    private JList<String> lstDepartment;
    private DefaultListModel<String> department = new DefaultListModel<>();
    private JList<String> lstProject;
    private DefaultListModel<String> project = new DefaultListModel<>();
    private JTextArea textAreaEmployee;
    private JCheckBox chckbxNotDept;
    private JCheckBox chckbxNotProject;

    private EmployeeDAO employeeDAO = new EmployeeDAO();

    public EmployeeSearchFrame() {
        setTitle("Employee Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 360);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Database:");
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel.setBounds(21, 23, 59, 14);
        contentPane.add(lblNewLabel);

        txtDatabase = new JTextField();
        txtDatabase.setBounds(90, 20, 193, 20);
        contentPane.add(txtDatabase);
        txtDatabase.setColumns(10);

        JButton btnDBFill = new JButton("Fill");
        btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnDBFill.setBounds(307, 19, 68, 23);
        contentPane.add(btnDBFill);

        JLabel lblDepartment = new JLabel("Department");
        lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDepartment.setBounds(52, 63, 89, 14);
        contentPane.add(lblDepartment);

        JLabel lblProject = new JLabel("Project");
        lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblProject.setBounds(255, 63, 47, 14);
        contentPane.add(lblProject);

        // ScrollPane tracking wrapper housing Department list elements
        lstDepartment = new JList<>(department);
        lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
        JScrollPane scrollDept = new JScrollPane(lstDepartment);
        scrollDept.setBounds(36, 84, 172, 45);
        contentPane.add(scrollDept);

        // ScrollPane tracking wrapper housing Project list elements
        lstProject = new JList<>(project);
        lstProject.setFont(new Font("Tahoma", Font.PLAIN, 12));
        JScrollPane scrollProject = new JScrollPane(lstProject);
        scrollProject.setBounds(225, 84, 150, 45);
        contentPane.add(scrollProject);

        chckbxNotDept = new JCheckBox("Not");
        chckbxNotDept.setBounds(71, 133, 59, 23);
        contentPane.add(chckbxNotDept);

        chckbxNotProject = new JCheckBox("Not");
        chckbxNotProject.setBounds(270, 133, 59, 23);
        contentPane.add(chckbxNotProject);

        JLabel lblEmployee = new JLabel("Employee");
        lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmployee.setBounds(52, 179, 89, 14);
        contentPane.add(lblEmployee);

        // ScrollPane tracking wrapper housing Employee JTextArea display
        textAreaEmployee = new JTextArea();
        textAreaEmployee.setEditable(false);
        JScrollPane scrollEmployee = new JScrollPane(textAreaEmployee);
        scrollEmployee.setBounds(36, 197, 339, 68);
        contentPane.add(scrollEmployee);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(80, 276, 89, 23);
        contentPane.add(btnSearch);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(236, 276, 89, 23);
        contentPane.add(btnClear);

        /* ==========================================
         *             INTERACTION TRIGGERS
         * ========================================== */

        // Note 5: Validate and load tables from database target
        btnDBFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // FIX DUPLICATES: Always drop existing model records before re-filling
                department.clear();
                project.clear();
                clearSelectionsAndData();
                
                String dbName = txtDatabase.getText().trim();
                
                // INPUT VALIDATION: Intercept blank input requests gracefully
                if (dbName.isEmpty()) {
                    JOptionPane.showMessageDialog(EmployeeSearchFrame.this, 
                        "Please enter a valid database schema name.", 
                        "Missing Input Data", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    DatabaseConnection.connect(dbName);
                    
                    // Hydrate UI list view states dynamically
                    for (String deptName : employeeDAO.getAllDepartments()) {
                        department.addElement(deptName);
                    }
                    for (String projName : employeeDAO.getAllProjects()) {
                        project.addElement(projName);
                    }

                } catch (SQLException ex) {
                    // Note 5 Error condition popup block handler
                    JOptionPane.showMessageDialog(EmployeeSearchFrame.this, 
                        "The database could not be opened.", 
                        "Database Failure", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Note 3 & 7: Execute dynamic lookup operations
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // INPUT VALIDATION: Ensure an active database session exists
                if (DatabaseConnection.getConnection() == null) {
                    JOptionPane.showMessageDialog(EmployeeSearchFrame.this, 
                        "No active database session. Enter a name and click 'Fill' first.", 
                        "Connection Required", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                textAreaEmployee.setText("");
                List<String> selDepts = lstDepartment.getSelectedValuesList();
                List<String> selProjs = lstProject.getSelectedValuesList();

                // INPUT VALIDATION: Ensure at least one selection choice is highlighted
                if (selDepts.isEmpty() && selProjs.isEmpty()) {
                    JOptionPane.showMessageDialog(EmployeeSearchFrame.this, 
                        "Please select at least one Department or Project from the lists to run a search.", 
                        "No Criteria Selected", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                try {
                    List<String> matches = employeeDAO.searchEmployees(
                        selDepts, selProjs, chckbxNotDept.isSelected(), chckbxNotProject.isSelected()
                    );

                    if (matches.isEmpty()) {
                        textAreaEmployee.setText("No matching records found inside the database.");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (String emp : matches) {
                        sb.append(emp).append("\n");
                    }
                    textAreaEmployee.setText(sb.toString());

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(EmployeeSearchFrame.this, 
                        "Query Execution Failed: " + ex.getMessage(), 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Note 6: Clear form input widgets
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearSelectionsAndData();
            }
        });
    }

    private void clearSelectionsAndData() {
        textAreaEmployee.setText("");
        lstDepartment.clearSelection();
        lstProject.clearSelection();
        chckbxNotDept.setSelected(false);
        chckbxNotProject.setSelected(false);
    }
}
