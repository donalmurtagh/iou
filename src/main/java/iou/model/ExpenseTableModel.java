package iou.model;

import iou.enums.ExpenseField;
import iou.util.DateUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.List;

public class ExpenseTableModel extends TransactionTableModel {

    private static final Logger LOGGER = Logger.getLogger(ExpenseTableModel.class);

    public ExpenseTableModel(List<Transaction> records) {
        super(records);
    }

    /**
     * Gets the number of columns encapsulated within this
     * <code>TableModel</code>
     *
     * @return The number of table columns
     */
    public int getColumnCount() {
        return ExpenseField.values().length;
    }

    /**
     * Sets a single expense field
     *
     * @param aValue The new field value
     * @param row    Identifies which expense record to modify
     * @param column Identifies which expense field to modify
     */
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        String fieldValue = aValue.toString();
        Expense expense = (Expense) txnRecords.get(row);

        if (column == ExpenseField.DATE.getIndex()) {

            try {
                expense.setDate(DateUtils.string2Date(fieldValue));
            } catch (ParseException e) {
                LOGGER.error("Error parsing date value: " + fieldValue);
                // TODO: Do something a bit smarter
                throw new IllegalArgumentException(e);
            }
        } else if (column == ExpenseField.DESCRIPTION.getIndex()) {
            expense.setDescription(fieldValue);

        } else if (column == ExpenseField.ANN_PAID.getIndex()) {
            expense.setAnnPaid(new Float(fieldValue));

        } else if (column == ExpenseField.BOB_PAID.getIndex()) {
            expense.setBobPaid(new Float(fieldValue));

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
        Expense expense = (Expense) txnRecords.get(rowIndex);

        if (columnIndex == ExpenseField.DATE.getIndex()) {
            return expense.getDate();

        } else if (columnIndex == ExpenseField.DESCRIPTION.getIndex()) {
            return expense.getDescription();

        } else if (columnIndex == ExpenseField.ANN_PAID.getIndex()) {
            return expense.getAnnPaid();

        } else if (columnIndex == ExpenseField.BOB_PAID.getIndex()) {
            return expense.getBobPaid();

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
        return ExpenseField.getByIndex(columnIndex).getName();
    }
}
