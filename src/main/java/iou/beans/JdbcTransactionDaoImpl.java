package iou.beans;

import iou.enums.TransactionType;
import iou.model.Expense;
import iou.model.Payment;
import iou.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcTransactionDaoImpl extends JdbcDaoSupport implements TransactionDao {

    /**
     * An object that maps rows in the transaction table to Transaction objects
     */
    private final RowMapper<Transaction> rowMapper = (rs, rowNum) -> {
        Transaction tran;

        if (rs.getString("type").equals(TransactionType.EXPENSE.toString())) {
            tran = new Expense();
        } else {
            tran = new Payment();
        }

        tran.setId(rs.getLong("id"));
        tran.setDate(rs.getDate("tran_date"));
        tran.setDescription(rs.getString("description"));
        tran.setBobPaid(rs.getFloat("bob_paid"));
        tran.setAnnPaid(rs.getFloat("ann_paid"));
        return tran;
    };

    public JdbcTransactionDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public void testConnection() {
        getJdbcTemplate().execute("select 1 from transaction where 1 = 2");
    }

    @Override
    public void deleteTransaction(Long id) {
        var sql = "delete from transaction where id = ?";
        int rowsAffected = getJdbcTemplate().update(sql, id);
        if (rowsAffected != 1) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(sql, 1, rowsAffected);
        }
    }

    @Override
    public void updateTransaction(Transaction tran) {
        var sql = """
            update transaction
            set type = ?, tran_date = ?, description = ?, ann_paid = ?, bob_paid = ?
            where id = ?""";

        Object[] params = {
            tran.getTransactionType().toString(),
            tran.getDate(),
            tran.getDescription(),
            tran.getAnnPaid(),
            tran.getBobPaid(),
            tran.getId()
        };
        int rowsUpdated = getJdbcTemplate().update(sql, params);
        if (rowsUpdated != 1) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(sql, 1, rowsUpdated);
        }
    }

    @Override
    public Transaction insertTransaction(final Transaction tran) {
        // Will hold the ID of the row created by the insert
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                insert into transaction (type, tran_date, description, ann_paid, bob_paid)
                values(?, ?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);

            Date sqlDate = new Date(tran.getDate().getTime());
            ps.setString(1, tran.getTransactionType().toString());
            ps.setDate(2, sqlDate);
            ps.setString(3, tran.getDescription());
            ps.setFloat(4, tran.getAnnPaid());
            ps.setFloat(5, tran.getBobPaid());
            return ps;
        }, keyHolder);

        tran.setId(keyHolder.getKey().longValue());
        return tran;
    }

    @Override
    public List<Transaction> getTransactions(TransactionType type) {
        return getJdbcTemplate().query("""
            select id, type, tran_date, description, ann_paid, bob_paid
            from transaction
            where archived = 0
            and type = ?""", rowMapper, type.toString());
    }

    @Override
    public void archiveTransactions() {
        // Set all current transactions to archived
        getJdbcTemplate().update("update transaction set archived = 1");
    }
}
