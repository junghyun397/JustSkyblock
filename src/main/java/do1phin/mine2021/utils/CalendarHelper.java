package do1phin.mine2021.utils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarHelper {

    public static String[] getYMDHMFromTimestamp(Timestamp timestamp) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
        calendar.setTimeInMillis(timestamp.getTime());
        return new String[]{
                String.valueOf(calendar.get(Calendar.YEAR)),
                String.valueOf(calendar.get(Calendar.MONTH) + 1),
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)),
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)),
                String.valueOf(calendar.get(Calendar.MINUTE))
        };
    }

}
