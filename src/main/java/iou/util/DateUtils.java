package iou.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    private DateUtils() {
    }

    private static DateFormat getDateFormatter() {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
        dateFormatter.setLenient(false);
        return dateFormatter;
    }

    public static Date string2Date(String string) throws ParseException {
        Date date = getDateFormatter().parse(string);
        LOGGER.debug("Converted text '{}' to date: {}", string, date);
        return date;
    }

    public static String date2String(Date date) {
        return getDateFormatter().format(date);
    }
}
