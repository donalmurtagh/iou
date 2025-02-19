package iou.controller;

import iou.enums.TransactionType;
import iou.model.Transaction;

import java.util.List;

public interface TransactionDao {

    void testConnection();

    List<Transaction> getTransactions(TransactionType type);

    Transaction insertTransaction(final Transaction tran);

    boolean deleteTransaction(Long id);

    boolean updateTransaction(Transaction tran);

    void archiveTransactions();
}
