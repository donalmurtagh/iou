package iou.gui;

import iou.enums.IField;
import org.apache.log4j.Logger;

import javax.swing.*;

public class TransactionTable extends JTable {

    private static final long serialVersionUID = -5818651467677229726L;

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
        if (col == IField.DATE_COLUMN_INDEX) {
            return java.util.Date.class;
        }

        return getValueAt(0, col).getClass();
    }
}
