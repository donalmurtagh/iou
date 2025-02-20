package iou.beans;

import iou.enums.TransactionType;
import iou.model.Transaction;

import java.util.List;

public interface TrasactionService {

    void login(String username, String password);

    List<Transaction> getTransactions(TransactionType type);

    Transaction insertTransaction(Transaction tran);

    boolean updateTransaction(Transaction tran);

    boolean deleteTransaction(Long id);

    /**
     * Archive all existing transactions and insert a payment transaction that
     * carries forward the current balance
     *
     * @param netBalance
     */
    void archiveTransactions(Float netBalance);
}
