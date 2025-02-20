package iou.gui;

import iou.controller.Controller;
import iou.enums.ExpenseField;
import iou.enums.PaymentField;
import iou.enums.TranDialogMode;
import iou.enums.TransactionType;
import iou.enums.User;
import iou.model.Expense;
import iou.model.ExpenseTableModel;
import iou.model.Payment;
import iou.model.PaymentTableModel;
import iou.model.Transaction;
import iou.model.TransactionTableModel;
import iou.util.GuiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Properties;

/**
 * The main application window
 *
 * @author Donal
 */
public class MainFrame extends JFrame {

    private final JButton editPmtButton = new JButton();

    private JButton archiveButton;

    private final JLabel balanceLabel = new JLabel();

    private final JButton addExpButton = new JButton();

    private final JButton deleteExpButton = new JButton();

    private final JButton deletePmtButton = new JButton();

    private final JTable expensesTable = new TransactionTable();

    private final JTable paymentsTable = new TransactionTable();

    private final JButton editExpButton = new JButton();

    private final JButton addPmtButton = new JButton();

    private TransactionTableModel expensesTableModel;

    private final Controller controller;

    private TransactionTableModel paymentsTableModel;

    private NumberFormat currencyFormatter;

    /**
     * If positive, Ann owes Bob, and vice versa
     */
    private float netBobBalance;

    private enum TableUpdateType {
        PAYMENT, EXPENSE, BOTH
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);

    public MainFrame(Controller controller) {
        GuiUtils.changeCursor(this, Cursor.WAIT_CURSOR);
        this.controller = controller;
        initUI();

        try {
            Properties messages = new Properties();
            messages.load(getClass().getResourceAsStream("/config.properties"));
            String currencySymbol = messages.getProperty("currency.symbol");
            currencyFormatter = new DecimalFormat(currencySymbol + "#.00");

            loadData();

        } catch (Exception ex) {
            handleFatalException(ex);

        } finally {
            GuiUtils.changeCursor(this, Cursor.DEFAULT_CURSOR);
        }
    }

    private void loadData() {
        try {
            // Get all the expenses
            List<Transaction> expenses = controller.getTransactions(TransactionType.EXPENSE);
            LOGGER.debug("Retrieved initial list of {} expenses", expenses.size());
            expensesTableModel = new ExpenseTableModel(expenses);
            expensesTable.setModel(expensesTableModel);

            // Get all the payments
            List<Transaction> payments = controller.getTransactions(TransactionType.PAYMENT);
            LOGGER.debug("Retrieved initial list of {} payments", payments.size());
            paymentsTableModel = new PaymentTableModel(payments);
            paymentsTable.setModel(paymentsTableModel);

            // Make the description columns a bit wider than the others
            GuiUtils.setTableColumnWidth(expensesTable, ExpenseField.DESCRIPTION.getIndex(), 170);
            GuiUtils.setTableColumnWidth(paymentsTable, PaymentField.DESCRIPTION.getIndex(), 170);

            updateUI(TableUpdateType.BOTH);

        } catch (RuntimeException ex) {
            handleFatalException(ex);
        }
    }

    private void showUpdatePaymentDialog() {
        // Get the selected expense from the table model
        int selectedRowIndex = paymentsTable.getSelectedRow();
        Transaction selectedPayment = paymentsTableModel.getTransaction(selectedRowIndex);

        TransactionDialog paymentDialog = new TransactionDialog(this,
                TranDialogMode.UPDATE_PAYMENT, selectedPayment);
        GuiUtils.showCentered(paymentDialog);

        if (paymentDialog.isValidTransaction()) {
            persistUpdatedTransaction(selectedRowIndex, paymentDialog.getTransaction());
        }
    }

    private void showAddPaymentDialog() {
        TransactionDialog paymentDialog = new TransactionDialog(this,
                TranDialogMode.ADD_PAYMENT, new Payment());
        GuiUtils.showCentered(paymentDialog);

        if (paymentDialog.isValidTransaction()) {
            persistNewTransaction(paymentDialog.getTransaction());
        }
    }

    /**
     * Open the add expense dialog
     */
    private void showAddExpenseDialog() {
        TransactionDialog expenseDialog = new TransactionDialog(this,
                TranDialogMode.ADD_EXPENSE, new Expense());
        GuiUtils.showCentered(expenseDialog);

        if (expenseDialog.isValidTransaction()) {
            persistNewTransaction(expenseDialog.getTransaction());
        }
    }

