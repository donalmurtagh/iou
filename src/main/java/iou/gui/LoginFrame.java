package iou.gui;

import iou.beans.TransactionService;
import iou.enums.User;
import iou.util.GuiUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.jdesktop.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

/**
 * The login dialog box
 */
public class LoginFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFrame.class);

    private JPasswordField passwordField;

    private JRadioButton annButton;

    public LoginFrame() {
        super("Login to IOU");
        this.setResizable(false);
        initGUI();
    }

    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame inst = new LoginFrame();
            inst.setLocationRelativeTo(null);
            inst.setVisible(true);
        });
    }

    private void doLogin() {
        GuiUtils.doWithWaitCursor(this, () -> {
            try (var applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml")) {
                User currentUser = annButton.isSelected() ? User.ANN : User.BOB;
                String username = currentUser.getUsername();
                String password = new String(passwordField.getPassword());

                TransactionService transactionService = applicationContext.getBean(TransactionService.class);
                BasicDataSource dataSource = applicationContext.getBean(BasicDataSource.class);
                dataSource.setUsername(username);
                dataSource.setPassword(password);

                LOGGER.debug("Attempting to login with username: {}", username);

                try {
                    transactionService.login();

                    // Close this window and open the main window instead
                    dispose();
                    new MainFrame(transactionService);

                } catch (Exception ex) {
                    LOGGER.error("Login failed for user: {}", username, ex);
                    JOptionPane.showMessageDialog(this, """
                            Login failed. Likely causes:
                            - Password typed incorrectly
                            - MySQL is not running
                            - Database is not initialized""",
                        "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void initGUI() {
        GuiUtils.loadApplicationImage(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel jPanel3 = new JPanel();
        getContentPane().add(jPanel3, BorderLayout.SOUTH);
        jPanel3.setPreferredSize(new java.awt.Dimension(392, 44));

        JButton loginButton = new JButton();
        loginButton.addActionListener(e -> doLogin());

        jPanel3.add(loginButton);
        loginButton.setText("Login");

        // The login button handles the enter key press
        getRootPane().setDefaultButton(loginButton);

        JButton cancelButton = new JButton();
        jPanel3.add(cancelButton);
        cancelButton.setText("Cancel");

        cancelButton.addActionListener(e -> System.exit(0));

        JPanel jPanel1 = new JPanel();
        getContentPane().add(jPanel1, BorderLayout.NORTH);
        jPanel1.setPreferredSize(new java.awt.Dimension(392, 37));

        annButton = new JRadioButton();
        jPanel1.add(annButton);
        annButton.setText(User.ANN.getName());

        JRadioButton bobButton = new JRadioButton();
        jPanel1.add(bobButton);
        bobButton.setText(User.BOB.getName());
        bobButton.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(annButton);
        group.add(bobButton);

        JPanel jPanel2 = new JPanel();
        getContentPane().add(jPanel2, BorderLayout.CENTER);
        jPanel2.setPreferredSize(new java.awt.Dimension(392, 65));

        JLabel passwordLabel = new JLabel();
        jPanel2.add(passwordLabel);
        passwordLabel.setText("Enter Password:");

        passwordField = new JPasswordField();
        jPanel2.add(passwordField);
        passwordField.setName("passwordField");
        passwordField.setPreferredSize(new java.awt.Dimension(161, 21));

        pack();
        this.setSize(400, 153);
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
    }
}
