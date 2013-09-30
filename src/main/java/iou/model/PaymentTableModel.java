package iou.model;

import iou.enums.PaymentField;
import iou.enums.User;
import iou.util.DateUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.List;

public class PaymentTableModel extends TransactionTableModel {

    private static final long serialVersionUID = 4868833031246226851L;

    private static final Logger LOGGER = Logger.getLogger(PaymentTableModel.class);

    public PaymentTableModel(List<Transaction> records) {
        super(records);
    }

    /**
     * Gets the number of columns encapsulated within this
     * <code>TableModel</code>
     *
     * @return The number of table columns
     */
    public int getColumnCount() {
        return PaymentField.values().length;
    }

    /**
     * Sets a single payment field
     *
     * @param aValue The new field value
     * @param row    Identifies which payment record to modify
     * @param column Identifies which payment field to modify
     */
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        String fieldValue = aValue.toString();
        Payment payment = (Payment) txnRecords.get(row);

        if (column == PaymentField.DATE.getIndex()) {

            try {
                payment.setDate(DateUtils.string2Date(fieldValue));
            } catch (ParseException e) {
                LOGGER.error("Error parsing date value: " + fieldValue);
                // TODO: Do something a bit smarter
                throw new IllegalArgumentException(e);
            }
        } else if (column == PaymentField.DESCRIPTION.getIndex()) {
            payment.setDescription(fieldValue);

        } else if (column == PaymentField.PAID_BY.getIndex()) {

            if (aValue.toString().equals(User.ANN)) {
                payment.setAnnPaid(new Float(fieldValue));

            } else if (aValue.toString().equals(User.BOB)) {
                payment.setBobPaid(new Float(fieldValue));

            } else {
                throw new IllegalArgumentException("Unrecognised user: " + aValue);
            }
        } else {
            throw new IllegalArgumentException("Invalid column index: " + column);
        }
    }

    /**
     * This method is used by the JTable to determine which fields to shown in
     * which cells
     *
     * @param rowIndex    The specified row index number
     * @param columnIndex The specified column index number
     * @return The object to show in the specified cell. The displayed value
     *         will the result of calling <code>toString</code> on this
     *         object.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Payment payment = (Payment) txnRecords.get(rowIndex);

        if (columnIndex == PaymentField.DATE.getIndex()) {
            return payment.getDate();

        } else if (columnIndex == PaymentField.DESCRIPTION.getIndex()) {
            return payment.getDescription();

        } else if (columnIndex == PaymentField.PAID_BY.getIndex()) {

            //LOGGER.debug("Getting payee for payment: " + payment);
            if (payment.getBobPaid() != 0) {
                return User.BOB;

            } else if (payment.getAnnPaid() != 0) {
                return User.ANN;

            } else {
                throw new RuntimeException("Could not determine payee for record ID: "
                        + payment.getId());
            }

        } else if (columnIndex == PaymentField.AMOUNT.getIndex()) {

            if (payment.getBobPaid() != 0) {
                return payment.getBobPaid();

            } else if (payment.getAnnPaid() != 0) {
                return payment.getAnnPaid();

            } else {
                throw new RuntimeException("Could not determine payee for record ID: "
                        + payment.getId());
            }

        } else {
            throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }
    }

    /**
     * Returns the header name of a specified column
     *
     * @param columnIndex The index of the table column
     * @return The table column header name
     */
    @Override
    public String getColumnName(int columnIndex) {
        return PaymentField.getByIndex(columnIndex).getName();
    }
}