    /**
     * Updates the visual state of a table and it's associated edit and delete buttons
     *
     * @param table
     * @param editButton
     * @param deleteButton
     */
    private static void updateTableAndButtonsUI(JTable table, JButton editButton, JButton deleteButton) {
        table.updateUI();
        boolean rowsExist = (table.getModel().getRowCount() > 0);
        deleteButton.setEnabled(rowsExist);
        editButton.setEnabled(rowsExist);
        GuiUtils.selectLastRow(table);
    }

    /**
     * Updates the table(s), the enabled state of the buttons and recalculates the balance
     *
     * @param updateType Indicates which set of transactions has changed
     */
    private void updateUI(TableUpdateType updateType) {
        // Unless only the payments have changed, update the expenses table
        if (updateType != TableUpdateType.PAYMENT) {
            updateTableAndButtonsUI(expensesTable, editExpButton, deleteExpButton);
        }

        // Unless only the expenses have changed, update the payments table
        if (updateType != TableUpdateType.EXPENSE) {
            updateTableAndButtonsUI(paymentsTable, editPmtButton, deletePmtButton);
        }

        // Enable the archive button if there is anything more than just a single payment
        // or just a single expense. This will prevent the user from doing one archive
        // immediately after another
        int paymentsTotal = paymentsTable.getRowCount();
        int expensesTotal = expensesTable.getRowCount();
        boolean enableArchive = paymentsTotal > 1 || expensesTotal > 1 ||
                (paymentsTotal > 0 && expensesTotal > 0);
        archiveButton.setEnabled(enableArchive);

        // The balance needs to be recalculated if either an expense or payment has changed
        updateNetBalance();

        if (netBobBalance > 0) {
            String annOwesBob = String.format("%s owes %s %s",
                    User.ANN.getName(),
                    User.BOB.getName(),
                    formatDecimal(netBobBalance));
            balanceLabel.setText(annOwesBob);

        } else if (netBobBalance < 0) {

            // A negative balance indicates Bob owes Ann, but it will be shown as a positive amount
            String bobOwesAnn = String.format("%s owes %s %s",
                    User.BOB.getName(),
                    User.ANN.getName(),
                    formatDecimal(-netBobBalance));

            balanceLabel.setText(bobOwesAnn);

        } else {
            balanceLabel.setText("Nothing owed");
        }

        balanceLabel.setToolTipText(balanceLabel.getText());
    }

    private String formatDecimal(Float decimal) {
        return currencyFormatter.format(decimal.doubleValue());
    }

    /**
     * Calculate the overall amount owed to Bob or owed by Bob
     */
    private void updateNetBalance() {
        float bobExpenseBalance = 0;

        for (Transaction expense : expensesTableModel.GetAllTransactions()) {
            bobExpenseBalance += expense.getBobPaid() - expense.getAnnPaid();
        }
        LOGGER.debug("Bob's net expenses balance is: {}", bobExpenseBalance);

        // Get the net difference in payments
        float bobPaymentBalance = 0;

        for (Transaction payment : paymentsTableModel.GetAllTransactions()) {
            bobPaymentBalance += payment.getBobPaid() - payment.getAnnPaid();
        }
        LOGGER.debug("Bob's net payments balance is: {}", bobPaymentBalance);

        // Ann owes Bob for his net payments to her and 50% of his net contributions towards the expenses
        this.netBobBalance = (bobExpenseBalance / 2) + bobPaymentBalance;
        LOGGER.debug("Bob's overall net balance is: {}", this.netBobBalance);
    }

    /**
     * Open the update expense dialog
     */
    private void showUpdateExpenseDialog() {
        // Get the selected expense from the table model
        int selectedRowIndex = expensesTable.getSelectedRow();
        Transaction selectedExpense = expensesTableModel.getTransaction(selectedRowIndex);

        TransactionDialog expenseDialog = new TransactionDialog(this,
                TranDialogMode.UPDATE_EXPENSE, selectedExpense);
        GuiUtils.showCentered(expenseDialog);

        if (expenseDialog.isValidTransaction()) {
            persistUpdatedTransaction(selectedRowIndex, expenseDialog.getTransaction());
        }
    }

