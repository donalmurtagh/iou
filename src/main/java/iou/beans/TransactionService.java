package iou.beans;

import iou.enums.TransactionType;
import iou.model.Transaction;

import java.util.List;

public interface TransactionService {

    void login();

    List<Transaction> getTransactions(TransactionType type);

    Transaction insertTransaction(Transaction tran);

    void updateTransaction(Transaction tran);

    void deleteTransaction(Long id);

    /**
     * Archive all existing transactions and insert a payment transaction that
     * carries forward the current balance
     *
     * @param netBalance
     */
    void archiveTransactions(Float netBalance);
}
