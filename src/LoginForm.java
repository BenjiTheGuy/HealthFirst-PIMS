// Importing libraries to utilize in login form.
import javax.swing.*;
import java.awt.*;

import database.DatabaseConnection;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Class which creates the Java Login Form Interface.
public class LoginForm extends JFrame {
    // Creating private variables and components to ensure security by preventing these variables from being accessed
    // outside the Login Form.
    private JTextField usernameTF;
    private JPasswordField passwordFld;
    private String username, password, fullName, role;
    private int userID;

    // Private method used to add color changes to buttons upon moving the mouse in and out of the specified button.
    private void addHoverEffect(JButton button) {
        // Action Event on the Button based on movement of the mouse.
        button.addMouseListener(new MouseAdapter() {
            // Button changes colour everytime the mouse hovers over it.
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.BLACK);
                button.setBackground(new Color(11, 255, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(0, 204, 153));
            }
        });
    }

    // Creating the class for the Login form.
    public LoginForm() {
        // Setting parameters for the frame.
        setTitle("HealthFirst - Login Form");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creating a custom color to be used in the background.
        Color customColor = new Color(208, 238, 228);

        // Main Container which will be used to add all panels and adjust each of their positions accordingly in a box
        // layout.
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBackground(customColor);

        // Creating the Title Panel which will contain the Icon and Company Name. The panel is also adjusted accordingly.
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setMaximumSize(new Dimension(600, 60));
        titlePanel.setBackground(customColor);

        // Logo Icon to include in the Login form by being added to a label and adjusting its position in the Main Panel.
        ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
        Image scaledImg = icon.getImage().getScaledInstance(175, 135, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        // Adding the Logo Icon to the JLabel component and aligning its position within the form.
        JLabel iconLbl = new JLabel(scaledIcon);
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Creating, Modifying the Font, and Aligning the Title label to the form.
        JLabel titleLbl = new JLabel("HealthFirst Pharmacy");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 24));
        titleLbl.setForeground(new Color(0, 204, 153));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding the two Java components to the Title Panel.
        titlePanel.add(iconLbl);
        titlePanel.add(titleLbl);

        // Adding the Title Panel to the Content Pane.
        contentPane.add(titlePanel);

        // Creating a Subtitle Panel which will contain both the message and slogan components. It is adjusted based on
        // the parameters set.
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setLayout(new GridLayout(2, 1));
        subtitlePanel.setMaximumSize(new Dimension(600, 60));
        subtitlePanel.setBackground(customColor);

        // Creating the Message component.
        JLabel messageLbl = new JLabel("Sign in to your console", SwingConstants.CENTER);
        messageLbl.setFont(new Font("Arial", Font.PLAIN, 18));

        // Creating the Slogan component.
        JLabel sloganLbl = new JLabel("Enterprise Clinical Inventory & Cold Chain Management",
                SwingConstants.CENTER);
        sloganLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        sloganLbl.setForeground(Color.GRAY);

        // Adding the components to the Subtitle Panel.
        subtitlePanel.add(messageLbl);
        subtitlePanel.add(sloganLbl);

        // Adding the Subtitle Panel to the Content Pane.
        contentPane.add(subtitlePanel);

        // Adding vertical spacing between the Subtitle Panel and the Form Panel.
        contentPane.add(Box.createVerticalStrut(30));

        // Form panel which includes all input fields and associated labels and buttons.
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 0, 10));
        formPanel.setMaximumSize(new Dimension(450, 300));
        formPanel.setBackground(customColor);

        // Creating the username labels and input fields.
        JLabel usernameLbl = new JLabel("Username");
        usernameLbl.setFont(new Font("Arial", Font.BOLD, 18));

        usernameTF = new JTextField(15);
        usernameTF.setPreferredSize(new Dimension(150, 25));

        // Creating the password labels and input fields.
        JLabel passwordLbl = new JLabel("Password");
        passwordLbl.setFont(new Font("Arial", Font.BOLD, 18));

        passwordFld = new JPasswordField();
        passwordFld.setPreferredSize(new Dimension(150, 25));

        // Adding the username and password fields and labels to the Form Panel.
        formPanel.add(usernameLbl);
        formPanel.add(usernameTF);

        formPanel.add(passwordLbl);
        formPanel.add(passwordFld);

        // Adding the Form Panel to the Content Pane.
        contentPane.add(formPanel);

        // Adding vertical spacing between the Form Panel and the Extra Panel.
        contentPane.add(Box.createVerticalStrut(5));

        // Extra Panel which will be used to contain additional functionality such as forget password and sign in feature.
        JPanel extraPanel = new JPanel();
        extraPanel.setLayout(new GridLayout(1, 2, 155, 0));
        extraPanel.setMaximumSize(new Dimension(450, 300));
        extraPanel.setBackground(customColor);

        // Checkbox used to ensure the user remains signed in.
        JCheckBox signedIn = new JCheckBox("Keep me signed in");
        signedIn.setSelected(false);
        signedIn.setBackground(customColor);

        // Label component used to determine if the user has forgotten their password.
        JLabel forgotPasswordLbl = new JLabel("Forgot Password?");
        forgotPasswordLbl.setFont(new Font("Arial", Font.BOLD, 16));
        forgotPasswordLbl.setForeground(new Color(0, 204, 153));

        // Adding the two components to the Extra Panel.
        extraPanel.add(signedIn);
        extraPanel.add(forgotPasswordLbl);

        // Adding the Extra Panel to the Content Pane.
        contentPane.add(extraPanel);

        // Creating the panel for the JButton component and aligning its position within the frame.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(customColor);

        // Creating the Login Button.
        JButton loginBtn = new JButton("Sign in");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(0, 204, 153));
        loginBtn.setPreferredSize(new Dimension(200, 35));

        // Calling the private method on the Login Button.
        addHoverEffect(loginBtn);

        // Adding the Java Button to the Form panel.
        buttonPanel.add(loginBtn);

        // Adding Functionality to the Login Button.
        loginBtn.addActionListener(e -> {
            // Declaring variables which will extract the input entered by the user.
            username = usernameTF.getText().trim();
            password = new String(passwordFld.getPassword());

            // Logic to determine if fields are empty.
            if (username.isEmpty() || password.isEmpty()) {
                // Dialog Box to display a message to the user indicating failure to successfully log in to dashboard.
                JOptionPane.showMessageDialog(
                        this,
                        "Please Enter Both Username and Password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE
                );
                // Exit the process if the user leaves both fields empty.
                return;
            }

            // SQL Query to be executed which compares the results of the user's input against the credentials stored
            // in the users table. BINARY ensures that both fields are Case-Sensitive in order to ensure security when
            // the user enters the associated fields.
            String sql = """
                    SELECT user_id, username, role, full_name
                    FROM USERS
                    WHERE username = BINARY ? AND password = BINARY ?;
                    """;

            // Try...Catch Statement used to run and execute the SQL Query.
            try (
                    // Connecting to the database and Preparing the Statement which will run the SQL Query.
                    Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement statement = conn.prepareStatement(sql);
            ) {
                // Setting the username value for the first placeholder found in the SQL Query.
                statement.setString(1, username);

                // Setting the password value for the second placeholder found in the SQL Query.
                statement.setString(2, password);

                // Executing the SQL Query and storing the returned results.
                ResultSet rs = statement.executeQuery();

                // If statement which will run through the results of the SQL Query.
                if (rs.next()) {
                    // Obtaining the fullName and Role attributes found within each result.
                    userID = rs.getInt("user_id");
                    fullName = rs.getString("full_name");
                    role = rs.getString("role");

                    // Dialog Box to indicate to the user successful login and redirect the user to the appropriate window.
                    JOptionPane.showMessageDialog(
                            this,
                            "Login Successful! \nWelcome " + fullName + "\nRole: " +
                                    role + "\nRedirecting to " + role + " Dashboard.",
                            "Successful Login",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Determining which Dashboard to open based on the Role of the user.
                    if (role.equals("Admin")) {
                        // Opening the AdminDashboard frame.
                        new AdminDashboard().setVisible(true);
                    } else if (role.equals("Cashier")) {
                        // Opening the CashierDashboard frame.
                        new CashierDashboard(userID).setVisible(true);
                    }

                    // Closing the Login Form.
                    dispose();
                // Else statement in the case of the SQL Query failing to validate the credentials.
                } else {
                    // Dialog Box to indicate the user failed to log in to the dashboard.
                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid Username or Password",
                            "Unsuccessful Login",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                // Catch block used to identify a database connection error.
            } catch (SQLException error) {
                // Dialog box that indicates to the user that the connection to the database failed.
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to Connect to the Database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

                // Print out the error.
                error.printStackTrace();
        }
        });

        // Centralizing the Button Panel.
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding spacing between the Login Button and the end of the Content Pane.
        contentPane.add(Box.createVerticalStrut(40));

        // Adding the Button Panel to the Content Pane.
        contentPane.add(buttonPanel);
    }
}