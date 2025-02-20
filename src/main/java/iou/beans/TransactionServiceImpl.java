package iou.beans;

import iou.enums.TransactionType;
import iou.model.Payment;
import iou.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TrasactionService {

    private final TransactionDao transactionDao;

    public TransactionServiceImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public void login(String username, String password) {
        transactionDao.testConnection();
    }

    @Override
    public List<Transaction> getTransactions(TransactionType type) {
        return transactionDao.getTransactions(type);
    }

    @Override
    public Transaction insertTransaction(Transaction tran) {
        return transactionDao.insertTransaction(tran);
    }

    @Override
    public boolean updateTransaction(Transaction tran) {
        return transactionDao.updateTransaction(tran);
    }

    @Override
    public boolean deleteTransaction(Long id) {
        return transactionDao.deleteTransaction(id);
    }

    @Override
    public void archiveTransactions(Float netBobBalance) {

        // Archive all current transactions
        transactionDao.archiveTransactions();

        if (netBobBalance != 0) {

            // Carry the balance forward by inserting a new payment for the relevant amount
            Transaction balancingPayment = new Payment();
            balancingPayment.setDate(new Date());
            balancingPayment.setDescription("Balance brought forward");

            // A positive netBobBalance indicates that Bob owes Ann and vice versa
            if (netBobBalance > 0) {
                balancingPayment.setBobPaid(netBobBalance);
            } else {
                // Ann owes Bob, change the sign of the amount before inserting
                balancingPayment.setAnnPaid(-netBobBalance);
            }
            insertTransaction(balancingPayment);
        }
    }
}
