// Importing libraries to utilize in Cashier Dashboard.
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// CashierDashboard Class
public class CashierDashboard extends JFrame {
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

    // Public Method used to run the Admin Dashboard
    public CashierDashboard(int cashierId) {
        // Setting the parameters of the Admin window.
        setTitle("Cashier Dashboard");
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
        JLabel title = new JLabel("Cashier Dashboard");
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
        sidebarPanel.add(Box.createVerticalStrut(100));

        // Creating a Java Button which will be the Point of Sale tab.
        JButton posBtn = new JButton("Point of Sale");
        posBtn.setFont(new Font("Arial", Font.BOLD, 20));
        posBtn.setForeground(Color.WHITE);
        posBtn.setBackground(new Color(0, 204, 153));
        posBtn.setMaximumSize(new Dimension(200, 40));
        posBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calling the Button Hover Effect method on this button.
        addHoverEffect(posBtn);

        // Creating a Java Button which will be the Stock Check tab.
        JButton stockBtn = new JButton("Stock Check");
        stockBtn.setFont(new Font("Arial", Font.BOLD, 20));
        stockBtn.setForeground(Color.WHITE);
        stockBtn.setBackground(new Color(0, 204, 153));
        stockBtn.setMaximumSize(new Dimension(200, 40));
        stockBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calling the Button Hover Effect method on this button.
        addHoverEffect(stockBtn);

        // Adding the buttons to the Sidebar Panel along with vertical spacing between each button.
        sidebarPanel.add(posBtn);
        sidebarPanel.add(Box.createVerticalStrut(55));
        sidebarPanel.add(stockBtn);

        // Adding vertical spacing.
        sidebarPanel.add(Box.createVerticalStrut(295));

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

            // Close the Cashier Dashboard.
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

        // Creating a cardlayout to make it easier to display different content within the same Cashier Dashboard window.
        CardLayout cardLayout = new CardLayout();

        // Creating the Content Panel which will be stored in the cardlayout.
        JPanel contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(customColor);

        // Creating an instance of the POS Panel
        POSPanel posPanel = new POSPanel(cashierId);

        // Creating an instance of the Stock Check Panel
        StockCheckPanel stockCheckPanel = new StockCheckPanel();

        // Adding each panel to the card layout.
        contentPanel.add(posPanel, "Point of Sale");
        contentPanel.add(stockCheckPanel, "Stock Check");

        // Connecting the sidebar buttons to each panel and displaying the associated panels.
        posBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Point of Sale");
        });

        stockBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Stock Check");
        });

        cardLayout.show(contentPanel, "Point of Sale");

        // Adding both the Sidebar Panel and Content Panel to the Content Pane.
        contentPane.add(sidebarPanel, BorderLayout.WEST);
        contentPane.add(contentPanel, BorderLayout.CENTER);
    }
}