package iou.controller;

import iou.enums.TransactionType;
import iou.model.Transaction;

import java.util.List;

public interface IController {

    boolean login(String username, String password);

    public List<Transaction> getTransactions(TransactionType type);

    public Transaction insertTransaction(Transaction tran);

    public boolean updateTransaction(Transaction tran);

    public boolean deleteTransaction(Long id);

    /**
     * Archive all existing transactions and insert a payment transaction that
     * carries forward the current balance
     *
     * @param netDonalBalance
     */
    public void archiveTransactions(Float netDonalBalance);
}
