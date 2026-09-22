// Importing libraries to be used in the POS Panel file.
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// POS Panel which extends JPanel. This ensures that the Panel can be added to the Cashier Dashboard with ease.
public class POSPanel extends JPanel {
    // Private variables used to ensure security of the components and variables created and utilized during execution.
    private JTextField search;
    private JButton searchBtn, addToCartBtn, clearSaleBtn, completeSaleBtn;
    private JTable medicineTable, cartTable;
    private DefaultTableModel medicineTableModel, cartTableModel;
    private JSpinner quantitySpinner;
    private JLabel totalLbl;

    private int cashierId;

    // Private Method used to search for a specific medicine before selection.
    private void searchMedicine() {
        // Declaring variables to be used.
        String name, company;
        int medicineId, stock;
        double price;

        // Initializing search variable based on input received in the search field.
        String searchText = search.getText().trim();

        // If statement that is executed if the search variable is empty.
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a medicine name or company.",
                    "Search",
                    JOptionPane.WARNING_MESSAGE
            );
            search.requestFocus();

            return;
        }

        // Setting the Medicine Table Model to null. Removing all existing rows.
        medicineTableModel.setRowCount(0);

        // SQL Query
        String sql = """
                SELECT medicine_id, name, company, price, quantity_in_stock
                FROM medicines
                WHERE name LIKE ? OR company LIKE ?
                ORDER BY name
                """;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Initialize the searchValue variable.
            String searchValue = "%" + searchText + "%";

            // Assign the searchValue variable as the placeholder values in the prepared Statement.
            stmt.setString(1, searchValue);
            stmt.setString(2, searchValue);

            // Execute the SQL Query.
            ResultSet rs = stmt.executeQuery();

            // Initialize the result counter with a value of 0.
            int resultCount = 0;

            // While loop which runs through the results.
            while (rs.next()) {
                // Initializing variables with the associated placeholder values obtained from the result set.
                medicineId = rs.getInt("medicine_id");
                name = rs.getString("name");
                company = rs.getString("company");
                price = rs.getDouble("price");
                stock = rs.getInt("quantity_in_stock");

                // Adding the variables into the table.
                medicineTableModel.addRow(new Object[]{
                        medicineId,
                        name,
                        company,
                        price,
                        stock
                });

                // Increase the result counter.
                resultCount++;
            }

            // If statement that is executed if the result counter is still 0.
            if (resultCount == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No Medicines Found.",
                        "Search",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            // SQL Error Handling
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Searching for Medicine:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private Method used to add items to a cart.
    private void addToCart() {
        // Initializing the variable based on the index of the selected row.
        int selectedRow = medicineTable.getSelectedRow();

        // Check if a medicine was selected
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
        int medicineId = (int) medicineTableModel.getValueAt(selectedRow, 0);

        // Fetch the Medicine Name from the selected Row.
        String medicineName = medicineTableModel.getValueAt(selectedRow, 1).toString();

        // Fetch the Price from the selected row.
        double price = Double.parseDouble(
                medicineTableModel
                        .getValueAt(selectedRow, 3)
                        .toString()
                        .replace("R", "")
                        .replace(",", ".")
        );

        // Fetch the Stock from the selected row.
        int stock = (int) medicineTableModel.getValueAt(selectedRow, 4);

        // Fetch the Quantity from the spinner
        int quantity = (Integer) quantitySpinner.getValue();

        // Initialize the cart row value.
        int cartRow = -1;

        // For loop that will fetch the medicine ID from each row.
        for (int row = 0; row < cartTableModel.getRowCount(); row++) {
            int existingMedicineId = (int) cartTableModel.getValueAt(row, 0);

            // If found, set the cart row value to the initial cart row value.
            if (existingMedicineId == medicineId) {
                cartRow = row;

                break;
            }
        }

        // Medicine Already in Cart
        if (cartRow != -1) {
            // Fetch the quantity value found in the Cart Table.
            int existingQuantity = (int) cartTableModel.getValueAt(cartRow, 2);

            // Set the new Quantity value based on the quantity value obtained from the spinner and add it to the
            // existing one in the Cart Table.
            int newQuantity = existingQuantity + quantity;

            // Check against available stock
            if (newQuantity > stock) {
                JOptionPane.showMessageDialog(
                        this,
                        "Only " + stock + " units of " + medicineName + " are available.",
                        "Insufficient Stock",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Determining subtotal.
            double newSubtotal = price * newQuantity;

            // Setting the Cart Table Models values.
            cartTableModel.setValueAt(newQuantity, cartRow,2);
            cartTableModel.setValueAt(String.format("R%.2f", newSubtotal), cartRow, 4);
        } else {
            // If the quantity value is greater than stock available, display the following dialog box.
            if (quantity > stock) {
                JOptionPane.showMessageDialog(
                        this,
                        "Quantity is greater than stock. Only " + stock + " units of " + medicineName +
                                " are available.",
                        "Insufficient Stock",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Calculating Subtotal
            double subtotal = price * quantity;

            // Add Medicine to Cart
            cartTableModel.addRow(new Object[]{
                    medicineId,
                    medicineName,
                    quantity,
                    String.format("R%.2f", price),
                    String.format("R%.2f", subtotal)
            });
        }

        // Update Displayed Stock
        updateDisplayedStock(medicineId, quantity);

        // Update total
        updateTotal();

        // Reset Quantity
        quantitySpinner.setValue(1);
    }

    // Private method used to update the Medicine Table stock.
    private void updateDisplayedStock(int medicineId, int quantity) {
        // For loop which will run through all Medicine Table rows to extract and compare the medicine Id to the one
        // found in each row.
        for (int row = 0; row < medicineTableModel.getRowCount(); row++) {
            int currentMedicineId = (int) medicineTableModel.getValueAt(row, 0);

            // If a match is found, the current stock value found in current row is updated with a new value.
            if (currentMedicineId == medicineId) {
                int currentStock = (int) medicineTableModel.getValueAt(row, 4);

                int newStock = currentStock - quantity;

                medicineTableModel.setValueAt(newStock, row, 4);

                break;
            }
        }
    }

    // Private method used to update the Total Label
    private void updateTotal() {
        double total = 0.0;

        // For loop which will run through each item in the Cart Table and accumulate all subtotals.
        for (int row = 0; row < cartTableModel.getRowCount(); row++) {
            double subtotal = Double.parseDouble(
                    cartTableModel
                            .getValueAt(row, 4)
                            .toString()
                            .replace("R", "")
                            .replace(",", ".")
            );

            total += subtotal;
        }

        // Display the final total.
        totalLbl.setText(String.format("Total: R%.2f", total));
    }

    // Private method used to clear the current sale.
    private void clearSale() {
        // Check if the cart is already empty.
        if (cartTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "There is no sale to clear.",
                    "Clear Sale",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Confirmation dialog used to determine user's choice.
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the current sale?",
                "Clear Sale",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // If the cashier selects the No option, cancel the method.
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Clear the cart
        cartTableModel.setRowCount(0);

        // Reset total
        totalLbl.setText("Total: R0.00");

        // Reset quantity
        quantitySpinner.setValue(1);

        // Refresh Medicine Search
        if (!search.getText().trim().isEmpty()) {
            searchMedicine();
        }
    }

    // Private method used to complete the current sale.
    private void completeSale() {
        // Check if there are any items in the current sale.
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "There are no items in the current sale.",
                    "Complete Sale",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Confirmation dialog used to determine user's choice.
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to complete this sale?",
                "Complete Sale",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        // If the cashier selects the No option, cancel the method.
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Variable used to store the database connection
        Connection conn = null;

        // Try...Catch Block
        try {
            // Establish a connection to the database.
            conn = DatabaseConnection.getConnection();

            // Starting transaction. All database changes are committed together.
            conn.setAutoCommit(false);

            // Calculating Total
            double total = 0.0;

            // For loop which goes through all items found in the Cart Table.
            for (int row = 0; row < cartTableModel.getRowCount(); row++) {
                // Fetching the subtotal value.
                double subtotal = Double.parseDouble(
                        cartTableModel
                                .getValueAt(row, 4)
                                .toString()
                                .replace("R", "")
                                .replace(",", ".")
                );

                total += subtotal;
            }

            // SQL INSERT SALE
            String saleSQL = """
                    INSERT INTO sales
                    (sale_date, total_amount, user_id)
                    VALUES (CURRENT_TIMESTAMP, ?, ?)
                    """;

            // Prepare the Sale Statement and request for the generated Sale Id.
            PreparedStatement stmt_sale = conn.prepareStatement(saleSQL, Statement.RETURN_GENERATED_KEYS);

            // Settting the placeholder values.
            stmt_sale.setDouble(1, total);
            stmt_sale.setInt(2, cashierId);

            // Executing the SQL statement.
            stmt_sale.executeUpdate();

            // Fetch the Sale ID
            ResultSet rs_keys = stmt_sale.getGeneratedKeys();

            int saleId;

            // Check if a Sale ID was successfully generated.
            if (rs_keys.next()) {
                saleId = rs_keys.getInt(1);
            } else {
                throw new SQLException("Couldn't fetch the generated Sale ID.");
            }

            // Inserting Individual Sale Items
            String itemSQL = """
                    INSERT INTO sale_items
                    (sale_id, medicine_id, quantity_sold, price_at_sale)
                    VALUES (?, ?, ?, ?)
                    """;

            // Prepare the Sale Item statement to be executed.
            PreparedStatement item_stmt = conn.prepareStatement(itemSQL);

            // Updating the Stock by reducing the stock.
            String stockSQL = """
                    UPDATE medicines
                    SET quantity_in_stock = quantity_in_stock - ?
                    WHERE medicine_id = ? AND quantity_in_stock >= ?;
                    """;

            // Preparing the stock update statement to be executed.
            PreparedStatement stock_stmt = conn.prepareStatement(stockSQL);

            // Processing the Cart by looping through each medicine.
            for (int row = 0;  row < cartTableModel.getRowCount(); row++) {
                // Fetch the Medicine ID from the cart table.
                int medicineId = (int) cartTableModel.getValueAt(row, 0);

                // Fetch the Quantity amount of stock being sold.
                int quantity = (int) cartTableModel.getValueAt(row, 2);

                // Fetch the medicine price from the cart table.
                double price = Double.parseDouble(
                        cartTableModel
                                .getValueAt(row, 3)
                                .toString().replace("R", "")
                                .replace(",", ".")
                );

                // Setting the placeholder values for the sale item.
                item_stmt.setInt(1, saleId);
                item_stmt.setInt(2, medicineId);
                item_stmt.setInt(3, quantity);
                item_stmt.setDouble(4, price);

                // Executing the SQL Query for each Sale Item.
                item_stmt.executeUpdate();

                // Setting the placeholder values for the stock.
                stock_stmt.setInt(1, quantity);
                stock_stmt.setInt(2, medicineId);
                stock_stmt.setInt(3, quantity);

                // Executing the results into a variable.
                int rowsUpdated = stock_stmt.executeUpdate();

                // If the variable is set to 0, no query was executed.
                if (rowsUpdated == 0) {
                    throw new SQLException("Insufficient Stock for Medicine ID: " + medicineId);
                }
            }

            // Commit Transaction to the Database and save changes.
            conn.commit();

            // Display the Receipt
            showReceipt(saleId, total);

            // Resetting the POS Interface
            cartTableModel.setRowCount(0);

            // Resetting the Total Label
            totalLbl.setText("Total: R0.00");

            // Resetting the quantity spinner value.
            quantitySpinner.setValue(1);

            // Reset the search field.
            if (!search.getText().trim().isEmpty()) {
                searchMedicine();
            }
            // SQL Error Handling
        } catch (SQLException e) {
            // Perform Rollback Operation on the transaction
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackError) {
                rollbackError.printStackTrace();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Error Completing Sale: " + e.getMessage(),
                    "Sale Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);

                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Private method used to display the receipt for a completed sale.
    private void showReceipt(int saleId, double total) {
        // Default cahsier name used if the cashier cannot be found.
        String cashierName = "Unknown";

        // SQL Query to retrieve the full cashier's name.
        String cashierSQL = """
                SELECT full_name
                FROM users
                WHERE user_id = ?
                """;

        // Try...Catch Block.
        try (
                // Establish a connection to the database.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(cashierSQL);
        ) {
            // Setting the current cashier's ID in the query.
            stmt.setInt(1, cashierId);

            // Executing the query.
            ResultSet rs = stmt.executeQuery();

            // Check if a cashier record was found.
            if (rs.next()) {
                // Fetch the cashier's full name.
                cashierName = rs.getString("full_name");
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not retrieve cashier name: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Receipt Window
        JDialog receiptDialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "HealthFirst Pharmacy - Receipt",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        // Window Parameters.
        receiptDialog.setSize(500, 600);
        receiptDialog.setLayout(new BorderLayout(10, 10));
        receiptDialog.setLocationRelativeTo(this);

        // Custom colors used to complement the window.
        Color customColor = new Color(208, 238, 228);
        Color accentColor = new Color(0, 204, 153);

        // Setting the background color of the window.
        receiptDialog.getContentPane().setBackground(customColor);

        // Heading Panel
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Pharmacy Label
        JLabel pharmacyName = new JLabel("HealthFirst Pharmacy");
        pharmacyName.setFont(new Font("Arial", Font.BOLD, 26));
        pharmacyName.setForeground(accentColor);
        pharmacyName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Receipt Label
        JLabel receiptTitle = new JLabel("SALES RECEIPT");
        receiptTitle.setFont(new Font("Arial", Font.BOLD, 26));
        receiptTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Horizontal Line
        JSeparator sep = new JSeparator();
        sep.setForeground(accentColor);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the components and vertical spacing to the Heading Panel
        heading.add(pharmacyName);
        heading.add(Box.createVerticalStrut(5));
        heading.add(receiptTitle);
        heading.add(Box.createVerticalStrut(5));
        heading.add(sep);

        // Adding the Heading Panel to the Receipt Window.
        receiptDialog.getContentPane().add(heading, BorderLayout.NORTH);

        // Billing Information Panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setBackground(customColor);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Sale ID Label
        JLabel saleIdLbl = new JLabel("Sale ID:");
        saleIdLbl.setFont(new Font("Arial", Font.BOLD, 16));

        // Sale ID Value Label
        JLabel saleIdValueLbl = new JLabel(String.valueOf(saleId));
        saleIdValueLbl.setFont(new Font("Arial", Font.BOLD, 16));

        // Date Label
        JLabel dateLbl = new JLabel("Date:");
        dateLbl.setFont(new Font("Arial", Font.BOLD, 16));

        // Date Value Label
        JLabel dateValueLbl = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm")));
        dateValueLbl.setFont(new Font("Arial", Font.BOLD, 16));

        // Cashier Label
        JLabel cashierLbl = new JLabel("Cashier ID:");
        cashierLbl.setFont(new Font("Arial", Font.BOLD, 16));

        // Cashier Value Label
        JLabel cashierValueLbl = new JLabel(cashierName);
        cashierValueLbl.setFont(new Font("Arial", Font.BOLD, 16));

        // Adding the labels to the Info Panel
        infoPanel.add(saleIdLbl);
        infoPanel.add(saleIdValueLbl);

        infoPanel.add(dateLbl);
        infoPanel.add(dateValueLbl);

        infoPanel.add(cashierLbl);
        infoPanel.add(cashierValueLbl);

        // Creating the columns to display in the table.
        String[] receiptColumns = {
                "Medicine",
                "Quantity",
                "Price",
                "Subtotal"
        };

        // Creating the Table Model.
        DefaultTableModel receiptModel = new DefaultTableModel(
                receiptColumns,
                0
        ) {
            // Ensurign the cells are not editable.
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // For loop which will run through each item in the Cart Table.
        for (int row = 0; row < cartTableModel.getRowCount(); row++) {
            // Fetching the Medicine Name.
            String medicineName = cartTableModel.getValueAt(row, 1).toString();

            // Fetching the Quantity of the Medicine.
            int quantity = (int) cartTableModel.getValueAt(row, 2);

            // Fetching the Price of the Medicine.
            String price = cartTableModel.getValueAt(row, 3).toString();

            // Fetching the Subtotal of the Medicine.
            String subtotal = cartTableModel.getValueAt(row, 4).toString();

            // Adding the columns into a new record in the table.
            receiptModel.addRow(new Object[] {
                    medicineName,
                    quantity,
                    price,
                    subtotal
            });
        }

        // Creating the Table.
        JTable receiptTable = new JTable(receiptModel);
        receiptTable.setRowHeight(30);
        receiptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        receiptTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Creating the Scroll Pane.
        JScrollPane receiptScrollPane = new JScrollPane(receiptTable);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(customColor);

        // Adding the Info Panel and Scroll Pane to the Center Panel.
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(receiptScrollPane, BorderLayout.CENTER);

        // Adding the Center Panel to the Receipt Window.
        receiptDialog.add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel where Total is displayed.
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(customColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // Total Label
        JLabel totalLbl = new JLabel(String.format("Total: R%.2f", total));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 24));
        totalLbl.setForeground(accentColor);
        totalLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(customColor);

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Event Action Listener that closes the window when clicked.
        closeButton.addActionListener(e -> {
            receiptDialog.dispose();
        });

        // Adding the Close button to the Button Panel.
        buttonPanel.add(closeButton);

        // Adding the Total Label, vertical spacing, and the Button Panel to the Bottom Panel
        bottomPanel.add(totalLbl);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(buttonPanel);

        // Adding the Bottom Panel to the Receipt Window.
        receiptDialog.add(bottomPanel, BorderLayout.SOUTH);

        // Displaying the Receipt Window.
        receiptDialog.setVisible(true);
    }

    // POS Panel which passes the cashier ID into the class.
    public POSPanel(int cashierId) {
        // Setting the cashier ID variable to be used here in this panel.
        this.cashierId = cashierId;

        // Custom colour which is used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Setting the layout and arrangement of the POS Panel.
        this.setLayout(new BorderLayout(15, 15));
        this.setBackground(customColor);

        // Title Panel.
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(customColor);

        // Heading Label
        JLabel heading = new JLabel("Point of Sale");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(new Color(0, 204, 153));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Adding the Heading Label and vertical spacing to the Title Panel.
        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(10));

        // Horizontal Line.
        JSeparator sep1 = new JSeparator();
        sep1.setBackground(Color.GRAY);
        sep1.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the Horizontal Line to the Title Panel
        titlePanel.add(sep1);

        // Adding the Title Panel to the POS Panel.
        this.add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBackground(customColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Medicine Panel
        JPanel medicinePanel = new JPanel(new BorderLayout(10, 10));
        medicinePanel.setBackground(new Color(98, 192, 63, 224));
        medicinePanel.setBorder(BorderFactory.createTitledBorder("Medicine Search"));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(new Color(98, 192, 63, 224));

        // Search Field
        search = new JTextField();
        search.setFont(new Font("Arial", Font.BOLD, 18));

        // Search Button
        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 18));
        searchBtn.setBackground(new Color(0, 204, 153));
        searchBtn.setForeground(Color.WHITE);

        // Calling the searchMedicine method when the button is clicked.
        searchBtn.addActionListener(e -> {
            searchMedicine();
        });

        // Adding the search field and search button to the Search Panel
        searchPanel.add(search, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        // Adding the Search Panel to the Medicine Panel
        medicinePanel.add(searchPanel, BorderLayout.NORTH);

        // Creating the columns for the Medicine Table.
        String[] medicineColumns = {
                "Medicine ID",
                "Medicine",
                "Company",
                "Price",
                "Stock"
        };

        // Creating the Medicine Table model.
        medicineTableModel = new DefaultTableModel(
                medicineColumns,
                0
        ) {
            // Ensuring that the Medicine Table's cells are not editable.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Creating the Medicine Table based on the model.
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Creating the Scroll Pane and attaching it to the Medicine Table.
        JScrollPane medicineScrollPane = new JScrollPane(medicineTable);

        // Adding the Scroll Pane to the Medicine Panel.
        medicinePanel.add(medicineScrollPane, BorderLayout.CENTER);

        // Add Panel
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBackground(new Color(98, 192, 63, 224));

        // Quantity Label
        JLabel quantityLbl = new JLabel("Quantity");
        quantityLbl.setFont(new Font("Arial", Font.BOLD, 16));

        // Quantity Spin Value.
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantitySpinner.setFont(new Font("Arial", Font.BOLD, 16));

        // Add To Cart Button
        addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setFont(new Font("Arial", Font.BOLD, 20));
        addToCartBtn.setBackground(new Color(0, 204, 153));
        addToCartBtn.setForeground(Color.WHITE);

        // Calling the addToCart method when the button is clicked.
        addToCartBtn.addActionListener(e -> {
            addToCart();
        });

        // Adding components to the Add Panel.
        addPanel.add(quantityLbl);
        addPanel.add(quantitySpinner);
        addPanel.add(addToCartBtn);

        // Add the Add Panel to the Medicine Panel.
        medicinePanel.add(addPanel, BorderLayout.SOUTH);

        // Cart Panel used to process a current sale being made.
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBackground(new Color(98, 192, 63, 224));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Current Sale"));

        // Creating the columns for the Cart Table.
        String[] cartColumns = {
                "Medicine ID",
                "Medicine",
                "Qty",
                "Price",
                "Subtotal"
        };

        // Creating the Cart Table Model
        cartTableModel = new DefaultTableModel(
                cartColumns,
                0
        ) {
            // Ensures the Cart Table's cells aren't editable.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // JTable which is created based on the table model.
        cartTable = new JTable(cartTableModel);

        // Scroll Pane which is added to the JTable.
        JScrollPane cartScrollPane = new JScrollPane(cartTable);

        // Adding the ScrollPane to the Cart Panel.
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(98, 192, 63, 224));

        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(new Color(98, 192, 63, 224));

        // Total Label
        totalLbl = new JLabel("Total: R0.00");
        totalLbl.setFont(new Font("Arial", Font.BOLD, 20));

        // Adding the Total Label to the Total Panel.
        totalPanel.add(totalLbl);

        // Button Panel.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(98, 192, 63, 224));

        // Clear Sale Button.
        clearSaleBtn = new JButton("Clear Sale");
        clearSaleBtn.setFont(new Font("Arial", Font.BOLD, 20));
        clearSaleBtn.setBackground(Color.LIGHT_GRAY);

        // Calling the clearSale method when the button is clicked.
        clearSaleBtn.addActionListener(e -> {
            clearSale();
        });

        // Complete Sale Button
        completeSaleBtn = new JButton("Complete Sale");
        completeSaleBtn.setFont(new Font("Arial", Font.BOLD, 20));
        completeSaleBtn.setBackground(new Color(0, 204, 153));
        completeSaleBtn.setForeground(Color.WHITE);

        // Calling the completeSale method when the button is clicked.
        completeSaleBtn.addActionListener(e -> {
            completeSale();
        });

        // Adding the Buttons to the Button Panel.
        buttonPanel.add(clearSaleBtn);
        buttonPanel.add(completeSaleBtn);

        // Adding the Total and Button Panels to the Bottom Panel.
        bottomPanel.add(totalPanel);
        bottomPanel.add(buttonPanel);

        // Adding Bottom Panel to the Cart Panel.
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Adding the Medicine and Cart Panel to the Main Panel.
        mainPanel.add(medicinePanel);
        mainPanel.add(cartPanel);

        // Adding Main Panel to the POS Panel.
        this.add(mainPanel, BorderLayout.CENTER);
    }
}