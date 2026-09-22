// Importing libraries to use in Stock Check Panel.
import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Stock Check Panel class which extends JPanel
public class StockCheckPanel extends JPanel {
    // Private variables used to ensure security when creating components.
    private JTextField searchFld;
    private JButton searchBtn, refreshBtn;
    private JTable stockTable;
    private DefaultTableModel stockModel;

    // Private method which will search for stock in the database.
    private void searchStock() {
        // String variable used to fetch the text inserted by the user in the search field.
        String searchText = searchFld.getText().trim();

        // Input Validation Fields
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a medicine name or company.",
                    "Search",
                    JOptionPane.WARNING_MESSAGE
            );

            searchFld.requestFocus();

            return;
        }
        // Resetting the Table Model by removing existing rows.
        stockModel.setRowCount(0);

        // SELECT SQL Query
        String sql = """
                SELECT medicine_id, name, company, quantity_in_stock, reorder_level
                FROM medicines
                WHERE name LIKE ? OR company LIKE ?
                ORDER BY name
                """;

        // Try...Catch Block
        try (
                // Establishing a connection to the database.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Initializing search string.
            String search = "%" + searchText + "%";

            // Set placeholder values.
            stmt.setString(1, search);
            stmt.setString(2, search);

            // Execute the query.
            ResultSet rs = stmt.executeQuery();

            // Initialize the result count variable.
            int resultCount = 0;

            // While loop which runs through the results.
            while (rs.next()) {
                // Initializing variables with the associated placeholder values obtained from the result set.
                int medicineId = rs.getInt("medicine_id");
                String name = rs.getString("name");
                String company = rs.getString("company");
                int currentStock = rs.getInt("quantity_in_stock");
                int reorderLevel = rs.getInt("reorder_level");

                // Determining Status variable.
                String status;

                // If Statement to determine status value.
                if (currentStock == 0) {
                    status = "Out of Stock";
                } else if (currentStock <= reorderLevel) {
                    status = "Low Stock";
                } else {
                    status = "In Stock";
                }

                // Adding the variables into the table.
                stockModel.addRow(new Object[] {
                        medicineId,
                        name,
                        company,
                        currentStock,
                        reorderLevel,
                        status
                });

                // Increase the result counter variable.
                resultCount++;
            }

            // If the result counter is still at 0, display the appropriate dialog.
            if (resultCount == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No medicines found.",
                        "Search",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            // SQL Error Handling
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Searching Stock: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private Method used to load stock into the table.
    private void loadStock() {
        // Removing all existing rows from the Stock Table.
        stockModel.setRowCount(0);

        // SELECT SQL Query
        String sql = """
                SELECT medicine_id, name, company, quantity_in_stock, reorder_level
                FROM medicines
                ORDER BY name
                """;

        // Try...Catch Block
        try (
                // Establishing a connection to the database and executing the query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            // While loop which runs through the results.
            while (rs.next()) {
                // Initializing variables with the associated placeholder values obtained from the result set.
                int medicineId = rs.getInt("medicine_id");
                String name = rs.getString("name");
                String company = rs.getString("company");
                int currentStock = rs.getInt("quantity_in_stock");
                int reorderLevel = rs.getInt("reorder_level");

                // Determining Status variable.
                String status;

                // If Statement to determine status value.
                if (currentStock == 0) {
                    status = "Out of Stock";
                } else if (currentStock <= reorderLevel) {
                    status = "Low Stock";
                } else {
                    status = "In Stock";
                }

                // Adding the variables into the table.
                stockModel.addRow(new Object[] {
                        medicineId,
                        name,
                        company,
                        currentStock,
                        reorderLevel,
                        status
                });
            }
            // SQL Error Handling
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error Loading Stock: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Stock Check Panel class.
    public StockCheckPanel() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Setting the layout of the Stock Check Panel.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(customColor);
        this.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 10));

        // Heading Panel
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Title Label
        JLabel titleLbl = new JLabel("Stock Check");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 24));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLbl.setForeground(new Color(0, 204, 153));

        // Subtitle Label
        JLabel subtitleLbl = new JLabel("View current medicine stock levels and stock status");
        subtitleLbl.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adding labels and vertical spacing to the Stock Check Panel
        heading.add(titleLbl);
        heading.add(Box.createVerticalStrut(7));
        heading.add(subtitleLbl);

        // Adding the Heading Panel to the Stock Check Panel
        this.add(heading);

        // Adding vertical spacing to the Stock Check Panel
        this.add(Box.createVerticalStrut(10));

        // Horizontal Line
        JSeparator sep1 = new JSeparator();
        sep1.setBackground(Color.GRAY);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the horizontal line to the Stock Check Panel
        this.add(sep1);

        // Adding vertical spacing to the Stock Check Panel
        this.add(Box.createVerticalStrut(13));

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(customColor);

        // Search Label
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(new Font("Arial", Font.BOLD, 18));

        // Search Field
        searchFld = new JTextField(25);
        searchFld.setFont(new Font("Arial", Font.BOLD, 18));

        // Search Button
        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 18));

        // Event Action Listener which calls the searchStock method when executed.
        searchBtn.addActionListener(e -> {
            searchStock();
        });

        // Refresh Button
        refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 18));

        // Event Action Listener which calls the loadStock method when executed.
        refreshBtn.addActionListener(e -> {
            loadStock();
        });

        // Adding the Search Label to the Search Panel.
        searchPanel.add(searchLbl);

        // Adding horizontal spacing to the Search Panel.
        searchPanel.add(Box.createHorizontalStrut(5));

        // Adding the Search Field to the Search Panel.
        searchPanel.add(searchFld);

        // Adding horizontal spacing to the Search Panel.
        searchPanel.add(Box.createHorizontalStrut(15));

        // Adding the Search Button to the Search Panel.
        searchPanel.add(searchBtn);

        // Adding horizontal spacing to the Search Panel.
        searchPanel.add(Box.createHorizontalStrut(7));

        // Adding the Refresh Button to the Search Panel.
        searchPanel.add(refreshBtn);

        // Adding vertical spacing to the Stock Check Panel.
        this.add(Box.createVerticalStrut(15));

        // Creates the columns for the table.
        String[] columns = {
                "ID",
                "Medicine",
                "Company",
                "Current Stock",
                "Reorder Level",
                "Status"
        };

        // Creating the Table Model
        stockModel = new DefaultTableModel(columns, 0) {
            // Ensures that all cells are not editable.
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // Creating the JTable
        stockTable = new JTable(stockModel);
        stockTable.setRowHeight(30);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 14));
        stockTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(customColor);
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Adding the Search Panel and Scroll Pane to the Center Panel.
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Adding the Center Panel to the Stock Check Panel.
        this.add(centerPanel);

        // Calling the loadStock method.
        loadStock();
    }
}