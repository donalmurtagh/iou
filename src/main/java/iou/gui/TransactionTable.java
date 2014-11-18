package iou.gui;

import iou.enums.Field;
import java.util.Date;
import javax.swing.*;

public class TransactionTable extends JTable {

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JTable#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JTable#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int col) {
        if (col == Field.DATE_COLUMN_INDEX) {
            return Date.class;
        }

        return getValueAt(0, col).getClass();
    }
}
