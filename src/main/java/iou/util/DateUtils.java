package iou.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public final class DateUtils {

    private static final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");

    private static final Logger LOGGER = Logger.getLogger(DateUtils.class);

    static {
        dateFormatter.setLenient(false);
    }

    private DateUtils() {
    }

    /**
     * Returns a clone of the date formatter being used in a half-hearted attempt
     * at considering thread-safety (these objects are not synchronized). 
     * @return
     */
    public static DateFormat getDateFormat() {
        return (DateFormat)dateFormatter.clone();
    }

    public static Date string2Date(String str) throws ParseException {

        LOGGER.debug("Parsing date string: " + str);
        Date date = dateFormatter.parse(str);
        LOGGER.debug("Converted to date: " + date);
        return date;
    }

    public static String date2String(Date date) throws ParseException {
        return dateFormatter.format(date);
    }

}
