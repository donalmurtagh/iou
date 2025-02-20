package iou.beans;

import iou.enums.TransactionType;
import iou.model.Transaction;

import java.util.List;

public interface TransactionDao {

    void testConnection();

    List<Transaction> getTransactions(TransactionType type);

    Transaction insertTransaction(final Transaction tran);

    void deleteTransaction(Long id);

    void updateTransaction(Transaction tran);

    void archiveTransactions();
}
