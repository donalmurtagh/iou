package iou.util;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    private static final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");

    private static final Logger LOGGER = Logger.getLogger(DateUtils.class);

    static {
        dateFormatter.setLenient(false);
    }

    private DateUtils() {
    }

    public static Date string2Date(String str) throws ParseException {

        LOGGER.debug("Parsing date string: " + str);
        Date date = dateFormatter.parse(str);
        LOGGER.debug("Converted to date: " + date);
        return date;
    }

    public static String date2String(Date date) {
        return dateFormatter.format(date);
    }
}
