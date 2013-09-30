package iou.controller;

import iou.enums.TransactionType;
import iou.model.Payment;
import iou.model.Transaction;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class ControllerImpl implements Controller {

    private static final Logger LOGGER = Logger.getLogger(ControllerImpl.class);

    private final TransactionDao transactionDao;

    public ControllerImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public boolean login(String username, String password) {

        try {
            transactionDao.testConnection();
            LOGGER.debug("Connection successfully tested");
            return true;

        } catch (Exception ex) {
            LOGGER.error("DB connection test failed", ex);
            return false;
        }
    }

    public List<Transaction> getTransactions(TransactionType type) {
        return transactionDao.getTransactions(type);
    }

    public Transaction insertTransaction(Transaction tran) {
        return transactionDao.insertTransaction(tran);
    }

    public boolean updateTransaction(Transaction tran) {
        return transactionDao.updateTransaction(tran);
    }

    public boolean deleteTransaction(Long id) {
        return transactionDao.deleteTransaction(id);
    }

    public void archiveTransactions(Float netBobBalance) {

        // Archive all current transactions
        transactionDao.archiveTransactions();

        if (netBobBalance != 0) {

            // Carry the balance forward by inserting a new payment for the relevant amount
            Transaction balancingPayment = new Payment();
            balancingPayment.setDate(new java.util.Date());
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
