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

    private TransactionDao dao;

    private final ApplicationContext applicationContext;

    public ControllerImpl() {
        applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
    }

    public boolean login(String username, String password) {

        dao = (TransactionDao) applicationContext.getBean("txnDao");

        // Set the username and password, then try and login
        BasicDataSource dataSource = (BasicDataSource) applicationContext.getBean("dataSource");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        try {
            dao.testConnection();
            LOGGER.debug("Connection successfully tested");
            return true;

        } catch (Exception ex) {
            LOGGER.error("DB connection test failed", ex);
            return false;
        }
    }

    public List<Transaction> getTransactions(TransactionType type) {
        return dao.getTransactions(type);
    }

    public Transaction insertTransaction(Transaction tran) {
        return dao.insertTransaction(tran);
    }

    public boolean updateTransaction(Transaction tran) {
        return dao.updateTransaction(tran);
    }

    public boolean deleteTransaction(Long id) {
        return dao.deleteTransaction(id);
    }

    public void archiveTransactions(Float netBobBalance) {

        // Archive all current transactions
        dao.archiveTransactions();

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
