package iou.gui;

import iou.controller.Factory;
import iou.controller.IController;
import iou.enums.ExpenseField;
import iou.enums.PaymentField;
import iou.enums.TranDialogMode;
import iou.enums.TransactionType;
import iou.model.*;
import iou.util.GuiUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * The main application window
 *
 * @author Donal
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = -3027922271769048472L;

    private JPanel centerPanel;

    private JPanel southPanel;

    private JButton editPmtButton = new JButton();

    private JButton archiveButton;

    private JLabel balanceLabel = new JLabel();

    private JButton addExpButton = new JButton();

    private JButton deleteExpButton = new JButton();

    private JButton deletePmtButton = new JButton();

    private JLabel summaryLabel;

    private JTable expensesTable = new TransactionTable();

    private JTable paymentsTable = new TransactionTable();

    private JScrollPane pmtScrollPane;

    private JScrollPane expScrollPane;

    private JLabel expensesLabel;

    private JLabel paymentsLabel;

    private JButton editExpButton = new JButton();

    private JButton addPmtButton = new JButton();

    private JSplitPane splitter;

    private JPanel eastPanel;

    private JPanel westPanel;

    private JPanel northPanel;

    private TransactionTableModel expensesTableModel;

    private IController controller = Factory.getController();

    private TransactionTableModel paymentsTableModel;

    private static final Font HEADING_FONT = new Font("Tahoma", Font.BOLD, 16);

    /**
     * If positive, Maude owes Donal, and vice versa
     */
    private float netDonalBalance;

    private enum TableUpdateType {
        PAYMENT, EXPENSE, BOTH;
    }

    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);

    public MainFrame() {
        startup();
        refreshGUI();
    }

    private void refreshGUI() {

        GuiUtils.changeCursor(this, Cursor.WAIT_CURSOR);

        try {
            // Get all the expenses
            List<Transaction> expenses = controller.getTransactions(TransactionType.EXPENSE);
            LOGGER.debug("Retrieved initial list of " + expenses.size() + " expenses");
            expensesTableModel = new ExpenseTableModel(expenses);
            expensesTable.setModel(expensesTableModel);

            // Get all the payments
            List<Transaction> payments = controller.getTransactions(TransactionType.PAYMENT);
            LOGGER.debug("Retrieved initial list of " + payments.size() + " payments");
            paymentsTableModel = new PaymentTableModel(payments);
            paymentsTable.setModel(paymentsTableModel);

            // Make the description columns a bit wider than the others
            GuiUtils.setTableColumnWidth(expensesTable, ExpenseField.DESCRIPTION.getIndex(), 170);
            GuiUtils.setTableColumnWidth(paymentsTable, PaymentField.DESCRIPTION.getIndex(), 170);

            updateUI(TableUpdateType.BOTH);

        } catch (RuntimeException ex) {
            handleFatalException(ex);

        } finally {
            GuiUtils.changeCursor(this, Cursor.DEFAULT_CURSOR);
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
    private static void updateTableAndButtonsUI(JTable table, JButton editButton,
                                                JButton deleteButton) {

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

        if (netDonalBalance > 0) {
            balanceLabel.setText("Maude owes Donal $" + GuiUtils.formatDecimal(netDonalBalance));

        } else if (netDonalBalance < 0) {

            // A negative balance indicates Donal owes Maude, but it will be shown
            // as a positive amount
            balanceLabel.setText("Donal owes Maude $" + GuiUtils.formatDecimal(-netDonalBalance));

        } else {
            balanceLabel.setText("Nothing owed");
        }
    }

    /**
     * Calculate the overall amount owed to Donal or owed by Donal
     */
    private void updateNetBalance() {
        float donalExpenseBalance = 0;

        for (Transaction expense : expensesTableModel.GetAllTransactions()) {
            donalExpenseBalance += expense.getDonalPaid() - expense.getMaudePaid();
        }
        LOGGER.debug("Donal's net expenses balance is: " + donalExpenseBalance);

        // Get the net difference in payments
        float donalPaymentBalance = 0;

        for (Transaction payment : paymentsTableModel.GetAllTransactions()) {
            donalPaymentBalance += payment.getDonalPaid() - payment.getMaudePaid();
        }
        LOGGER.debug("Donal's net payments balance is: " + donalPaymentBalance);

        // Maude owes Donal for his net payments to her and 50% of his net
        // contributions towards the expenses
        this.netDonalBalance = (donalExpenseBalance / 2) + donalPaymentBalance;
        LOGGER.debug("Donal's overall net balance is: " + this.netDonalBalance);
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

        LOGGER.debug("Deleting transaction: " + tran);

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

        LOGGER.debug("Updated transaction passed validation: " + tran);

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

        LOGGER.debug("New transaction passed validation: " + tran);

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

    private void startup() {

        setSize(1050, 420);
        GuiUtils.loadApplicationImage(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        centerPanel = new JPanel();
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        centerPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        splitter = new JSplitPane();
        centerPanel.add(splitter);
        expScrollPane = new JScrollPane();
        splitter.add(expScrollPane, JSplitPane.RIGHT);
        expScrollPane.setPreferredSize(new java.awt.Dimension(409, 275));
        pmtScrollPane = new JScrollPane();
        splitter.add(pmtScrollPane, JSplitPane.LEFT);
        pmtScrollPane.setPreferredSize(new java.awt.Dimension(409, 275));

        expScrollPane.setViewportView(expensesTable);
        expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        expensesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        paymentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pmtScrollPane.setViewportView(paymentsTable);

        westPanel = new JPanel();
        BoxLayout westPanelLayout = new BoxLayout(westPanel, BoxLayout.Y_AXIS);
        westPanel.setLayout(westPanelLayout);
        westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        getContentPane().add(westPanel, BorderLayout.WEST);
        westPanel.setPreferredSize(new java.awt.Dimension(94, 309));

        westPanel.add(addPmtButton);
        addPmtButton.setName("addPmtButton");
        addPmtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showAddPaymentDialog();
            }
        });

        westPanel.add(deletePmtButton);
        deletePmtButton.setName("deletePmtButton");
        deletePmtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showDeletePaymentDialog();
            }
        });

        westPanel.add(editPmtButton);
        editPmtButton.setName("editPmtButton");

        editPmtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showUpdatePaymentDialog();
            }
        });

        eastPanel = new JPanel();
        BoxLayout eastPanelLayout = new BoxLayout(eastPanel, BoxLayout.Y_AXIS);
        eastPanel.setLayout(eastPanelLayout);
        eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        getContentPane().add(eastPanel, BorderLayout.EAST);
        eastPanel.setPreferredSize(new java.awt.Dimension(97, 309));

        eastPanel.add(addExpButton);
        addExpButton.setName("addExpButton");
        addExpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showAddExpenseDialog();

            }
        });

        eastPanel.add(deleteExpButton);
        deleteExpButton.setName("deleteExpButton");
        deleteExpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showDeleteExpenseDialog();
            }
        });

        eastPanel.add(editExpButton);
        editExpButton.setName("editExpButton");
        editExpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showUpdateExpenseDialog();
            }
        });

        southPanel = new JPanel();
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        southPanel.setBorder(BorderFactory.createTitledBorder(""));

        archiveButton = new JButton();
        southPanel.add(archiveButton);
        archiveButton.setName("archiveButton");
        archiveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doArchive();
            }
        });
        archiveButton.setText("Archive");
        archiveButton.setToolTipText("Archive the payments and expenses");


        summaryLabel = new JLabel();
        southPanel.add(summaryLabel);
        summaryLabel.setName("summaryLabel");
        summaryLabel.setText("Summary:");

        southPanel.add(balanceLabel);
        balanceLabel.setName("balanceLabel");
        balanceLabel.setPreferredSize(new java.awt.Dimension(170, 14));

        northPanel = new JPanel();
        BorderLayout northPanelLayout = new BorderLayout();
        northPanel.setLayout(northPanelLayout);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        getContentPane().add(northPanel, BorderLayout.NORTH);

        paymentsLabel = new JLabel();
        northPanel.add(paymentsLabel, BorderLayout.WEST);
        paymentsLabel.setName("paymentsLabel");

        expensesLabel = new JLabel();
        northPanel.add(expensesLabel, BorderLayout.EAST);
        expensesLabel.setName("expensesLabel");

        // Set some textual properties and fonts on a bunch of components
        setTitle("IOU");

        paymentsLabel.setFont(HEADING_FONT);
        paymentsLabel.setText("Payments");

        expensesLabel.setFont(HEADING_FONT);
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

        LOGGER.debug("Current width is: " + this.getWidth());

        GuiUtils.showCentered(this);
    }

    /**
     * Archive the current transactions
     */
    private void doArchive() {

        try {

            int answer = JOptionPane
                    .showConfirmDialog(
                            this,
                            "Are you sure you want to archive all payments and expenses?\n"
                                    + "This will cause all currently displayed payments and expenses to be replaced "
                                    + "by a single balancing payment.", "Confirm Archive",
                            JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.YES_OPTION) {

                controller.archiveTransactions(this.netDonalBalance);

                // Refresh the list of payments and expenses. At most, a single balancing
                // payment should be returned
                refreshGUI();
            }
        } catch (RuntimeException ex) {
            handleFatalException(ex);
        }
    }

    /**
     * Handle any errors that may arise from accessing the database
     * These should all be converted to subclasses of RuntimeException by Spring
     *
     * @param ex
     */
    private void handleFatalException(RuntimeException ex) {

        LOGGER.fatal("Error accessing database", ex);
        JOptionPane.showMessageDialog(this,
                "An unexpected error occurred.\nThe application will be shut down.\n"
                        + "Please consult the logs for further information.", "Fatal Error",
                JOptionPane.ERROR_MESSAGE);

        // Shut down the application to prevent any (further) data corruption
        System.exit(1);
    }
}