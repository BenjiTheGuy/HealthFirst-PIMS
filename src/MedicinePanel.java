// Importing libraries to utilize in Medicine Panel.
import database.DatabaseConnection;

import java.util.Date;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// MedicinePanel class which extends JPanel and is used to display all functionality within the card found in the Admin
// Dashboard.
public class MedicinePanel extends JPanel {
    // Private variables used to ensure security when creating components.
    private JTextField med_name, med_price, med_company, search;
    private JSpinner med_reorder, med_quantity, med_date;
    private JButton add_med, update_med, delete_med, clear_med, search_med, refresh_med;
    private JComboBox<String> med_type, med_supplier;
    private DefaultTableModel model;
    private JTable medicineTable;

    // Private method used to load list of suppliers into the combo box for editing.
    private void loadSuppliers() {
        // SQL statement
        String sql = "SELECT supplier_id, name FROM suppliers";

        // Try...Catch Block
        try (
            // Establishing connection with the database and executing the sql query.
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
        ) {
            // Removing all items found in the combo box.
            med_supplier.removeAllItems();

            // While loop which runs through the results of the query.
            while (rs.next()) {
                // Initializing variables with the placeholders used in the results set.
                int supp_id = rs.getInt("supplier_id");
                String supp_name = rs.getString("name");

                // Adding both variables as a single string to the combo box.
                med_supplier.addItem(supp_id + " - " + supp_name);
            };
            // SQL error.
        } catch (SQLException e) {
            // Dialog box displaying error.
            JOptionPane.showMessageDialog(
                    this,
                    "Error Loading Suppliers: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to load all the medicines found within the Medicines table into the table.
    private void loadMedicines(DefaultTableModel model) {
        // Remove all existing rows before loading in a clean record of the database table.
        model.setRowCount(0);

        // SQL Query
        String sql = """
                SELECT medicine_id, name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date,
                supplier_id FROM medicines
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
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("company"),
                        rs.getString("medicine_type"),
                        rs.getBigDecimal("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("reorder_level"),
                        rs.getDate("expiry_date"),
                        rs.getInt("supplier_id")
                };

                // Adding each column and its associated placeholders into a single record.
                model.addRow(row);
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            // Dialog box to display error.
            JOptionPane.showMessageDialog(
                    this,
                    "Error Loading Medicines: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform the Create operation as part of CRUD.
    private void addMedicine() {
        // Assigning user input from the components to variables for processing.
        String name = med_name.getText().trim();
        String company = med_company.getText().trim();
        String type = (String) med_type.getSelectedItem();
        String priceText = med_price.getText().trim();
        int reorderLevel = (Integer) med_reorder.getValue();
        int quantity = (Integer) med_quantity.getValue();
        String selectedSupplier = (String) med_supplier.getSelectedItem();
        Date selectedDate = (Date) med_date.getValue();

        // Input Validation on Input Fields
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Medicine Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_name.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (company.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Company Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_company.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (type == null || type.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a Medicine Type.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_type.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Medicine Price.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_price.requestFocus();

            return;
        }

        // Declaring a price variable to be used in try...catch block.
        double price;

        try {
            // Convert Price from String to Double.
            price = Double.parseDouble(priceText);

            // Input Validation on Input Fields
            if (price < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Price cannot be negative.",
                        "Invalid Price",
                        JOptionPane.WARNING_MESSAGE
                );
                med_price.requestFocus();

                return;
            }
            // Error Handling if the Price variable is in the incorrect format.
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid numerical price.",
                    "Invalid Price",
                    JOptionPane.WARNING_MESSAGE
            );
            med_price.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (reorderLevel < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Reorder level cannot be negative.",
                    "Invalid Reorder Level",
                    JOptionPane.WARNING_MESSAGE
            );
            med_reorder.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (quantity < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Quantity cannot be negative.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );
            med_quantity.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (selectedSupplier == null || selectedSupplier.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_supplier.requestFocus();

            return;
        }

        // Declaring a supplierID variable to be used in input validation.
        int supplierId;

        // Try...Catch block.
        try {
            // Extracting the supplier ID from splitting the combo box string.
            supplierId = Integer.parseInt(selectedSupplier.split(" - ")[0]);
            // Error Handling if the number isn't extracted properly.
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Supplier Selection",
                    "Invalid Supplier",
                    JOptionPane.WARNING_MESSAGE
            );

            med_supplier.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an expiry date.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_date.requestFocus();

            return;
        }

        // Convert Java Date into SQL Date.
        java.sql.Date expiryDate = new java.sql.Date(selectedDate.getTime());

        try {
            // SQL INSERT statement
            String sql = """
                    INSERT INTO medicines
                    (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            // Try...Catch block.
            try (
                    // Establishing connection with the database and executing the sql query.
                    Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stm = conn.prepareStatement(sql);
            ) {
                // Setting Placeholder Values in the statement based on our input variables.
                stm.setString(1, name);
                stm.setString(2, company);
                stm.setString(3, type);
                stm.setDouble(4, price);
                stm.setInt(5, quantity);
                stm.setInt(6, reorderLevel);
                stm.setDate(7, expiryDate);
                stm.setInt(8, supplierId);

                // Execute INSERT sql statement.
                stm.executeUpdate();

                // Success Message
                JOptionPane.showMessageDialog(
                        this,
                        "Medicine Added Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh JTable component by calling the loadMedicines method.
                loadMedicines(model);

                // Clear Form by calling the clearMedicine method.
                clearMedicine();
            // Input Error within the form.
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please Enter a Valid Price.",
                        "Invalid Price",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        // SQL Error Handling
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Adding Medicine: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform the Update operation as part of CRUD.
    private void updateMedicine() {
        // Check if a medicine has been selected.
        int selectedRow = medicineTable.getSelectedRow();

        // If no row, has been selected, display a dialog box showcasing a message.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine from the table.",
                    "No Medicine Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get the Medicine ID from the selected row.
        int medicineId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        // Fetch all input entered by the user within the form components into their associated variables.
        String name = med_name.getText().trim();
        String company = med_company.getText().trim();
        String type = (String) med_type.getSelectedItem();
        String priceText = med_price.getText().trim();
        int reorderLevel = (Integer) med_reorder.getValue();
        int quantity = (Integer) med_quantity.getValue();
        String selectedSupplier = (String) med_supplier.getSelectedItem();
        Date selectedDate = (Date) med_date.getValue();

        // Input Validation on Input Fields
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Medicine Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_name.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (company.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Company Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_company.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (type == null || type.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a Medicine Type.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_type.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Medicine Price.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_price.requestFocus();

            return;
        }

        // Declaring a price variable to be used in try...catch block.
        double price;

        try {
            // Convert Price from String to Double.
            price = Double.parseDouble(priceText);

            // Input Validation on Input Fields
            if (price < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Price cannot be negative.",
                        "Invalid Price",
                        JOptionPane.WARNING_MESSAGE
                );
                med_price.requestFocus();

                return;
            }
            // Error Handling if the Price variable is in the incorrect format.
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid numerical price.",
                    "Invalid Price",
                    JOptionPane.WARNING_MESSAGE
            );
            med_price.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (reorderLevel < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Reorder level cannot be negative.",
                    "Invalid Reorder Level",
                    JOptionPane.WARNING_MESSAGE
            );
            med_reorder.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (quantity < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Quantity cannot be negative.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );
            med_quantity.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (selectedSupplier == null || selectedSupplier.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_supplier.requestFocus();

            return;
        }

        // Declaring a supplierID variable to be used in input validation.
        int supplierId;

        // Try...Catch Block
        try {
            // Extracting the supplier ID from splitting the combo box string.
            supplierId = Integer.parseInt(selectedSupplier.split(" - ")[0]);
        // Error Handling if the number isn't extracted properly.
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Supplier Selection",
                    "Invalid Supplier",
                    JOptionPane.WARNING_MESSAGE
            );

            med_supplier.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an expiry date.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            med_date.requestFocus();

            return;
        }

        // Convert Java Date into SQL Date.
        java.sql.Date expiryDate = new java.sql.Date(selectedDate.getTime());

        // SQL UPDATE statement
        String sql = """
                UPDATE medicines
                SET name = ?,
                    company = ?,
                    medicine_type = ?,
                    price = ?,
                    quantity_in_stock = ?,
                    reorder_level = ?,
                    expiry_date = ?,
                    supplier_id = ?
                WHERE medicine_id = ?
                """;

        // Try...Catch block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Setting Placeholder Values in the statement based on our input variables.
            stmt.setString(1, name);
            stmt.setString(2, company);
            stmt.setString(3, type);
            stmt.setDouble(4, price);
            stmt.setInt(5, quantity);
            stmt.setInt(6, reorderLevel);
            stmt.setDate(7, expiryDate);
            stmt.setInt(8, supplierId);

            // Set the ID of the medicine being updated.
            stmt.setInt(9, medicineId);

            // Execute UPDATE Statement
            int rowsUpdated = stmt.executeUpdate();

            // If statement which ensures that the update being performed on the query returns more than 0 rows.
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Medicine Updated Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh the table by calling the loadMedicines method.
                loadMedicines(model);

                // Clear the form by calling the clearMedicine method.
                clearMedicine();
            // Dialog which displays an error if the results of the query returns 0 rows.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Medicine could not be found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Updating Medicine: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform the DELETE Method as part of CRUD.
    private void deleteMedicine() {
        // Fetch the row number of the row which the user has selected.
        int selectedRow = medicineTable.getSelectedRow();

        // If statement that is executed if the user hasn't selected a row.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine from the table.",
                    "No Medicine Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get Medicine ID from the selected row.
        int medicineId = Integer.parseInt(medicineTable.getValueAt(selectedRow, 0).toString());

        // Fetch the name of the medicine from the selected row.
        String medicineName = model.getValueAt(selectedRow, 1).toString();

        // Integer variable used to display a confirmation dialog box which will be used to confirm deletion of a certain
        // medicine.
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + medicineName + "\"?",
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
                    DELETE FROM medicines
                    WHERE medicine_id = ?
                    """;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Set the ID of the medicine being deleted.
            stmt.setInt(1, medicineId);

            // Execute DELETE Statement
            int rowsUpdated = stmt.executeUpdate();

            // If statement which ensures that the update being performed on the query returns more than 0 rows.
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Medicine Deleted Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh the table by calling the loadMedicines method.
                loadMedicines(model);

                // Clear the form by calling the clearMedicine method.
                clearMedicine();
            // Dialog which displays an error if the results of the query returns 0 rows.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Medicine could not be found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Deleting Medicine: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to clear all components of any text entered by the user.
    private void clearMedicine() {
        // Resetting all components values to either null, 0, the first option found within combo boxes or no selection.
        med_name.setText("");

        if (med_type.getItemCount() > 0) {
            med_type.setSelectedIndex(0);
        };

        med_price.setText("");
        med_reorder.setValue(0);
        med_company.setText("");

        if (med_supplier.getItemCount() > 0) {
            med_supplier.setSelectedIndex(0);
        };

        med_quantity.setValue(0);
        med_date.setValue(new java.util.Date());

        medicineTable.clearSelection();

        med_name.requestFocus();
    }

    // Private method used to perform a search query on the Medicines table.
    private void searchMedicines(String searchString) {
        // Remove existing table rows and make the table empty.
        model.setRowCount(0);

        // SQL Search Query.
        String sql = """
                SELECT medicine_id, name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date,
                supplier_id
                FROM medicines
                WHERE name LIKE ? OR company LIKE ? OR medicine_type LIKE ?
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

            // Executing the query and returning the result set.
            ResultSet rs = stmt.executeQuery();

            // While loop which runs through the results.
            while (rs.next()) {
                // Converts each result and its associated data into columns to be inserted into table.
                Object[] row = {
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("company"),
                        rs.getString("medicine_type"),
                        rs.getDouble("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("reorder_level"),
                        rs.getDate("expiry_date"),
                        rs.getInt("supplier_id"),
                };

                // All columns are combined to create a single record.
                model.addRow(row);
            }

            // Check whether a result was found.
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No Medicines found matching: " + searchString,
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Searching Medicines: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Medicine Panel
    public MedicinePanel() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Setting out the layout of the MedicinePanel.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(customColor);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Heading Panel used to contain the title and description.
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setMaximumSize(new Dimension(700, 60));

        // Title Component.
        JLabel medicineTitle = new JLabel("Medicine Management");
        medicineTitle.setFont(new Font("Arial", Font.BOLD, 24));
        medicineTitle.setForeground(new Color(0, 204, 153));

        // Description component.
        JLabel medicineDesc = new JLabel("Create, Read, Update and Delete Medicines");
        medicineDesc.setFont(new Font("Arial", Font.BOLD, 18));

        // Adding the two components and vertical spacing between each other.
        heading.add(medicineTitle);
        heading.add(Box.createVerticalStrut(7));
        heading.add(medicineDesc);

        // Adding the Heading Panel to the Medicine Panel.
        this.add(heading);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(10));

        // Horizontal Line Component.
        JSeparator sep1 = new JSeparator();
        sep1.setBackground(Color.GRAY);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the component to the Medicine Panel.
        this.add(sep1);

        // Medicine Label component.
        JLabel medicineLbl = new JLabel("Medicine Details");
        medicineLbl.setFont(new Font("Arial", Font.BOLD, 18));
        medicineLbl.setForeground(Color.BLUE);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(15));

        // Adding the component to the Medicine Panel.
        this.add(medicineLbl);

        // Med Form Panel which contains all form input components.
        JPanel medForm = new JPanel();
        medForm.setLayout(new GridLayout(4, 4, 20, 10));
        medForm.setBackground(customColor);
        medForm.setPreferredSize(new Dimension(1300, 180));
        medForm.setMaximumSize(new Dimension(1300, 180));
        medForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Creating the associated labels and components for each field.
        JLabel med_nameLbl = new JLabel("Name: ");
        med_nameLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_name = new JTextField(20);
        med_name.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel med_typeLbl = new JLabel("Medicine Type: ");
        med_typeLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_type = new JComboBox<>(new String[]{
                "Tablet",
                "Capsule",
                "Syrup",
                "Injection",
                "Cream"
        });
        med_type.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel med_priceLbl = new JLabel("Price: ");
        med_priceLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_price = new JTextField(20);
        med_price.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel med_reorderLbl = new JLabel("Reorder Level: ");
        med_reorderLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_reorder = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        med_reorder.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel med_companyLbl = new JLabel("Company: ");
        med_companyLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_company = new JTextField(20);
        med_company.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel med_supplierLbl = new JLabel("Supplier: ");
        med_supplierLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_supplier = new JComboBox<>();
        med_supplier.setFont(new Font("Arial", Font.BOLD, 18));

        // Calling the loadSuppliers method to fill the med_supplier combo box.
        loadSuppliers();

        JLabel med_quantityLbl = new JLabel("Quantity: ");
        med_quantityLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_quantity = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        med_quantity.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel med_expiryLbl = new JLabel("Expiry Date: ");
        med_expiryLbl.setFont(new Font("Arial", Font.BOLD, 18));

        med_date = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dtEditor = new JSpinner.DateEditor(med_date, "yyyy-MM-dd");
        med_date.setEditor(dtEditor);
        med_date.setFont(new Font("Arial", Font.BOLD, 18));

        // Adding all components and their associated labels to the Med Form Panel.
        medForm.add(med_nameLbl);
        medForm.add(med_name);

        medForm.add(med_typeLbl);
        medForm.add(med_type);

        medForm.add(med_priceLbl);
        medForm.add(med_price);

        medForm.add(med_reorderLbl);
        medForm.add(med_reorder);

        medForm.add(med_companyLbl);
        medForm.add(med_company);

        medForm.add(med_supplierLbl);
        medForm.add(med_supplier);

        medForm.add(med_quantityLbl);
        medForm.add(med_quantity);

        medForm.add(med_expiryLbl);
        medForm.add(med_date);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(20));

        // Adding the Med Form Panel to the Medicine Panel.
        this.add(medForm);

        // CRUD Panel used to add all CRUD Buttons
        JPanel crudPanelMed = new JPanel();
        crudPanelMed.setLayout(new FlowLayout(FlowLayout.LEFT));
        crudPanelMed.setBackground(customColor);
        crudPanelMed.setPreferredSize(new Dimension(700, 50));
        crudPanelMed.setMaximumSize(new Dimension(700, 50));
        crudPanelMed.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Creating the associated labels and components for each field.
        add_med = new JButton("Add Medicine");
        add_med.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the addMedicine method when clicked.
        add_med.addActionListener(e -> {
            addMedicine();
        });

        update_med = new JButton("Update Medicine");
        update_med.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the updateMedicine method when clicked.
        update_med.addActionListener(e -> {
            updateMedicine();
        });

        delete_med = new JButton("Delete Medicine");
        delete_med.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the deleteMedicine method when clicked.
        delete_med.addActionListener(e -> {
            deleteMedicine();
        });

        clear_med = new JButton("Clear Details");
        clear_med.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the clearMedicine and loadSuppliers method when clicked.
        clear_med.addActionListener(e -> {
            clearMedicine();
            loadSuppliers();
        });

        // Adding all buttons to the CRUD Panel.
        crudPanelMed.add(add_med);
        crudPanelMed.add(update_med);
        crudPanelMed.add(delete_med);
        crudPanelMed.add(clear_med);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(25));

        // Adding the CRUD Panel to the Medicine Panel.
        this.add(crudPanelMed);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(10));

        // Creating the Horizontal Line.
        JSeparator sep2 = new JSeparator();
        sep2.setBackground(Color.GRAY);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the separator to the Medicine Panel.
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

        search_med = new JButton("Search");
        search_med.setFont(new Font("Arial", Font.BOLD, 15));

        // Performing Search Operation when the button is clicked.
        search_med.addActionListener(e -> {
            // Setting String variable to the input field text.
            String searchText = search.getText().trim();

            // If not found, display message.
            if (searchText.equals("")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a search term.",
                        "Search",
                        JOptionPane.WARNING_MESSAGE
                );

                search.requestFocus();

                return;
            }

            // Call the searchMedicines method if found.
            searchMedicines(searchText);
        });

        refresh_med = new JButton("Refresh");
        refresh_med.setFont(new Font("Arial", Font.BOLD, 15));

        // Performing Refresh Operation when the button is clicked.
        refresh_med.addActionListener(e -> {
            // Setting search field to null and calling the associated methods.
            search.setText("");

            loadMedicines(model);
            loadSuppliers();
        });

        // Adding components to searchPanel.
        searchPanel.add(searchLbl);
        searchPanel.add(search);
        searchPanel.add(search_med);
        searchPanel.add(refresh_med);

        // Adding Search Panel to the Medicine Panel.
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
                "Company",
                "Type",
                "Price",
                "Stock",
                "Reorder Level",
                "Expiry Date",
                "Supplier ID"
        };

        // Creating the Table Model.
        model = new DefaultTableModel(columns, 0);

        // Initializing the table.
        medicineTable = new JTable(model);
        medicineTable.setBackground(Color.BLUE);
        medicineTable.setRowHeight(28);
        medicineTable.setFont(new Font("Arial", Font.BOLD, 14));
        medicineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        medicineTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // When a row is selected, its information is loaded into the form.
        medicineTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = medicineTable.getSelectedRow();

                // If row is selected, assign table row values to variables and assign these variables to the components.
                if (selectedRow != -1) {
                    String name = model.getValueAt(selectedRow, 1).toString();
                    String company = model.getValueAt(selectedRow, 2).toString();
                    String type = model.getValueAt(selectedRow, 3).toString();
                    String price = model.getValueAt(selectedRow, 4).toString();
                    int quantity = Integer.parseInt(model.getValueAt(selectedRow, 5).toString());
                    int reorderLevel = Integer.parseInt(model.getValueAt(selectedRow, 6).toString());
                    Date expiryDate = (Date) model.getValueAt(selectedRow, 7);
                    int supplierId = Integer.parseInt(model.getValueAt(selectedRow, 8).toString());

                    med_name.setText(name);
                    med_company.setText(company);
                    med_type.setSelectedItem(type);
                    med_price.setText(price);
                    med_quantity.setValue(quantity);
                    med_reorder.setValue(reorderLevel);
                    med_date.setValue(expiryDate);

                    // Fetch the Index of the supplier found within the combo box.
                    for (int i = 0; i < med_supplier.getItemCount(); i++) {
                        String supplierItem = med_supplier.getItemAt(i);

                        if (supplierItem.startsWith(supplierId + " - ")) {
                            med_supplier.setSelectedIndex(i);

                            break;
                        }
                    }
                }
            }
        });

        // Calling the loadMedicines method based on the model.
        loadMedicines(model);

        // Creating scroll pane for the table
        JScrollPane medScrollPane = new JScrollPane(medicineTable);

        // Adding scroll pane to the Table Panel.
        tablePanel.add(medScrollPane, BorderLayout.CENTER);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(25));

        // Adding the Table Panel to the Medicine Panel.
        this.add(tablePanel);
    }
}