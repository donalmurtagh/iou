package iou.model;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class defines the table model of the <code>JTable</code> used in the
 * main application window
 *
 * @author Donal Murtagh
 */
@SuppressWarnings("serial")
public abstract class TransactionTableModel extends DefaultTableModel {

    /**
     * Holds the transaction records shown in the JTable body.
     */
    protected final List<Transaction> txnRecords = new ArrayList<Transaction>();

    /**
     * Creates an instance of this class
     *
     * @param records Each element of the array corresponds to a row in the JTable body
     */
    public TransactionTableModel(List<Transaction> records) {
        txnRecords.addAll(records);
    }

    public final void addTransaction(Transaction tran) {
        txnRecords.add(tran);
    }

    public final boolean deleteTransaction(Transaction tran) {
        return txnRecords.remove(tran);
    }

    public final void replaceTransaction(int rowIndex, Transaction tran) {
        txnRecords.set(rowIndex, tran);
    }

    /**
     * Gets the number of rows encapsulated within this <code>TableModel</code>
     *
     * @return The number of table rows
     */
    @Override
    public final int getRowCount() {

        return txnRecords == null ? 0 : txnRecords.size();
    }

    /**
     * Returns the <code>Transaction</code> shown in a row of the JTable
     *
     * @param rowIndex The JTable row number of the transaction
     * @return The expense
     */
    public final Transaction getTransaction(int rowIndex) {
        return txnRecords.get(rowIndex);
    }

    /**
     * Returns a read-only view of the Transaction records
     *
     * @return
     */
    public final List<Transaction> GetAllTransactions() {
        return Collections.unmodifiableList(txnRecords);
    }
}
