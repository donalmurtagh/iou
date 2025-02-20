package iou.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import iou.enums.TranDialogMode;
import iou.enums.User;
import iou.model.Transaction;
import iou.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.text.ParseException;
import java.util.Date;


public class TransactionDialog extends JDialog {

    private JTextField descField;

    private static final float MAX_AMOUNT = 10000;

    private JFormattedTextField dateField;

    private final Transaction tran;

    // TODO Add decimal masks to these fields
    private final JTextField bobField = new JTextField();

    private final JTextField annField = new JTextField();

    private boolean isValidTransaction = false;

    private final TranDialogMode mode;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDialog.class);

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
        if (StringUtils.isBlank(annField.getText()) && StringUtils.isBlank(bobField.getText())) {

            String missingPayeeMsg = String.format("Please enter an amount in either '%s Paid' or '%s Paid'",
                    User.ANN.getName(),
                    User.BOB.getName());
            JOptionPane.showMessageDialog(this, missingPayeeMsg, "Missing Amount", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (StringUtils.isNotBlank(dateField.getText())) {
                LOGGER.debug("Date field contains text: {}", dateField.getText());
                Date parsedDate = DateUtils.string2Date(dateField.getText());
                LOGGER.debug("Parsed date is: {}", parsedDate);

                tran.setDate(DateUtils.string2Date(dateField.getText()));
            }
            tran.setDescription(descField.getText());
            tran.setAnnPaid(validateAmount(annField.getText()));
            tran.setBobPaid(validateAmount(bobField.getText()));

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
            this.setResizable(true);
            getContentPane().setLayout(new BorderLayout());

            // Create a border so the OK and Cancel buttons don't sit right on
            // the bottom of the window
            getRootPane().setBorder(BorderFactory.createLineBorder(getBackground(), 5));

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            JPanel buttonPanel = new JPanel();
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            buttonPanel.setPreferredSize(new java.awt.Dimension(292, 31));

            JButton okButton = new JButton();
            buttonPanel.add(okButton);
            okButton.setText("OK");
            okButton.addActionListener(e -> {

                // If the transaction is valid set the status and close the window. If it's not valid a message box
                // should pop up and the user will be able to fix the problem
                if (updateTransaction()) {
                    close(true);
                }
            });
            JButton cancelButton = new JButton();
            buttonPanel.add(cancelButton);
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(e -> close(false));

            JPanel formPanel = new JPanel();

            FormLayout formLayout = new FormLayout(
                    // columns, rows
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)",
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)");

            formPanel.setLayout(formLayout);
            getContentPane().add(formPanel, BorderLayout.CENTER);

            JLabel dateLabel = new JLabel();
            formPanel.add(dateLabel, new CellConstraints("2, 2, 1, 1, default, default"));
            dateLabel.setText("Date:");

            formPanel.add(dateField, new CellConstraints("4, 2, 1, 1, default, default"));
            dateField.setName("dateField");
            dateField.setPreferredSize(new java.awt.Dimension(174, 21));
            dateField.setText(DateUtils.date2String(tran.getDate()));

            JLabel descLabel = new JLabel();
            formPanel.add(descLabel, new CellConstraints("2, 4, 1, 1, default, default"));
            descLabel.setText("Description:");

            descField = new JTextField();
            formPanel.add(descField, new CellConstraints("4, 4, 1, 1, default, default"));
            descField.setText(tran.getDescription());

            formPanel.add(createPersonLabel(User.ANN.getName() + " Paid:"), new CellConstraints("2, 6, 1, 1, default, default"));

            formPanel.add(annField, new CellConstraints("4, 6, 1, 1, default, default"));
            annField.setName("annField");
            if (tran.getAnnPaid() != 0) {
                annField.setText(String.valueOf(tran.getAnnPaid()));
            }

            formPanel.add(bobField, new CellConstraints("4, 8, 1, 1, default, default"));
            bobField.setName("bobField");
            if (tran.getBobPaid() != 0) {
                bobField.setText(String.valueOf(tran.getBobPaid()));
            }

            formPanel.add(createPersonLabel(User.BOB.getName() + " Paid:"), new CellConstraints("2, 8, 1, 1, default, default"));

            // Add listeners to ensure that text can only be entered in one of the amount
            // fields when adding or updating a payment
            if (this.mode == TranDialogMode.ADD_PAYMENT || this.mode == TranDialogMode.UPDATE_PAYMENT) {

                annField.getDocument().addDocumentListener(new PaymentAmountFieldListener(bobField));
                bobField.getDocument().addDocumentListener(new PaymentAmountFieldListener(annField));
            }

            pack();
            this.setSize(450, 214);
            Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
        } catch (Exception e) {
            LOGGER.error("Error initializing dialog", e);
        }
    }

    private JLabel createPersonLabel(String text) {

        JLabel personLabel = new JLabel();
        personLabel.setText(text);
        personLabel.setMaximumSize(new Dimension(200, 16));
        personLabel.setToolTipText(text);
        return personLabel;
    }

    /**
     * Ensures that only text may be entered in one of the amount fields when adding/editing
     * a payment
     *
     * @author Donal
     */
    private static class PaymentAmountFieldListener implements DocumentListener {

        private final JTextField otherAmountField;

        private PaymentAmountFieldListener(JTextField otherAmountField) {
            this.otherAmountField = otherAmountField;
        }

        @Override
        public void changedUpdate(DocumentEvent e) { }

        @Override
        public void insertUpdate(DocumentEvent e) {
            otherAmountField.setText("");
        }

        @Override
        public void removeUpdate(DocumentEvent e) { }
    }
}
