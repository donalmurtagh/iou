package iou.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import iou.enums.TranDialogMode;
import iou.enums.User;
import iou.model.Transaction;
import iou.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;


public class TransactionDialog extends javax.swing.JDialog {
    private static final long serialVersionUID = 7474907167576126317L;

    private JPanel btnPanel;

    private JTextField descField;

    private JLabel bobLabel;

    private static final float MAX_AMOUNT = 10000;

    private JLabel dateLabel;

    private JFormattedTextField dateField;

    private JLabel descLabel;

    private JPanel jPanel2;

    private JButton cancelButton;

    private JButton okButton;

    private Transaction tran;

    // TODO Add decimal masks to these fields
    private JTextField bobField = new JTextField();

    private JTextField annField = new JTextField();

    private boolean isValidTransaction = false;

    private TranDialogMode mode;

    private static final Logger LOGGER = Logger.getLogger(TransactionDialog.class);

    /**
     * Shows this dialog in modal mode
     *
     * @param owner
     * @param mode
     * @param tran
     */
    public TransactionDialog(Frame owner, TranDialogMode mode, Transaction tran) {
        super(owner, mode.getTitle(), true);


        try {
            // TODO This mask will accept something like '44/13/07' which is not a valid date
            dateField = new JFormattedTextField(new MaskFormatter("##/##/##"));

        } catch (ParseException e) {

            // If there's a problem masking the input fields just carry on because the
            // input will be validated when "OK" is pressed
            LOGGER.warn("Error masking input field", e);
        }

        this.mode = mode;
        this.tran = tran;
        initGUI();
    }

    public Transaction getTransaction() {
        return this.tran;
    }

    /**
     * Close the dialog and set the validity of the transaction object
     *
     * @param isValid
     */
    private void close(boolean isValid) {
        this.isValidTransaction = isValid;
        this.dispose();
    }

    public boolean isValidTransaction() {
        return this.isValidTransaction;
    }

    /**
     * Updates the transaction from the values in the component fields
     *
     * @return true if the updated transaction is valid, otherwise false
     */
    private boolean updateTransaction() {

        // Check that at least one amount has been provided
        if (StringUtils.isBlank(annField.getText())
                && StringUtils.isBlank(bobField.getText())) {

            JOptionPane.showMessageDialog(this,
                    "Please enter an amount in either 'Maude Paid' or 'Donal Paid'",
                    "Missing Amount", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {

            if (StringUtils.isNotBlank(dateField.getText())) {
                LOGGER.debug("Date field contains text: " + dateField.getText());
                Date parsedDate = DateUtils.string2Date(dateField.getText());
                LOGGER.debug("Parsed date is: " + parsedDate);

                tran.setDate(DateUtils.string2Date(dateField.getText()));
            }
            tran.setDescription(descField.getText());
            tran.setMaudePaid(validateAmount(annField.getText()));
            tran.setDonalPaid(validateAmount(bobField.getText()));

            return true;

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, dateField.getText()
                    + " is not a valid date.\n"
                    + "If a date is entered, it must be in the format 'dd/MM/yy'.",
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (NumberFormatException e) {
            LOGGER.info("Number validation failed", e);

            JOptionPane.showMessageDialog(this,
                    "Please enter a valid decimal greater than 0 and less than " + MAX_AMOUNT
                            + " in the amount field(s)", "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static float validateAmount(String amountStr) {

        if (StringUtils.isBlank(amountStr)) {
            return 0;
        }

        float amount = Float.parseFloat(amountStr);

        if (amount <= 0 || amount >= MAX_AMOUNT) {
            throw new NumberFormatException(amountStr + " is out of range");
        }
        return amount;
    }

    private void initGUI() {
        try {
            this.setResizable(false);
            BorderLayout thisLayout = new BorderLayout();
            getContentPane().setLayout(thisLayout);

            // Create a border so the OK and Cancel buttons don't sit right on
            // the bottom of the window
            getRootPane().setBorder(BorderFactory.createLineBorder(getBackground(), 5));

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            btnPanel = new JPanel();
            getContentPane().add(btnPanel, BorderLayout.SOUTH);
            btnPanel.setPreferredSize(new java.awt.Dimension(292, 31));

            okButton = new JButton();
            btnPanel.add(okButton);
            okButton.setText("OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    // If the transaction is valid set the status and close
                    // the
                    // window. If it's not valid a message box should pop up
                    // and the user will be able to fix the problem
                    if (updateTransaction()) {
                        close(true);
                    }
                }
            });
            cancelButton = new JButton();
            btnPanel.add(cancelButton);
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close(false);
                }
            });

            jPanel2 = new JPanel();

            FormLayout jPanel2Layout = new FormLayout(
                    // columns, rows
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)",
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)");

            jPanel2.setLayout(jPanel2Layout);
            getContentPane().add(jPanel2, BorderLayout.CENTER);

            dateLabel = new JLabel();
            jPanel2.add(dateLabel, new CellConstraints("2, 2, 1, 1, default, default"));
            dateLabel.setText("Date:");

            jPanel2.add(dateField, new CellConstraints("4, 2, 1, 1, default, default"));
            dateField.setName("dateField");
            dateField.setPreferredSize(new java.awt.Dimension(174, 21));
            dateField.setText(DateUtils.date2String(tran.getDate()));

            descLabel = new JLabel();
            jPanel2.add(descLabel, new CellConstraints("2, 4, 1, 1, default, default"));
            descLabel.setText("Description:");

            descField = new JTextField();
            jPanel2.add(descField, new CellConstraints("4, 4, 1, 1, default, default"));
            descField.setText(tran.getDescription());

            JLabel annLabel = new JLabel();
            jPanel2.add(annLabel, new CellConstraints("2, 6, 1, 1, default, default"));
            annLabel.setText(User.ANN.getName() + " Paid:");

            jPanel2.add(annField, new CellConstraints("4, 6, 1, 1, default, default"));
            annField.setName("annField");
            if (tran.getAnnPaid() != 0) {
                annField.setText(String.valueOf(tran.getAnnPaid()));
            }

            jPanel2.add(bobField, new CellConstraints("4, 8, 1, 1, default, default"));
            bobField.setName("bobField");
            if (tran.getBobPaid() != 0) {
                bobField.setText(String.valueOf(tran.getBobPaid()));
            }

            bobLabel = new JLabel();
            jPanel2.add(bobLabel, new CellConstraints("2, 8, 1, 1, default, default"));
            bobLabel.setText(User.BOB.getName() + " Paid:");

            // Add listeners to ensure that text can only be entered in one of the amount
            // fields when adding or updating a payment
            if (this.mode == TranDialogMode.ADD_PAYMENT
                    || this.mode == TranDialogMode.UPDATE_PAYMENT) {

                annField.getDocument().addDocumentListener(
                        new PaymentAmountFieldListener(bobField));
                bobField.getDocument().addDocumentListener(
                        new PaymentAmountFieldListener(annField));
            }

            pack();
            this.setSize(300, 214);
            Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures that only text may be entered in one of the amount fields when adding/editing
     * a payment
     *
     * @author Donal
     */
    private static class PaymentAmountFieldListener implements DocumentListener {

        private JTextField otherAmountField;

        private PaymentAmountFieldListener(JTextField otherAmountField) {
            this.otherAmountField = otherAmountField;
        }

        public void changedUpdate(DocumentEvent e) {
            // Empty on purpose
        }

        public void insertUpdate(DocumentEvent e) {
            otherAmountField.setText("");
        }

        public void removeUpdate(DocumentEvent e) {
            // Empty on purpose
        }
    }
}
