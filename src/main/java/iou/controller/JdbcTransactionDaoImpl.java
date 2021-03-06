package iou.controller;

import iou.enums.TransactionType;
import iou.model.Expense;
import iou.model.Payment;
import iou.model.Transaction;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class JdbcTransactionDaoImpl extends SimpleJdbcDaoSupport implements TransactionDao {

    private static final Logger LOGGER = Logger.getLogger(JdbcTransactionDaoImpl.class);

    private static final String GET_TRANS_SQL =
            "select id, type, tran_date, description, ann_paid, bob_paid "
                    + "from transaction "
                    + "where archived = 0 "
                    + "and type = ?";


    /**
     * TODO: Replace with named params
     */
    private static final String INSERT_TRAN_SQL =
            "insert into TRANSACTION (type, tran_date, description, ann_paid, bob_paid) values(?, ?, ?, ?, ?)";

    private static final String DELETE_TRAN_SQL =
            "delete from transaction where id = ?";

    /**
     * TODO: Replace with named params
     */
    private static final String UPDATE_TRAN_SQL =
            "update transaction " +
                    "set type = ?, " +
                    "tran_date = ?, " +
                    "description = ?, " +
                    "ann_paid = ?, " +
                    "bob_paid = ? " +
                    "where id = ?";

    private static final String ARCHIVE_TRANS_SQL =
            "update transaction set archived = 1";

    private UpdateTransaction tranUpdater;


    /* (non-Javadoc)
     * @see org.springframework.dao.support.DaoSupport#initDao()
     */
    @Override
    protected void initDao() throws Exception {

        tranUpdater = new UpdateTransaction(getDataSource());
    }

    /**
     * Encapsulates a transaction row update
     *
     * @author dmurtagh
     */
    public static class UpdateTransaction extends SqlUpdate {

        public UpdateTransaction(DataSource ds) {
            setDataSource(ds);
            setSql(UPDATE_TRAN_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.DATE));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.FLOAT));
            declareParameter(new SqlParameter(Types.FLOAT));
            declareParameter(new SqlParameter(Types.NUMERIC));
            compile();
        }

        /**
         * Execute the transaction update SQL
         *
         * @param tran
         * @return
         */
        public boolean doUpdate(Transaction tran) {

            Object[] params = {tran.getTransactionType().toString(), tran.getDate(),
                    tran.getDescription(), tran.getAnnPaid(), tran.getBobPaid(),
                    tran.getId()};

            int rowsAffected = update(params);
            LOGGER.debug("Rows affected by update: " + rowsAffected);
            return rowsAffected == 1 ? true : false;
        }
    }

    /* (non-Javadoc)
     * @see iou.controller.ITransactionDao#testConnection()
     */
    public void testConnection() {

        // Execute this just for the side-effect of raising an exception
        // if there's something wrong with the connection or schema;
        getJdbcTemplate().execute("select * from transaction where 1 = 2");
    }

    /* (non-Javadoc)
     * @see iou.controller.ITransactionDao#deleteTransaction(java.lang.Long)
     */
    public boolean deleteTransaction(Long id) {

        int rowsAffected = getJdbcTemplate().update(DELETE_TRAN_SQL, new Object[]{id});
        LOGGER.debug("Rows affected by delete: " + rowsAffected);
        return rowsAffected == 1 ? true : false;
    }

    public boolean updateTransaction(Transaction tran) {
        return tranUpdater.doUpdate(tran);
    }


    /* (non-Javadoc)
     * @see iou.controller.ITransactionDao#insertTransaction(iou.model.Transaction)
     */
    public Transaction insertTransaction(final Transaction tran) {

        // Will hold the ID of the row created by the insert
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // TODO: maybe this instance of PreparedStatementCreator could be
        // reused, rather than creating a new object each time this method is called
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {

                PreparedStatement ps = connection.prepareStatement(INSERT_TRAN_SQL, Statement.RETURN_GENERATED_KEYS);

                // Parameters order is: type, tran_date, description, ann_paid, bob_paid
                ps.setString(1, tran.getTransactionType().toString());

                Date sqlDate = new Date(tran.getDate().getTime());
                ps.setDate(2, sqlDate);
                ps.setString(3, tran.getDescription());
                ps.setFloat(4, tran.getAnnPaid());
                ps.setFloat(5, tran.getBobPaid());

                return ps;
            }
        }, keyHolder);

        tran.setId(keyHolder.getKey().longValue());
        return tran;
    }

    /**
     * An object that maps rows in the transaction table to Transaction objects
     */
    private ParameterizedRowMapper<Transaction> rowMapper = new ParameterizedRowMapper<Transaction>() {

        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
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
        }
    };

    /*
     * (non-Javadoc)
     * @see iou.controller.ITransactionDao#getTransactions(iou.enums.TransactionType)
     */
    public List<Transaction> getTransactions(TransactionType type) {

        return getSimpleJdbcTemplate().query(GET_TRANS_SQL, rowMapper, type.toString());
    }

    /* (non-Javadoc)
     * @see iou.controller.ITransactionDao#archiveTransactions(java.lang.Float)
     */
    public void archiveTransactions() {

        // Set all current transactions to archived
        getJdbcTemplate().update(ARCHIVE_TRANS_SQL);
    }
}
