// Importing libraries to utilize in Supplier Panel.
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// SupplierPanel class which extends JPanel and is used to display all functionality within the card found in the Admin
// Dashboard
public class SupplierPanel extends JPanel {
    // Private variables used to ensure security when creating components.
    private JTextField sup_name, sup_contact_person, sup_phone, sup_email, sup_address, search;
    private JButton add_sup, update_sup, delete_sup, clear_sup, search_sup, refresh_sup;
    private DefaultTableModel model;
    private JTable supplierTable;

    // Private method used to load all the suppliers found within the Suppliers table into the table.
    private void loadSuppliers(DefaultTableModel model) {
        // Remove all existing rows before loading in a clean record of the database table.
        model.setRowCount(0);

        // SQL Query
        String sql = """
                SELECT supplier_id, name, contact_person, phone, email, address
                FROM suppliers
                """;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql);
                ResultSet rs = stm.executeQuery();
        ) {
            // While loop which runs through the results of the query.
            while (rs.next()) {
                // Creating columns for the table.
                Object[] row = {
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                };

                // Adding each column and its associated placeholders into a single record.
                model.addRow(row);
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            // Dialog box to display error.
            JOptionPane.showMessageDialog(
                    this,
                    "Error Loading Suppliers: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform the Create operation as part of CRUD.
    private void addSupplier() {
        // Assigning user input from the components to variables for processing.
        String name = sup_name.getText().trim();
        String contactPerson = sup_contact_person.getText().trim();
        String phone = sup_phone.getText().trim();
        String email = sup_email.getText().trim();
        String address = sup_address.getText().trim();

        // Input Validation on Input Fields
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Supplier Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_name.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (contactPerson.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the name of the Contact Person.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_contact_person.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a Phone Number.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_phone.requestFocus();

            return;
        }

        // Input Validation on Input Fields by ensuring that phone number has exactly 10 digits.
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a 10 digit Phone Number.",
                    "Invalid Phone Number",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_phone.requestFocus();

            return;
        }

        // Input Validation on Input Fields by ensuring that phone number starts with 0.
        if (!phone.startsWith("0")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Phone Number must start with 0.",
                    "Invalid Phone Number",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_phone.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Email Address.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_email.requestFocus();

            return;
        }

        // Input Validation on Input Fields by ensuring that the enail contains the @ character.
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Email Address.",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_email.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Address of the Company.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_address.requestFocus();

            return;
        }

        try {
            // SQL INSERT statement
            String sql = """
                    INSERT INTO suppliers
                    (name, contact_person, phone, email, address)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            // Try...Catch block.
            try (
                    // Establishing connection with the database and executing the sql query.
                    Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stm = conn.prepareStatement(sql);
            ) {
                // Setting Placeholder Values in the statement based on our input variables.
                stm.setString(1, name);
                stm.setString(2, contactPerson);
                stm.setString(3, phone);
                stm.setString(4, email);
                stm.setString(5, address);

                // Execute INSERT sql statement.
                stm.executeUpdate();

                // Success Message
                JOptionPane.showMessageDialog(
                        this,
                        "Supplier Added Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh JTable component by calling the loadSuppliers method.
                loadSuppliers(model);

                // Clear Form by calling the clearSupplier method.
                clearSupplier();
            }
            // SQL Error Handling
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Adding Supplier: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform the Update operation as part of CRUD.
    private void updateSupplier() {
        // Check if a supplier has been selected.
        int selectedRow = supplierTable.getSelectedRow();

        // If no row, has been selected, display a dialog box showcasing a message.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier from the table.",
                    "No Supplier Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get the Supplier ID from the selected row.
        int supplierId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        // Fetch all input entered by the user within the form components into their associated variables.
        String name = sup_name.getText().trim();
        String contactPerson = sup_contact_person.getText().trim();
        String phone = sup_phone.getText().trim();
        String email = sup_email.getText().trim();
        String address = sup_address.getText().trim();

        // Input Validation on Input Fields
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Supplier Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_name.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (contactPerson.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the name of the Contact Person.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_contact_person.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a Phone Number.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_phone.requestFocus();

            return;
        }

        // Input Validation on Input Fields by ensuring that phone number has exactly 10 digits.
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a 10 digit Phone Number.",
                    "Invalid Phone Number",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_phone.requestFocus();

            return;
        }

        // Input Validation on Input Fields by ensuring phone number field starts with 0.
        if (!phone.startsWith("0")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Phone Number must start with 0.",
                    "Invalid Phone Number",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_phone.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Email Address.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_email.requestFocus();

            return;
        }

        // Input Validation on Input Fields by ensuring that email contains the @ character.
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Email Address.",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_email.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Address of the Company.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            sup_address.requestFocus();

            return;
        }

        // SQL UPDATE statement
        String sql = """
                UPDATE suppliers
                SET name = ?,
                    contact_person = ?,
                    phone = ?,
                    email = ?,
                    address = ?
                WHERE supplier_id = ?
                """;

        // Try...Catch block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Setting Placeholder Values in the statement based on our input variables.
            stmt.setString(1, name);
            stmt.setString(2, contactPerson);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, address);

            // Set the ID of the supplier being updated.
            stmt.setInt(6, supplierId);

            // Execute UPDATE Statement
            int rowsUpdated = stmt.executeUpdate();

            // If statement which ensures that the update being performed on the query returns more than 0 rows.
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Supplier Updated Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh the table by calling the loadSuppliers method.
                loadSuppliers(model);

                // Clear the form by calling the clearSupplier method.
                clearSupplier();
                // Dialog which displays an error if the results of the query returns 0 rows.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Supplier could not be found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Updating Supplier: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform the DELETE Method as part of CRUD.
    private void deleteSupplier() {
        // Fetch the row number of the row which the user has selected.
        int selectedRow = supplierTable.getSelectedRow();

        // If statement that is executed if the user hasn't selected a row.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier from the table.",
                    "No Supplier Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get Supplier ID from the selected row.
        int supplierId = Integer.parseInt(supplierTable.getValueAt(selectedRow, 0).toString());

        // Fetch the name of the supplier from the selected row.
        String supplierName = model.getValueAt(selectedRow, 1).toString();

        // Integer variable used to display a confirmation dialog box which will be used to confirm deletion of a certain
        // supplier.
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + supplierName + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        // Stop if the user selects No
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        // SQL DELETE Query
        String sql = """
                    DELETE FROM suppliers
                    WHERE supplier_id = ?
                    """;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Set the ID of the supplier being deleted.
            stmt.setInt(1, supplierId);

            // Execute DELETE Statement
            int rowsUpdated = stmt.executeUpdate();

            // If statement which ensures that the update being performed on the query returns more than 0 rows.
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Supplier Deleted Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh the table by calling the loadSuppliers method.
                loadSuppliers(model);

                // Clear the form by calling the clearSupplier method.
                clearSupplier();
            // Dialog which displays an error if the results of the query returns 0 rows.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Supplier could not be found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Deleting Supplier: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to clear all components of any text entered by the user.
    private void clearSupplier() {
        // Resetting all components values to either null, 0, or no selection.
        sup_name.setText("");
        sup_contact_person.setText("");
        sup_phone.setText("");
        sup_email.setText("");
        sup_address.setText("");

        supplierTable.clearSelection();

        sup_name.requestFocus();
    }

    // Private method used to perform a search query on the Suppliers table.
    private void searchSuppliers(String searchString) {
        // Remove existing table rows and make the table empty.
        model.setRowCount(0);

        // SQL Search Query.
        String sql = """
                SELECT supplier_id, name, contact_person, phone, email, address
                FROM suppliers
                WHERE name LIKE ? OR contact_person LIKE ? OR phone LIKE ? OR email LIKE ?
        """;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Add search text to each LIKE condition
            String searchPattern =  "%" + searchString + "%";

            // Setting the placeholder values for each placeholder within the query.
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            // Executing the query and returning the result set.
            ResultSet rs = stmt.executeQuery();

            // While loop which runs through the results.
            while (rs.next()) {
                // Converts each result and its associated data into columns to be inserted into table.
                Object[] row = {
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                };

                // All columns are combined to create a single record.
                model.addRow(row);
            }

            // Check whether a result was found.
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No Suppliers found matching: " + searchString,
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Searching Suppliers: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Supplier Panel
    public SupplierPanel() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Setting out the layout of the SupplierPanel.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(customColor);
        this.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        // Heading Panel used to contain the title and description.
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setMaximumSize(new Dimension(750, 60));

        // Title Component.
        JLabel supplierTitle = new JLabel("Supplier Management");
        supplierTitle.setFont(new Font("Arial", Font.BOLD, 24));
        supplierTitle.setForeground(new Color(0, 204, 153));

        // Description component.
        JLabel supplierDesc = new JLabel("Create, Read, Update and Delete Suppliers");
        supplierDesc.setFont(new Font("Arial", Font.BOLD, 18));

        // Adding the two components and vertical spacing between each other.
        heading.add(supplierTitle);
        heading.add(Box.createVerticalStrut(7));
        heading.add(supplierDesc);

        // Adding the Heading Panel to the Supplier Panel.
        this.add(heading);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(10));

        // Horizontal Line Component.
        JSeparator sep1 = new JSeparator();
        sep1.setBackground(Color.GRAY);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the component to the Supplier Panel.
        this.add(sep1);

        // Supplier Label component.
        JLabel supplierLbl = new JLabel("Supplier Details");
        supplierLbl.setFont(new Font("Arial", Font.BOLD, 18));
        supplierLbl.setForeground(new Color(255, 98, 0));

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(15));

        // Adding the component to the Supplier Panel.
        this.add(supplierLbl);

        // Sup Form Panel which contains all form input components.
        JPanel supForm = new JPanel();
        supForm.setLayout(new GridLayout(3, 4, 20, 10));
        supForm.setBackground(customColor);
        supForm.setPreferredSize(new Dimension(1300, 180));
        supForm.setMaximumSize(new Dimension(1300, 180));
        supForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Creating the associated labels and components for each field.
        JLabel sup_nameLbl = new JLabel("Name: ");
        sup_nameLbl.setFont(new Font("Arial", Font.BOLD, 18));

        sup_name = new JTextField(20);
        sup_name.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel sup_contact_personLbl = new JLabel("Contact Person: ");
        sup_contact_personLbl.setFont(new Font("Arial", Font.BOLD, 18));

        sup_contact_person = new JTextField(20);
        sup_contact_person.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel sup_phoneLbl = new JLabel("Phone Number: ");
        sup_phoneLbl.setFont(new Font("Arial", Font.BOLD, 18));

        sup_phone = new JTextField(20);
        sup_phone.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel sup_emailLbl = new JLabel("Email Address: ");
        sup_emailLbl.setFont(new Font("Arial", Font.BOLD, 18));

        sup_email = new JTextField(20);
        sup_email.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel sup_addressLbl = new JLabel("Address: ");
        sup_addressLbl.setFont(new Font("Arial", Font.BOLD, 18));

        sup_address = new JTextField(20);
        sup_address.setFont(new Font("Arial", Font.BOLD, 18));

        // Adding all components and their associated labels to the Sup Form Panel.
        supForm.add(sup_nameLbl);
        supForm.add(sup_name);

        supForm.add(sup_contact_personLbl);
        supForm.add(sup_contact_person);

        supForm.add(sup_phoneLbl);
        supForm.add(sup_phone);

        supForm.add(sup_emailLbl);
        supForm.add(sup_email);

        supForm.add(sup_addressLbl);
        supForm.add(sup_address);

        supForm.add(new JLabel(""));
        supForm.add(new JLabel(""));

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(20));

        // Adding the Sup Form Panel to the Supplier Panel.
        this.add(supForm);

        // CRUD Panel used to add all CRUD Buttons
        JPanel crudPanelSup = new JPanel();
        crudPanelSup.setLayout(new FlowLayout(FlowLayout.LEFT));
        crudPanelSup.setBackground(customColor);
        crudPanelSup.setPreferredSize(new Dimension(700, 50));
        crudPanelSup.setMaximumSize(new Dimension(700, 50));
        crudPanelSup.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Creating the associated labels and components for each field.
        add_sup = new JButton("Add Supplier");
        add_sup.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the addSupplier method when clicked.
        add_sup.addActionListener(e -> {
            addSupplier();
        });

        update_sup = new JButton("Update Supplier");
        update_sup.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the updateSupplier method when clicked.
        update_sup.addActionListener(e -> {
            updateSupplier();
        });

        delete_sup = new JButton("Delete Supplier");
        delete_sup.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the deleteSupplier method when clicked.
        delete_sup.addActionListener(e -> {
            deleteSupplier();
        });

        clear_sup = new JButton("Clear Details");
        clear_sup.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the clearSupplier method when clicked.
        clear_sup.addActionListener(e -> {
            clearSupplier();
        });

        // Adding all buttons to the CRUD Panel.
        crudPanelSup.add(add_sup);
        crudPanelSup.add(update_sup);
        crudPanelSup.add(delete_sup);
        crudPanelSup.add(clear_sup);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(25));

        // Adding the CRUD Panel to the Supplier Panel.
        this.add(crudPanelSup);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(10));

        // Creating the Horizontal Line.
        JSeparator sep2 = new JSeparator();
        sep2.setBackground(Color.GRAY);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the separator to the Supplier Panel.
        this.add(sep2);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(15));

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(customColor);
        searchPanel.setPreferredSize(new Dimension(700, 70));
        searchPanel.setMaximumSize(new Dimension(700, 70));
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Creating the associated labels and components for each field.
        JLabel searchLbl = new JLabel("Search: ");
        searchLbl.setFont(new Font("Arial", Font.BOLD, 18));

        search = new JTextField(22);
        search.setFont(new Font("Arial", Font.BOLD, 18));

        search_sup = new JButton("Search");
        search_sup.setFont(new Font("Arial", Font.BOLD, 15));

        // Performing Search Operation when the button is clicked.
        search_sup.addActionListener(e -> {
            // Setting String variable to the input field text.
            String searchText = search.getText().trim();

            // If not found, display message.
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a search term.",
                        "Search",
                        JOptionPane.WARNING_MESSAGE
                );

                search.requestFocus();

                return;
            }
            // Call the searchSuppliers method if found.
            searchSuppliers(searchText);
        });

        refresh_sup = new JButton("Refresh");
        refresh_sup.setFont(new Font("Arial", Font.BOLD, 15));

        // Performing Refresh Operation when the button is clicked.
        refresh_sup.addActionListener(e -> {
            // Setting search field to null and calling the associated methods.
            search.setText("");

            loadSuppliers(model);
        });

        // Adding components to searchPanel.
        searchPanel.add(searchLbl);
        searchPanel.add(search);
        searchPanel.add(search_sup);
        searchPanel.add(refresh_sup);

        // Adding Search Panel to the Supplier Panel.
        this.add(searchPanel);

        // Creating Table Panel used to contain the table.
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.GREEN);
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // Creating columns for the table.
        String[] columns = {
                "ID",
                "Name",
                "Contact Person",
                "Phone Number",
                "Email Address",
                "Address"
        };

        // Creating the Table Model.
        model = new DefaultTableModel(columns, 0);

        // Initializing the table.
        supplierTable = new JTable(model);
        supplierTable.setBackground(new Color(255, 98, 0));
        supplierTable.setRowHeight(28);
        supplierTable.setFont(new Font("Arial", Font.BOLD, 14));
        supplierTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        supplierTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // When a row is selected, its information is loaded into the form.
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = supplierTable.getSelectedRow();

                // If row is selected, assign table row values to variables and assign these variables to the components.
                if (selectedRow != -1) {
                    String name = model.getValueAt(selectedRow, 1).toString();
                    String contactPerson = model.getValueAt(selectedRow, 2).toString();
                    String phoneNumber = model.getValueAt(selectedRow, 3).toString();
                    String email = model.getValueAt(selectedRow, 4).toString();
                    String address = model.getValueAt(selectedRow, 5).toString();

                    sup_name.setText(name);
                    sup_contact_person.setText(contactPerson);
                    sup_phone.setText(phoneNumber);
                    sup_email.setText(email);
                    sup_address.setText(address);
                }
            }
        });

        // Calling the loadSuppliers method based on the model.
        loadSuppliers(model);

        // Creating scroll pane for the table
        JScrollPane supScrollPane = new JScrollPane(supplierTable);

        // Adding scroll pane to the Table Panel.
        tablePanel.add(supScrollPane, BorderLayout.CENTER);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(25));

        // Adding the Table Panel to the Supplier Panel.
        this.add(tablePanel);
    }
}