// Importing libraries to utilize in Admin Dashboard.
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// AdminDashboard Class
public class AdminDashboard extends JFrame {
    // Private Method used to add a hovering effect on the logout button.
    private void addHoverEffectLogout(JButton button) {
        // Action Event on the Button based on movement of the mouse.
        button.addMouseListener(new MouseAdapter() {
            // Button changes colour everytime the mouse hovers over it.
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(0, 204, 153));
            }
        });
    }

    // Private Method used to add a hovering effect on the logout button.
    private void addHoverEffect(JButton button) {
        // Private Method used to add a hovering effect on the logout button.
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

    // Public Method used to run the Admin Dashboard
    public AdminDashboard() {
        // Setting the parameters of the Admin window.
        setTitle("Admin Dashboard");
        setSize(1500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Primary Colour used as part of the theme of the interface.
        Color customColor = new Color(208, 238, 228);

        // Creating a container which will contain all the content to be added onto the window. Parameters are used to
        // set the arrangement of the window.
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(customColor);

        // Creating a Side Panel which will house different functions to the user. Parameters are used to set the
        // arrangement of the panel.
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(300, 800));

        // Creating a Title component to add to the dashboard.
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(0, 204, 153));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo Icon to include in the Login form by being added to a label and adjusting its position in the Main Panel.
        ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
        Image scaledImg = icon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        // Adding the Logo Icon to the JLabel component and aligning its position within the form.
        JLabel iconLbl = new JLabel(scaledIcon);
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding the components to the Sidebar Panel.
        sidebarPanel.add(iconLbl);
        sidebarPanel.add(title);

        // Adding vertical spacing.
        sidebarPanel.add(Box.createVerticalStrut(50));

        // Creating a Java Button which will be the Medicine Management tab.
        JButton medicineBtn = new JButton("Medicines");
        medicineBtn.setFont(new Font("Arial", Font.BOLD, 20));
        medicineBtn.setForeground(Color.WHITE);
        medicineBtn.setBackground(new Color(0, 204, 153));
        medicineBtn.setMaximumSize(new Dimension(150, 40));
        medicineBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calling the Button Hover Effect method on this button.
        addHoverEffect(medicineBtn);

        // Creating a Java Button which will be the Supplier Management tab.
        JButton supplierBtn = new JButton("Suppliers");
        supplierBtn.setFont(new Font("Arial", Font.BOLD, 20));
        supplierBtn.setForeground(Color.WHITE);
        supplierBtn.setBackground(new Color(0, 204, 153));
        supplierBtn.setMaximumSize(new Dimension(150, 40));
        supplierBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calling the Button Hover Effect method on this button.
        addHoverEffect(supplierBtn);

        // Creating a Java Button which will be the Cashier Management tab.
        JButton userBtn = new JButton("Users");
        userBtn.setFont(new Font("Arial", Font.BOLD, 20));
        userBtn.setForeground(Color.WHITE);
        userBtn.setBackground(new Color(0, 204, 153));
        userBtn.setMaximumSize(new Dimension(150, 40));
        userBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calling the Button Hover Effect method on this button.
        addHoverEffect(userBtn);

        // Creating a Java Button which will be the Report Generation tab.
        JButton reportBtn = new JButton("Reports");
        reportBtn.setFont(new Font("Arial", Font.BOLD, 20));
        reportBtn.setForeground(Color.WHITE);
        reportBtn.setBackground(new Color(0, 204, 153));
        reportBtn.setMaximumSize(new Dimension(150, 40));
        reportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calling the Button Hover Effect method on this button.
        addHoverEffect(reportBtn);

        // Adding the buttons to the Sidebar Panel along with vertical spacing between each button.
        sidebarPanel.add(medicineBtn);
        sidebarPanel.add(Box.createVerticalStrut(35));
        sidebarPanel.add(supplierBtn);
        sidebarPanel.add(Box.createVerticalStrut(35));
        sidebarPanel.add(userBtn);
        sidebarPanel.add(Box.createVerticalStrut(35));
        sidebarPanel.add(reportBtn);

        // Adding vertical spacing.
        sidebarPanel.add(Box.createVerticalStrut(225));

        // Creating the logout panel, along with its parameters to determine the arrangement of the panel.
        JPanel logoutPanel = new JPanel();
        logoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setBackground(Color.WHITE);

        // Creating the Logout Button along with its effects.
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 20));
        logoutBtn.setForeground(new Color(0, 204, 153));
        logoutBtn.setMaximumSize(new Dimension(150, 40));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);

        // Calling the Logout Hover Effect method on this button.
        addHoverEffectLogout(logoutBtn);

        // Adding the logout button to the logout panel.
        logoutPanel.add(logoutBtn);

        // Adding the logout panel to the sidebar panel.
        sidebarPanel.add(logoutPanel);

        // Setting up an event action listener on the button.
        logoutBtn.addActionListener(e -> {
            // Create an instance of the login form and display it.
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);

            // Close the Admin Dashboard.
            dispose();
        });

        // Add vertical spacing to the sidebar panel.
        sidebarPanel.add(Box.createVerticalStrut(10));

        // Creating a label component to attach the current date and time.
        JLabel dateLbl = new JLabel();
        dateLbl.setFont(new Font("Arial", Font.BOLD, 14));
        dateLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // DateTime formatter used to set out the layout of the date and time.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy | HH:mm:ss");

        // Creating a timer which will run every second and update the label.
        Timer tmr = new Timer(1000, e -> {
            dateLbl.setText(LocalDateTime.now().format(dtf));
        });

        // Starting the timer.
        tmr.start();

        // Adding the date label to the sidebar panel.
        sidebarPanel.add(dateLbl);

        // Adding vertical spacing.
        sidebarPanel.add(Box.createVerticalStrut(10));

        // Creating a cardlayout to make it easier to display different content within the same Admin Dashboard window.
        CardLayout cardLayout = new CardLayout();

        // Creating the Content Panel which will be stored in the cardlayout.
        JPanel contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(customColor);

        // Creating an instance of the Medicine Panel
        MedicinePanel medicinePanel = new MedicinePanel();

        // Creating an instance of the Supplier Panel
        SupplierPanel supplierPanel = new SupplierPanel();

        // Creating an instance of the User Panel
        UserPanel userPanel = new UserPanel();

        // Creating an instance of the Report Panel
        ReportPanel reportPanel = new ReportPanel();

        // Adding each panel to the card layout.
        contentPanel.add(medicinePanel, "Medicine");
        contentPanel.add(supplierPanel, "Supplier");
        contentPanel.add(userPanel, "User");
        contentPanel.add(reportPanel, "Report");

        // Connecting the sidebar buttons to each panel and displaying the associated panels.
        medicineBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Medicine");
        });

        supplierBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Supplier");
        });

        userBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "User");
        });

        reportBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Report");
        });

        // Adding both the Sidebar Panel and Content Panel to the Content Pane.
        contentPane.add(sidebarPanel, BorderLayout.WEST);
        contentPane.add(contentPanel, BorderLayout.CENTER);
    }
}