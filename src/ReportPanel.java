// Importing libraries to utilize in Report Panel.
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// ReportPanel class which extends JPanel and is used to display all functionality within the card found in the Admin
// Dashboard
public class ReportPanel extends JPanel {
    // Private method used to run the Sales Report Dialog Window.
    private void showSalesReport() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Dialog Window being created which will display the report details.
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Sales Report",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        // Dialog Parameters
        dialog.setSize(950, 600);
        dialog.setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(customColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Heading Panel
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Label
        JLabel titleLbl = new JLabel("Sales Report");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 20));
        titleLbl.setForeground(new Color(0, 204, 153));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle Label
        JLabel subtitleLbl = new JLabel("View pharmacy sales and transaction information.");
        subtitleLbl.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adding Title Label and Subtitle Label to the Heading Panel
        heading.add(titleLbl);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitleLbl);

        // Adding the Heading Panel to the Main Panel.
        mainPanel.add(heading, BorderLayout.NORTH);

        // Creating columns to display in the table
        String[] columns = {
                "Sale ID",
                "Date and Time",
                "Cashier",
                "Total Amount"
        };

        // Creating the Table Model.
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            // Ensuring the table cells are not editable.
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // Creating the JTable based on the model.
        JTable table = new JTable(model);

        // Setting parameters of the JTable.
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        // SQL Query
        String sql = """
                SELECT sale.sale_id, sale.sale_date, user.full_name, sale.total_amount
                FROM sales sale
                JOIN users user
                ON sale.user_id = user.user_id
                ORDER BY sale.sale_date DESC
                """;

        // Initializing the variable.
        double totalSales = 0.0;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            // While loop which runs through the results.
            while (rs.next()) {
                // Initializing variables with the associated placeholder values obtained from the result set.
                int saleId = rs.getInt("sale_id");
                String saleDate = rs.getString("sale_date");
                String cashier = rs.getString("full_name");
                double amount = rs.getDouble("total_amount");

                // Adding the variables into the table.
                model.addRow(new Object[] {
                        saleId,
                        saleDate,
                        cashier,
                        String.format("R%.2f", amount)
                });

                // Summing up the total amount variable.
                totalSales += amount;
            }
            // SQL Error Handling
        } catch (SQLException e) {
            // Dialog Box to display error.
            JOptionPane.showMessageDialog(
                    dialog,
                    "Error Loading Sales Report: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Scroll Pane added to the JTable
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Adding the Scroll Pane to the Main Panel.
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(customColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total Label
        JLabel totalLbl = new JLabel(String.format("Total Sales: R%.2f", totalSales));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 18));

        // Close Button
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));

        // Event Action Listener to close the report dialog window.
        closeBtn.addActionListener(e -> {
            dialog.dispose();
        });

        // Adding the Total Label to the Bottom Panel.
        bottomPanel.add(totalLbl, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(customColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        // Adding the Close Button to the Button Panel.
        buttonPanel.add(closeBtn);

        // Adding the Button Panel to the Bottom Panel.
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Adding the Bottom Panel to the Main Panel.
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Adding the Main Panel to the Dialog Window.
        dialog.add(mainPanel);

        // Displaying the Dialog Window.
        dialog.setVisible(true);
    }

    // Private method used to run the Item-Wise Sale Report Dialog Window.
    private void showItemWiseReport() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Dialog Window being created which will display the report details.
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Item-Wise Sales Report",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        // Dialog Parameters
        dialog.setSize(950, 600);
        dialog.setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(customColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Heading Panel
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Label
        JLabel titleLbl = new JLabel("Item-Wise Sales Report");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 20));
        titleLbl.setForeground(new Color(0, 204, 153));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle Label
        JLabel subtitleLbl = new JLabel("View medicine sales and revenue by item.");
        subtitleLbl.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adding Title Label and Subtitle Label to the Heading Panel
        heading.add(titleLbl);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitleLbl);

        // Adding the Heading Panel to Main Panel.
        mainPanel.add(heading, BorderLayout.NORTH);

        // Creating columns to display in the table
        String[] columns = {
                "Medicine ID",
                "Medicine",
                "Company",
                "Units Sold",
                "Revenue"
        };

        // Creating the Table Model
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            // Ensuring the table cells are not editable.
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // Creating the JTable based on the model.
        JTable table = new JTable(model);

        // Setting parameters of the JTable.
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        // SQL Query
        String sql = """
                SELECT medicine.medicine_id, medicine.name, medicine.company, SUM(si.quantity_sold) AS units_sold,
                       SUM(si.quantity_sold * si.price_at_sale) AS revenue
                FROM sale_items si
                JOIN medicines medicine
                ON si.medicine_id = medicine.medicine_id
                GROUP BY medicine.medicine_id, medicine.name, medicine.company
                ORDER BY units_sold DESC
                """;

        // Initializing the variable.
        double totalRevenue = 0.0;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            // While loop which runs through the results.
            while (rs.next()) {
                // Initializing variables with the associated placeholder values obtained from the result set.
                int medicineId = rs.getInt("medicine_id");
                String medicineName = rs.getString("name");
                String company = rs.getString("company");
                int unitsSold = rs.getInt("units_sold");
                double revenue = rs.getDouble("revenue");

                // Adding the variables into the table.
                model.addRow(new Object[] {
                        medicineId,
                        medicineName,
                        company,
                        unitsSold,
                        String.format("R%.2f", revenue)
                });

                // Summing up the total amount variable.
                totalRevenue += revenue;
            }
            // SQL Error Handling
        } catch (SQLException e) {
            // Dialog Box to display error.
            JOptionPane.showMessageDialog(
                    dialog,
                    "Error Loading Item-Wise Report: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Scroll Pane added to the JTable
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Adding the Scroll Pane to the Main Panel.
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(customColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total Label
        JLabel totalLbl = new JLabel(String.format("Total Revenue: R%.2f", totalRevenue));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 18));

        // Close Button
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));

        // Event Action Listener to close the report dialog window.
        closeBtn.addActionListener(e -> {
            dialog.dispose();
        });

        // Adding the Total Label to the Bottom Panel.
        bottomPanel.add(totalLbl, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(customColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        // Adding the Close Button to the Button Panel.
        buttonPanel.add(closeBtn);

        // Adding the Button Panel to the Bottom Panel.
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Adding the Bottom Panel to the Main Panel.
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Adding the Main Panel to the Dialog Window.
        dialog.add(mainPanel);

        // Displaying the Dialog Window.
        dialog.setVisible(true);
    }

    // Private method used to run the Low Stock Report Dialog Window.
    private void showLowStockReport() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Dialog Window being created which will display the report details.
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Low Stock Report",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        // Dialog Parameters
        dialog.setSize(950, 600);
        dialog.setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(customColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Heading Panel
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Label
        JLabel titleLbl = new JLabel("Low Stock Report");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 20));
        titleLbl.setForeground(new Color(0, 204, 153));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle Label
        JLabel subtitleLbl = new JLabel("View medicines that have reached or fallen below their reorder level.");
        subtitleLbl.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adding Title Label and Subtitle Label to the Heading Panel
        heading.add(titleLbl);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitleLbl);

        // Adding the Heading Panel to the Main Panel.
        mainPanel.add(heading, BorderLayout.NORTH);

        // Creating columns to display in the table
        String[] columns = {
                "Medicine ID",
                "Medicine",
                "Company",
                "Current Stock",
                "Reorder Level"
        };

        // Creating the Table Model.
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            // Ensuring the table cells are not editable.
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // Creating the JTable based on the model.
        JTable table = new JTable(model);

        // Setting parameters of the JTable.
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        // SQL Query
        String sql = """
                SELECT medicine_id, name, company, quantity_in_stock, reorder_level
                FROM medicines
                WHERE quantity_in_stock <= reorder_level
                ORDER BY quantity_in_stock ASC, name
                """;

        // Initializing the variable.
        int medicineCount = 0;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            // While loop which runs through the results.
            while (rs.next()) {
                // Initializing variables with the associated placeholder values obtained from the result set.
                int medicineId = rs.getInt("medicine_id");
                String medicineName = rs.getString("name");
                String company = rs.getString("company");
                int currentStock = rs.getInt("quantity_in_stock");
                int reorderLevel = rs.getInt("reorder_level");

                // Adding the variables into the table.
                model.addRow(new Object[] {
                        medicineId,
                        medicineName,
                        company,
                        currentStock,
                        reorderLevel
                });

                // Summing up the medicine count variable.
                medicineCount++;
            }
            // SQL Error Handling
        } catch (SQLException e) {
            // Dialog Box to display error.
            JOptionPane.showMessageDialog(
                    dialog,
                    "Error Loading Low Stock Report: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Scroll Pane added to the JTable
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Adding the Scroll Pane to the Main Panel.
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(customColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Count Label
        JLabel countLbl = new JLabel("Medicines Requiring Restock: " + medicineCount);
        countLbl.setFont(new Font("Arial", Font.BOLD, 18));

        // Close Button
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));

        // Event Action Listener to close the report dialog window.
        closeBtn.addActionListener(e -> {
            dialog.dispose();
        });

        // Adding the Count Label to the Bottom Panel.
        bottomPanel.add(countLbl, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(customColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        // Adding the Close Button to the Button Panel.
        buttonPanel.add(closeBtn);

        // Adding the Button Panel to the Bottom Panel.
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Adding the Bottom Panel to the Main Panel.
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Adding the Main Panel to the Dialog Window.
        dialog.add(mainPanel);

        // Displaying the Dialog Window.
        dialog.setVisible(true);
    }

    // Private method used to run the Expiry Report Dialog Window.
    private void showExpiryReport() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Dialog Window being created which will display the report details.
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Expiry Report",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        // Dialog Parameters
        dialog.setSize(950, 600);
        dialog.setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(customColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Heading Panel
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Label
        JLabel titleLbl = new JLabel("Expiry Report");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 20));
        titleLbl.setForeground(new Color(0, 204, 153));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle Label
        JLabel subtitleLbl = new JLabel("View expired medicines and medicines their expiry date.");
        subtitleLbl.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adding Title Label and Subtitle Label to the Heading Panel
        heading.add(titleLbl);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitleLbl);

        // Adding the Heading Panel to the Main Panel.
        mainPanel.add(heading, BorderLayout.NORTH);

        // Creating columns to display in the table
        String[] columns = {
                "Medicine ID",
                "Medicine",
                "Company",
                "Expiry Date",
                "Current Stock",
                "Status"
        };

        // Creating the Table Model.
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            // Ensuring the table cells are not editable.
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // Creating the JTable based on the model.
        JTable table = new JTable(model);

        // Setting parameters of the JTable.
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        // SQL Query
        String sql = """
                SELECT medicine_id, name, company, expiry_date, quantity_in_stock
                FROM medicines
                WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)
                ORDER BY expiry_date ASC
                """;

        // Initializing the variable.
        int expiredCount = 0;
        int expiringSoonCount = 0;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            // While loop which runs through the results.
            while (rs.next()) {
                // Initializing variables with the associated placeholder values obtained from the result set.
                int medicineId = rs.getInt("medicine_id");
                String medicineName = rs.getString("name");
                String company = rs.getString("company");
                String expiryDate = rs.getString("expiry_date");
                int currentStock = rs.getInt("quantity_in_stock");

                // Initializing status variable.
                String status;

                // Determining status variable value.
                if (rs.getDate("expiry_date").toLocalDate().isBefore(java.time.LocalDate.now())) {
                    status = "Expired";
                    expiredCount++;
                } else {
                    status = "Expiring Soon";
                    expiringSoonCount++;
                }

                // Adding the variables into the table.
                model.addRow(new Object[] {
                        medicineId,
                        medicineName,
                        company,
                        expiryDate,
                        currentStock,
                        status
                });
            }
            // SQL Error Handling
        } catch (SQLException e) {
            // Dialog Box to display error.
            JOptionPane.showMessageDialog(
                    dialog,
                    "Error Loading Expiry Report: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Scroll Pane added to the JTable
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Adding the Scroll Pane to the Main Panel.
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(customColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Summary Label
        JLabel summaryLbl = new JLabel("Expired: " + expiredCount + "   |   Expiring Soon: " + expiringSoonCount);
        summaryLbl.setFont(new Font("Arial", Font.BOLD, 18));

        // Close Button
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));

        // Event Action Listener to close the report dialog window.
        closeBtn.addActionListener(e -> {
            dialog.dispose();
        });

        // Adding the Summary Label to the Bottom Panel.
        bottomPanel.add(summaryLbl, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(customColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        // Adding the Close Button to the Button Panel.
        buttonPanel.add(closeBtn);

        // Adding the Button Panel to the Bottom Panel.
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Adding the Bottom Panel to the Main Panel.
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Adding the Main Panel to the Dialog Window.
        dialog.add(mainPanel);

        // Displaying the Dialog Window.
        dialog.setVisible(true);
    }

    // Report Panel
    public ReportPanel() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Setting out the layout of the ReportPanel.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(customColor);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Heading Panel used to contain the title and description.
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Title Component.
        JLabel reportTitle = new JLabel("Reports");
        reportTitle.setFont(new Font("Arial", Font.BOLD, 24));
        reportTitle.setForeground(new Color(0, 204, 153));

        // Description component.
        JLabel reportDesc = new JLabel("View and Generate Pharmacy Management Reports");
        reportDesc.setFont(new Font("Arial", Font.BOLD, 18));

        // Adding the two components and vertical spacing between each other.
        heading.add(reportTitle);
        heading.add(Box.createVerticalStrut(7));
        heading.add(reportDesc);

        // Adding the Heading Panel to the Report Panel.
        this.add(heading);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(10));

        // Horizontal Line Component.
        JSeparator sep1 = new JSeparator();
        sep1.setBackground(Color.GRAY);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the component to the Report Panel.
        this.add(sep1);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(13));

        // Reports Panel used to add each panel that contains the report description.
        JPanel reportsMainPanel = new JPanel();
        reportsMainPanel.setLayout(new GridLayout(2, 2, 15, 15));
        reportsMainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sales Report Panel
        JPanel salesReportPanel = new JPanel();
        salesReportPanel.setLayout(new BoxLayout(salesReportPanel, BoxLayout.Y_AXIS));
        salesReportPanel.setBackground(new Color(0, 204, 153));
        salesReportPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Sales Title
        JLabel salesTitle = new JLabel("Sales Report");
        salesTitle.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 18));
        salesTitle.setForeground(Color.YELLOW);
        salesTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sales Description
        JLabel salesDescription = new JLabel("View Sales and Transaction Information.");
        salesDescription.setFont(new Font("Arial", Font.BOLD, 18));
        salesDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sales Button
        JButton salesButton = new JButton("View Report");
        salesButton.setFont(new Font("Arial", Font.BOLD, 18));
        salesButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Action Event Listener which calls the showSalesReport method.
        salesButton.addActionListener(e -> {
            showSalesReport();
        });

        // Adding vertical spacing and the associated components to the Sales Report Panel.
        salesReportPanel.add(Box.createVerticalStrut(15));
        salesReportPanel.add(salesTitle);
        salesReportPanel.add(Box.createVerticalStrut(5));
        salesReportPanel.add(salesDescription);
        salesReportPanel.add(Box.createVerticalStrut(15));
        salesReportPanel.add(salesButton);

        // Item-Wise Report Panel
        JPanel itemWiseReportPanel = new JPanel();
        itemWiseReportPanel.setLayout(new BoxLayout(itemWiseReportPanel, BoxLayout.Y_AXIS));
        itemWiseReportPanel.setBackground(new Color(0, 204, 153));
        itemWiseReportPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Item-Wise Title
        JLabel itemTitle = new JLabel("Item-Wise Report");
        itemTitle.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 18));
        itemTitle.setForeground(Color.YELLOW);
        itemTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Item-Wise Description
        JLabel itemDescription = new JLabel("View Medicine Sales by Item.");
        itemDescription.setFont(new Font("Arial", Font.BOLD, 16));
        itemDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Item-Wise Button
        JButton itemButton = new JButton("View Report");
        itemButton.setFont(new Font("Arial", Font.BOLD, 18));
        itemButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Action Event Listener which calls the showItemWiseReport method.
        itemButton.addActionListener(e -> {
            showItemWiseReport();
        });

        // Adding vertical spacing and the associated components to the Item-Wise Report Panel.
        itemWiseReportPanel.add(Box.createVerticalStrut(15));
        itemWiseReportPanel.add(itemTitle);
        itemWiseReportPanel.add(Box.createVerticalStrut(5));
        itemWiseReportPanel.add(itemDescription);
        itemWiseReportPanel.add(Box.createVerticalStrut(15));
        itemWiseReportPanel.add(itemButton);

        // Low Stock Report Panel
        JPanel lowStockReportPanel = new JPanel();
        lowStockReportPanel.setLayout(new BoxLayout(lowStockReportPanel, BoxLayout.Y_AXIS));
        lowStockReportPanel.setBackground(new Color(0, 204, 153));
        lowStockReportPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Stock Title
        JLabel stockTitle = new JLabel("Low Stock Report");
        stockTitle.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 18));
        stockTitle.setForeground(Color.YELLOW);
        stockTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stock Description
        JLabel stockDescription = new JLabel("View Medicines that Need Restocking.");
        stockDescription.setFont(new Font("Arial", Font.BOLD, 16));
        stockDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stock Button
        JButton stockButton = new JButton("View Report");
        stockButton.setFont(new Font("Arial", Font.BOLD, 18));
        stockButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Action Event Listener which calls the showLowStockReport method.
        stockButton.addActionListener(e -> {
            showLowStockReport();
        });

        // Adding vertical spacing and the associated components to the Low Stock Report Panel.
        lowStockReportPanel.add(Box.createVerticalStrut(15));
        lowStockReportPanel.add(stockTitle);
        lowStockReportPanel.add(Box.createVerticalStrut(5));
        lowStockReportPanel.add(stockDescription);
        lowStockReportPanel.add(Box.createVerticalStrut(15));
        lowStockReportPanel.add(stockButton);

        // Expiry Report Panel
        JPanel expiryReportPanel = new JPanel();
        expiryReportPanel.setLayout(new BoxLayout(expiryReportPanel, BoxLayout.Y_AXIS));
        expiryReportPanel.setBackground(new Color(0, 204, 153));
        expiryReportPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Expiry Title
        JLabel expiryTitle = new JLabel("Expiry Report");
        expiryTitle.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 18));
        expiryTitle.setForeground(Color.YELLOW);
        expiryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Expiry Description
        JLabel expiryDescription = new JLabel("View Expired and Expiring Medicines.");
        expiryDescription.setFont(new Font("Arial", Font.BOLD, 16));
        expiryDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Expiry Button
        JButton expiryButton = new JButton("View Report");
        expiryButton.setFont(new Font("Arial", Font.BOLD, 18));
        expiryButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Action Event Listener which calls the showExpiryReport method.
        expiryButton.addActionListener(e -> {
            showExpiryReport();
        });

        // Adding vertical spacing and the associated components to the Expiry Report Panel.
        expiryReportPanel.add(Box.createVerticalStrut(15));
        expiryReportPanel.add(expiryTitle);
        expiryReportPanel.add(Box.createVerticalStrut(5));
        expiryReportPanel.add(expiryDescription);
        expiryReportPanel.add(Box.createVerticalStrut(15));
        expiryReportPanel.add(expiryButton);

        // Adding all panels to the Reports Panel.
        reportsMainPanel.add(salesReportPanel);
        reportsMainPanel.add(itemWiseReportPanel);
        reportsMainPanel.add(lowStockReportPanel);
        reportsMainPanel.add(expiryReportPanel);

        // Adding the Reports Panel to the Main Panel.
        this.add(reportsMainPanel);
    }
}