    private void showDeletePaymentDialog() {
        int selectedRow = paymentsTable.getSelectedRow();
        Transaction selectedTran = paymentsTableModel.getTransaction(selectedRow);

        int answer = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected payment?", "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            persistDeletedTransaction(selectedTran);
        }
    }

    private void persistDeletedTransaction(Transaction tran) {
        LOGGER.debug("Deleting transaction: {}", tran);

        try {
            if (controller.deleteTransaction(tran.getId())) {

                if (tran.getTransactionType() == TransactionType.EXPENSE) {
                    expensesTableModel.deleteTransaction(tran);
                    updateUI(TableUpdateType.EXPENSE);
                } else {
                    paymentsTableModel.deleteTransaction(tran);
                    updateUI(TableUpdateType.PAYMENT);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete transaction",
                        "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException ex) {
            handleFatalException(ex);
        }
    }

    private void showDeleteExpenseDialog() {
        int selectedRow = expensesTable.getSelectedRow();
        Transaction selectedTran = expensesTableModel.getTransaction(selectedRow);

        int answer = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected expense?", "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            persistDeletedTransaction(selectedTran);
        }
    }

    /**
     * Update the transaction in the DB and JTable
     *
     * @param tableRowIndex The row index of the transaction in the JTable
     * @param tran          The updated transaction
     */
    private void persistUpdatedTransaction(int tableRowIndex, Transaction tran) {
        LOGGER.debug("Updated transaction passed validation: {}", tran);

        try {
            if (controller.updateTransaction(tran)) {

                if (tran.getTransactionType() == TransactionType.EXPENSE) {
                    expensesTableModel.replaceTransaction(tableRowIndex, tran);
                    updateUI(TableUpdateType.EXPENSE);
                } else {
                    paymentsTableModel.replaceTransaction(tableRowIndex, tran);
                    updateUI(TableUpdateType.PAYMENT);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update transaction",
                        "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException ex) {
            handleFatalException(ex);
        }
    }

    /**
     * Add the transaction to the DB and the JTable
     */
    private void persistNewTransaction(Transaction tran) {
        LOGGER.debug("New transaction passed validation: {}", tran);

        try {
            Transaction persistedTran = controller.insertTransaction(tran);

            if (tran.getTransactionType() == TransactionType.EXPENSE) {
                expensesTableModel.addTransaction(persistedTran);
                updateUI(TableUpdateType.EXPENSE);
            } else {
                paymentsTableModel.addTransaction(persistedTran);
                updateUI(TableUpdateType.PAYMENT);
            }
        } catch (RuntimeException ex) {
            handleFatalException(ex);
        }
    }

    private void initUI() {
        setSize(1050, 420);
        GuiUtils.loadApplicationImage(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel centerPanel = new JPanel();
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        centerPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        JSplitPane splitter = new JSplitPane();
        centerPanel.add(splitter);
        JScrollPane expScrollPane = new JScrollPane();
        splitter.add(expScrollPane, JSplitPane.RIGHT);
        expScrollPane.setPreferredSize(new java.awt.Dimension(409, 275));
        JScrollPane pmtScrollPane = new JScrollPane();
        splitter.add(pmtScrollPane, JSplitPane.LEFT);
        pmtScrollPane.setPreferredSize(new java.awt.Dimension(409, 275));

        expScrollPane.setViewportView(expensesTable);
        expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        expensesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        paymentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pmtScrollPane.setViewportView(paymentsTable);

        JPanel westPanel = new JPanel();
        BoxLayout westPanelLayout = new BoxLayout(westPanel, BoxLayout.Y_AXIS);
        westPanel.setLayout(westPanelLayout);
        westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        getContentPane().add(westPanel, BorderLayout.WEST);
        westPanel.setPreferredSize(new java.awt.Dimension(94, 309));

        westPanel.add(addPmtButton);
        addPmtButton.setName("addPmtButton");
        addPmtButton.addActionListener(arg0 -> showAddPaymentDialog());

        westPanel.add(deletePmtButton);
        deletePmtButton.setName("deletePmtButton");
        deletePmtButton.addActionListener(arg0 -> showDeletePaymentDialog());

        westPanel.add(editPmtButton);
        editPmtButton.setName("editPmtButton");

        editPmtButton.addActionListener(arg0 -> showUpdatePaymentDialog());

        JPanel eastPanel = new JPanel();
        BoxLayout eastPanelLayout = new BoxLayout(eastPanel, BoxLayout.Y_AXIS);
        eastPanel.setLayout(eastPanelLayout);
        eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        getContentPane().add(eastPanel, BorderLayout.EAST);
        eastPanel.setPreferredSize(new java.awt.Dimension(97, 309));

        eastPanel.add(addExpButton);
        addExpButton.setName("addExpButton");
        addExpButton.addActionListener(evt -> showAddExpenseDialog());

        eastPanel.add(deleteExpButton);
        deleteExpButton.setName("deleteExpButton");
        deleteExpButton.addActionListener(evt -> showDeleteExpenseDialog());

        eastPanel.add(editExpButton);
        editExpButton.setName("editExpButton");
        editExpButton.addActionListener(evt -> showUpdateExpenseDialog());

        JPanel southPanel = new JPanel();
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        southPanel.setBorder(BorderFactory.createTitledBorder(""));

        archiveButton = new JButton();
        southPanel.add(archiveButton);
        archiveButton.setName("archiveButton");
        archiveButton.addActionListener(evt -> doArchive());
        archiveButton.setText("Archive");
        archiveButton.setToolTipText("Archive the payments and expenses");

        JLabel balanceLabel = new JLabel();
        southPanel.add(balanceLabel);
        balanceLabel.setText("Balance:");

        southPanel.add(this.balanceLabel);
        this.balanceLabel.setName("balanceLabel");
        this.balanceLabel.setPreferredSize(new java.awt.Dimension(370, 14));

        JPanel northPanel = new JPanel();
        BorderLayout northPanelLayout = new BorderLayout();
        northPanel.setLayout(northPanelLayout);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        getContentPane().add(northPanel, BorderLayout.NORTH);

        JLabel paymentsLabel = new JLabel();
        northPanel.add(paymentsLabel, BorderLayout.WEST);
        paymentsLabel.setName("paymentsLabel");

        JLabel expensesLabel = new JLabel();
        northPanel.add(expensesLabel, BorderLayout.EAST);
        expensesLabel.setName("expensesLabel");

        // Set some textual properties and fonts on a bunch of components
        setTitle("IOU");

        Font headingFont = new Font("Tahoma", Font.BOLD, 16);

        paymentsLabel.setFont(headingFont);
        paymentsLabel.setText("Payments");

        expensesLabel.setFont(headingFont);
        expensesLabel.setText("Expenses");

        addExpButton.setText("Add");
        addExpButton.setToolTipText("Add a new expense");
        deleteExpButton.setText("Delete");
        deleteExpButton.setToolTipText("Delete the selected expense");
        editExpButton.setText("Edit");
        editExpButton.setToolTipText("Edit the selected expense");

        addPmtButton.setText("Add");
        addPmtButton.setToolTipText("Add a new payment");
        deletePmtButton.setText("Delete");
        deletePmtButton.setToolTipText("Delete the selected payment");
        editPmtButton.setText("Edit");
        editPmtButton.setToolTipText("Edit the selected payment");

        this.balanceLabel.setForeground(Color.RED);
        LOGGER.debug("Current width is: {}", this.getWidth());

        GuiUtils.showCentered(this);
    }

    /**
     * Archive the current transactions
     */
    private void doArchive() {
        try {
            int answer = JOptionPane.showConfirmDialog(this, """                    
                    Are you sure you want to archive all payments and expenses?
                    This will cause all currently displayed payments and expenses to be replaced by a single balancing payment.""",
                "Confirm Archive", JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.YES_OPTION) {
                GuiUtils.changeCursor(this, Cursor.WAIT_CURSOR);
                controller.archiveTransactions(this.netBobBalance);

                // Refresh the list of payments and expenses. At most, a single balancing
                // payment should be returned
                loadData();
            }
        } catch (RuntimeException ex) {
            handleFatalException(ex);

        } finally {
            GuiUtils.changeCursor(this, Cursor.DEFAULT_CURSOR);
        }
    }

    private void handleFatalException(Exception ex) {
        LOGGER.error("Fatal error occurred", ex);
        JOptionPane.showMessageDialog(this,
                "An unexpected error occurred.\nPlease consult the logs for further information.", "Fatal Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
