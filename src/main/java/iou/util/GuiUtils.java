package iou.util;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

public final class GuiUtils {

    private static final Logger LOGGER = Logger.getLogger(GuiUtils.class);

    private static Image applicationImage;

    private GuiUtils() {
    }


    /**
     * Load the application image into the top-left corner of a frame
     *
     * @param frame
     */
    public static void loadApplicationImage(Frame frame) {
        frame.setIconImage(applicationImage);
    }

    public static void setApplicationImage(Image image) {
        applicationImage = image;
    }

    public static void setTableColumnWidth(JTable table, int columnIndex, int width) {

        LOGGER.debug("Table has " + table.getColumnCount() + " columns");

        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setPreferredWidth(width);
    }


    /**
     * If possible, selects the last row in a table
     *
     * @param table
     */
    public static void selectLastRow(JTable table) {

        int rowCount = table.getRowCount();

        // Select the last row if there are rows
        if (rowCount != 0) {

            LOGGER.debug("Selecting last row of: " + table.getRowCount());
            table.setRowSelectionInterval(rowCount - 1, rowCount - 1);
        }
    }


    public static void showCentered(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    public static void changeCursor(Window window, int cursorType) {

        Cursor hourglassCursor = new Cursor(cursorType);
        window.setCursor(hourglassCursor);
    }
}
