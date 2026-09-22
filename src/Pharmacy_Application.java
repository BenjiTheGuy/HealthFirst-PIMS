// Importing libraries to be used in application.
import java.awt.*;
import javax.swing.*;
import java.io.*;

// Main Application where the HealthFirst Application will be run.
public class Pharmacy_Application extends JFrame {
    // Main Method.
    public static void main(String[] args) {
        // Declaring the LoginForm class to be created to enable for it to be run and displayed during runtime.
        LoginForm loginForm = new LoginForm();

        // Displaying the Login Form.
        loginForm.setVisible(true);
    }
}
