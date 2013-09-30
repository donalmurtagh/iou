package iou.gui;

import iou.controller.Controller;
import iou.controller.Factory;
import iou.enums.User;
import iou.util.GuiUtils;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class LoginFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = -3157372084289283925L;

    private static final Logger LOGGER = Logger.getLogger(LoginFrame.class);

    private JPasswordField passwordField;

    private JRadioButton annButton;

    private void doLogin() {

        // Change the cursor to an hourglass
        GuiUtils.changeCursor(this, Cursor.WAIT_CURSOR);
        try {

            String username = User.BOB.getUsername();
            String password = new String(passwordField.getPassword());

            if (annButton.isSelected()) {
                username = User.ANN.getUsername();
            }

            Controller controller = Factory.getController();
            LOGGER.debug("logging in with username: " + username);

            if (controller.login(username, password)) {

                // Close this window and open the main window instead
                this.dispose();
                new MainFrame();


            } else {
                JOptionPane.showMessageDialog(this, "Login failed. Likely causes:\n"
                        + "- Password typed incorrectly\n" + "- Database is not running",
                        "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            // Change the cursor back
            GuiUtils.changeCursor(this, Cursor.DEFAULT_CURSOR);
        }
    }

    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginFrame inst = new LoginFrame();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }

    public LoginFrame() {
        super("Login to IOU");
        this.setResizable(false);
        initGUI();
    }

    private void initGUI() {
        try {
            GuiUtils.loadApplicationImage(this);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JPanel jPanel3 = new JPanel();
            getContentPane().add(jPanel3, BorderLayout.SOUTH);
            jPanel3.setPreferredSize(new java.awt.Dimension(392, 44));

            JButton loginButton = new JButton();
            loginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doLogin();
                }
            });

            jPanel3.add(loginButton);
            loginButton.setText("Login");

            // The login button handles the enter key press
            getRootPane().setDefaultButton(loginButton);

            JButton cancelButton = new JButton();
            jPanel3.add(cancelButton);
            cancelButton.setText("Cancel");

            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

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
        } catch (Exception e) {
            LOGGER.error("Error initialising login window", e);
        }
    }

}