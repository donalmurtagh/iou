package iou.controller;

import iou.enums.TransactionType;
import iou.model.Transaction;

import java.util.List;

public interface TransactionDao {

    public void testConnection();

    public List<Transaction> getTransactions(TransactionType type);

    public Transaction insertTransaction(final Transaction tran);

    public boolean deleteTransaction(Long id);

    public boolean updateTransaction(Transaction tran);

    public void archiveTransactions();
}
