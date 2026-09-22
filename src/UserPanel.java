// Importing libraries to utilize in User Panel.
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// UserPanel class which extends JPanel and is used to display all functionality within the card found in the Admin
// Dashboard
public class UserPanel extends JPanel {
    // Private variables used to ensure security when creating components.
    private JTextField username, user_full_name, user_role, search;
    private JPasswordField user_password, user_confirm_password;
    private JButton add_user, update_user, reset_user, delete_user, clear_user, search_user, refresh_user;
    private DefaultTableModel model;
    private JTable userTable;

    // Private method used to load all the users found within the Users table into the table.
    private void loadUsers(DefaultTableModel model) {
        // Remove all existing rows before loading in a clean record of the database table.
        model.setRowCount(0);

        // SQL Query
        String sql = """
                SELECT user_id, username, full_name, role
                FROM users
                WHERE role = 'Cashier'
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
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role")
                };

                // Adding each column and its associated placeholders into a single record.
                model.addRow(row);
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            // Dialog box to display error.
            JOptionPane.showMessageDialog(
                    this,
                    "Error Loading Users: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform the Create operation as part of CRUD.
    private void addUser() {
        // Assigning user input from the components to variables for processing.
        String userName = username.getText().trim();
        String fullName = user_full_name.getText().trim();
        String password = new String(user_password.getPassword());
        String confirmPassword = new String(user_confirm_password.getPassword());

        // Input Validation on Input Fields
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Username.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            username.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Full Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            user_full_name.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a Password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            user_password.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please confirm the Password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            user_confirm_password.requestFocus();

            return;
        }

        // Input Validation on Input Fields by ensuring both password fields and their values match,
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match.",
                    "Password Error",
                    JOptionPane.WARNING_MESSAGE
            );
            user_confirm_password.requestFocus();

            return;
        }

        try {
            // SQL INSERT statement
            String sql = """
                    INSERT INTO users
                    (username, password, role, full_name)
                    VALUES (?, ?, ?, ?)
                    """;

            // Try...Catch block.
            try (
                    // Establishing connection with the database and executing the sql query.
                    Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stm = conn.prepareStatement(sql);
            ) {
                // Setting Placeholder Values in the statement based on our input variables.
                stm.setString(1, userName);
                stm.setString(2, password);
                stm.setString(3, "Cashier");
                stm.setString(4, fullName);

                // Execute INSERT sql statement.
                stm.executeUpdate();

                // Success Message
                JOptionPane.showMessageDialog(
                        this,
                        "Cashier Account Created Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh JTable component by calling the loadUsers method.
                loadUsers(model);

                // Clear Form by calling the clearUser method.
                clearUser();
            }
            // SQL Error Handling
        } catch (SQLException e) {
            // If statement that is executed if the username already exists.
            if (e.getMessage().toLowerCase().contains("duplicate")) {
                JOptionPane.showMessageDialog(
                        this,
                        "That username already exists. Please choose another username.",
                        "Username Already Exists",
                        JOptionPane.WARNING_MESSAGE
                );

                username.requestFocus();
                // Dialog box to display an error in creating the user account.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Error Adding User: "  + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Private method used to perform the Update operation as part of CRUD.
    private void updateUser() {
        // Check if a user has been selected.
        int selectedRow = userTable.getSelectedRow();

        // If no row, has been selected, display a dialog box showcasing a message.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a cashier from the table.",
                    "No User Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get the User ID from the selected row.
        int userId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        // Fetch all input entered by the user within the form components into their associated variables.
        String userName = username.getText().trim();
        String fullName = user_full_name.getText().trim();

        // Input Validation on Input Fields
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a Username.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            username.requestFocus();

            return;
        }

        // Input Validation on Input Fields
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the Full Name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            username.requestFocus();

            return;
        }

        // SQL UPDATE statement
        String sql = """
                UPDATE users
                SET username = ?,
                    full_name = ?
                WHERE user_id = ? AND role = 'Cashier'
                """;

        // Try...Catch block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Setting Placeholder Values in the statement based on our input variables.
            stmt.setString(1, userName);
            stmt.setString(2, fullName);

            // Set the ID of the user being selected.
            stmt.setInt(3, userId);

            // Execute UPDATE Statement
            int rowsUpdated = stmt.executeUpdate();

            // If statement which ensures that the update being performed on the query returns more than 0 rows.
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cashier Account Updated Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh the table by calling the loadUsers method.
                loadUsers(model);

                // Clear the form by calling the clearUser method.
                clearUser();
                // Dialog which displays an error if the results of the query returns 0 rows.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Cashier Account could not be found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Updating User: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform a reset password if the user has forgotten their password.
    private void resetUser() {
        // Fetch the row number of the row which the user has selected.
        int selectedRow = userTable.getSelectedRow();

        // If statement that is executed if the user hasn't selected a row.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a cashier from the table.",
                    "No User Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get User ID from the selected row.
        int userId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        // Fetch the name of the user from the selected row.
        String selectedUsername = model.getValueAt(selectedRow, 1).toString();

        // Creating the associated password components.
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();

        // Creating the reset panel to house these components.
        JPanel resetPanel = new JPanel();
        resetPanel.setLayout(new GridLayout(2, 2, 10, 10));

        // Adding the components to the Reset Panel.
        resetPanel.add(new JLabel("New Password:"));
        resetPanel.add(newPassword);
        resetPanel.add(new JLabel("Confirm Password:"));
        resetPanel.add(confirmPassword);

        // Introducing a dialog box to confirm if the user wants to reset their password.
        int result = JOptionPane.showConfirmDialog(
                this,
                resetPanel,
                "Reset Password - " + selectedUsername,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        // If the user selects No, cancel the window.
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // String variables to store user input in the dialog window.
        String new_password = new String(newPassword.getPassword());
        String confirm_password = new String(confirmPassword.getPassword());

        // Input validation on fields.
        if (new_password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a new password.",
                    "Missing Password",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Input validation on fields by ensuring that both fields are the same.
        if (!new_password.equals(confirm_password)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match.",
                    "Password Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // UPDATE SQL Query.
        String sql = """
                UPDATE users
                SET password = ?
                WHERE user_id = ?
                AND role = 'Cashier'
                """;

        // Try...Catch Statement
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Set the ID and new password of the user being deleted.
            stmt.setString(1, new_password);
            stmt.setInt(2, userId);

            // Execute UPDATE Statement
            int rowsUpdated = stmt.executeUpdate();

            // If statement which ensures that the update being performed on the query returns more than 0 rows.
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password Reset Successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // Dialog which displays an error if the results of the query returns 0 rows.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Password could not be reset.",
                        "Reset Failed.",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Resetting Password: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to clear all components of any text entered by the user.
    private void clearUser() {
        // Resetting all components values to either null, 0, or no selection.
        username.setText("");
        user_full_name.setText("");
        user_role.setText("Cashier");
        user_password.setText("");
        user_confirm_password.setText("");

        userTable.clearSelection();

        username.requestFocus();
    }

    // Private method used to perform the DELETE Method as part of CRUD.
    private void deleteUser() {
        // Fetch the row number of the row which the user has selected.
        int selectedRow = userTable.getSelectedRow();

        // If statement that is executed if the user hasn't selected a row.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a cashier from the table.",
                    "No User Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Get User ID from the selected row.
        int userId = Integer.parseInt(userTable.getValueAt(selectedRow, 0).toString());

        // Fetch the name of the user from the selected row.
        String userName = model.getValueAt(selectedRow, 1).toString();

        // Integer variable used to display a confirmation dialog box which will be used to confirm deletion of a certain
        // user.
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + userName + "\"?",
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
                    DELETE FROM users
                    WHERE user_id = ? AND role = 'Cashier'
                    """;

        // Try...Catch Block
        try (
                // Establishing connection with the database and executing the sql query.
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            // Set the ID of the user being deleted.
            stmt.setInt(1, userId);

            // Execute DELETE Statement
            int rowsUpdated = stmt.executeUpdate();

            // If statement which ensures that the update being performed on the query returns more than 0 rows.
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cashier Account Deleted Successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Refresh the table by calling the loadUsers method.
                loadUsers(model);

                // Clear the form by calling the clearUser method.
                clearUser();
                // Dialog which displays an error if the results of the query returns 0 rows.
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Cashier Account could not be found.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Deleting User: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Private method used to perform a search query on the Users table.
    private void searchUsers(String searchString) {
        // Remove existing table rows and make the table empty.
        model.setRowCount(0);

        // SQL Search Query.
        String sql = """
                SELECT user_id, username, full_name, role
                FROM users
                WHERE role = 'Cashier' AND (
                    username LIKE ? OR full_name LIKE ?
                )
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

            // Executing the query and returning the result set.
            ResultSet rs = stmt.executeQuery();

            // While loop which runs through the results.
            while (rs.next()) {
                // Converts each result and its associated data into columns to be inserted into table.
                Object[] row = {
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role")
                };

                // All columns are combined to create a single record.
                model.addRow(row);
            }

            // Check whether a result was found.
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No Cashier Accounts found matching: " + searchString,
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        // SQL Error Handling.
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error Searching Users: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // User Panel
    public UserPanel() {
        // Custom Color used as the default primary colour theme.
        Color customColor = new Color(208, 238, 228);

        // Setting out the layout of the UserPanel.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(customColor);
        this.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 10));

        // Heading Panel used to contain the title and description.
        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(customColor);
        heading.setMaximumSize(new Dimension(750, 60));

        // Title Component.
        JLabel userTitle = new JLabel("User Management");
        userTitle.setFont(new Font("Arial", Font.BOLD, 24));
        userTitle.setForeground(new Color(0, 204, 153));

        // Description component.
        JLabel userDesc = new JLabel("Create, Read, Update and Delete Users");
        userDesc.setFont(new Font("Arial", Font.BOLD, 18));

        // Adding the two components and vertical spacing between each other.
        heading.add(userTitle);
        heading.add(Box.createVerticalStrut(7));
        heading.add(userDesc);

        // Adding the Heading Panel to the User Panel.
        this.add(heading);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(10));

        // Horizontal Line Component.
        JSeparator sep1 = new JSeparator();
        sep1.setBackground(Color.GRAY);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the component to the User Panel.
        this.add(sep1);

        // User Label component.
        JLabel userLbl = new JLabel("User Details");
        userLbl.setFont(new Font("Arial", Font.BOLD, 18));
        userLbl.setForeground(Color.RED);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(15));

        // Adding the component to the User Panel.
        this.add(userLbl);

        // User Form Panel which contains all form input components.
        JPanel userForm = new JPanel();
        userForm.setLayout(new GridLayout(5, 2, 20, 10));
        userForm.setBackground(customColor);
        userForm.setPreferredSize(new Dimension(1300, 180));
        userForm.setMaximumSize(new Dimension(1300, 180));
        userForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Creating the associated labels and components for each field.
        JLabel usernameLbl = new JLabel("Username: ");
        usernameLbl.setFont(new Font("Arial", Font.BOLD, 18));

        username = new JTextField();
        username.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel userFullNameLbl = new JLabel("Full Name: ");
        userFullNameLbl.setFont(new Font("Arial", Font.BOLD, 18));

        user_full_name = new JTextField();
        user_full_name.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel userRoleLbl = new JLabel("Role: ");
        userRoleLbl.setFont(new Font("Arial", Font.BOLD, 18));

        user_role = new JTextField();
        user_role.setFont(new Font("Arial", Font.BOLD, 18));
        user_role.setText("Cashier");
        user_role.setEditable(false);

        JLabel userPasswordLbl = new JLabel("Password: ");
        userPasswordLbl.setFont(new Font("Arial", Font.BOLD, 18));

        user_password = new JPasswordField();
        user_password.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel userConfirmPasswordLbl = new JLabel("Confirm Password: ");
        userConfirmPasswordLbl.setFont(new Font("Arial", Font.BOLD, 18));

        user_confirm_password = new JPasswordField();
        user_confirm_password.setFont(new Font("Arial", Font.BOLD, 18));

        // Adding all components and their associated labels to the User Form Panel.
        userForm.add(usernameLbl);
        userForm.add(username);

        userForm.add(userFullNameLbl);
        userForm.add(user_full_name);

        userForm.add(userRoleLbl);
        userForm.add(user_role);

        userForm.add(userPasswordLbl);
        userForm.add(user_password);

        userForm.add(userConfirmPasswordLbl);
        userForm.add(user_confirm_password);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(20));

        // Adding the User Form Panel to the User Panel.
        this.add(userForm);

        // CRUD Panel used to add all CRUD Buttons
        JPanel crudPanelUser = new JPanel();
        crudPanelUser.setLayout(new FlowLayout(FlowLayout.LEFT));
        crudPanelUser.setBackground(customColor);
        crudPanelUser.setPreferredSize(new Dimension(900, 50));
        crudPanelUser.setMaximumSize(new Dimension(900, 50));
        crudPanelUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Creating the associated labels and components for each field.
        add_user = new JButton("Add Cashier");
        add_user.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the addUser method when clicked.
        add_user.addActionListener(e -> {
            addUser();
        });

        update_user = new JButton("Update Account");
        update_user.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the updateUser method when clicked.
        update_user.addActionListener(e -> {
            updateUser();
        });

        reset_user = new JButton("Reset Password");
        reset_user.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the resetUser method when clicked.
        reset_user.addActionListener(e -> {
            resetUser();
        });

        delete_user = new JButton("Delete Account");
        delete_user.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the deleteUser method when clicked.
        delete_user.addActionListener(e -> {
            deleteUser();
        });

        clear_user = new JButton("Clear Details");
        clear_user.setFont(new Font("Arial", Font.BOLD, 15));

        // Calling the clearUser method when clicked.
        clear_user.addActionListener(e -> {
            clearUser();
        });

        // Adding all buttons to the CRUD Panel.
        crudPanelUser.add(add_user);
        crudPanelUser.add(update_user);
        crudPanelUser.add(reset_user);
        crudPanelUser.add(delete_user);
        crudPanelUser.add(clear_user);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(25));

        // Adding the CRUD Panel to the User Panel.
        this.add(crudPanelUser);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(10));

        // Creating the Horizontal Line.
        JSeparator sep2 = new JSeparator();
        sep2.setBackground(Color.GRAY);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Adding the separator to the User Panel.
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

        search_user = new JButton("Search");
        search_user.setFont(new Font("Arial", Font.BOLD, 15));

        // Performing Search Operation when the button is clicked.
        search_user.addActionListener(e -> {
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
            // Call the searchUsers method if found.
            searchUsers(searchText);
        });

        refresh_user = new JButton("Refresh");
        refresh_user.setFont(new Font("Arial", Font.BOLD, 15));

        // Performing Refresh Operation when the button is clicked.
        refresh_user.addActionListener(e -> {
            // Setting search field to null and calling the associated methods.
            search.setText("");

            loadUsers(model);
        });

        // Adding components to searchPanel.
        searchPanel.add(searchLbl);
        searchPanel.add(search);
        searchPanel.add(search_user);
        searchPanel.add(refresh_user);

        // Adding Search Panel to the User Panel.
        this.add(searchPanel);

        // Creating Table Panel used to contain the table.
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.GREEN);
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // Creating columns for the table.
        String[] columns = {
                "ID",
                "Username",
                "Full Name",
                "Role"
        };

        // Creating the Table Model.
        model = new DefaultTableModel(columns, 0);

        // Initializing the table.
        userTable = new JTable(model);
        userTable.setBackground(Color.RED);
        userTable.setRowHeight(28);
        userTable.setFont(new Font("Arial", Font.BOLD, 14));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // When a row is selected, its information is loaded into the form.
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = userTable.getSelectedRow();

                // If row is selected, assign table row values to variables and assign these variables to the components.
                if (selectedRow != -1) {
                    String user_name = model.getValueAt(selectedRow, 1).toString();
                    String fullname = model.getValueAt(selectedRow, 2).toString();
                    String role = model.getValueAt(selectedRow, 3).toString();

                    username.setText(user_name);
                    user_full_name.setText(fullname);
                    user_role.setText(role);

                    user_password.setText("");
                    user_confirm_password.setText("");
                }
            }
        });

        // Calling the loadUsers method based on the model.
        loadUsers(model);

        // Creating scroll pane for the table
        JScrollPane supScrollPane = new JScrollPane(userTable);

        // Adding scroll pane to the Table Panel.
        tablePanel.add(supScrollPane, BorderLayout.CENTER);

        // Adding vertical spacing.
        this.add(Box.createVerticalStrut(25));

        // Adding the Table Panel to the Supplier Panel.
        this.add(tablePanel);
    }
}