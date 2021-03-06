package org.openyu.commons.util;

import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openyu.commons.helper.ex.HelperException;
import org.openyu.commons.helper.supporter.BaseHelperSupporter;

public final class TimestampHelper extends BaseHelperSupporter {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(TimestampHelper.class);

	private TimestampHelper() {
		throw new HelperException(
				new StringBuilder().append(TimestampHelper.class.getName()).append(" can not construct").toString());
	}

	public static Timestamp toTimestamp(String value) {
		Timestamp result = null;
		if (value != null) {
			try {
				result = Timestamp.valueOf(value);
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		return result;
	}

	// yyyy-mm-dd hh:mm:ss.fffffffff
	public static String toString(Timestamp value) {
		String result = null;
		if (value != null) {
			try {
				result = value.toString();
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		return result;
	}
	// public static Timestamp wrap(Object value) {
	// Timestamp result = null;
	// if (value instanceof Timestamp) {
	// result = (Timestamp) value;
	// } else if (value instanceof String) {
	// result = parse((String) value);
	// }
	// return result;
	// }

	// public static Timestamp setDate(Timestamp timestamp, java.util.Date date)
	// {
	// if (timestamp == null || date == null)
	// {
	// return null;
	// }
	// Calendar timestampCalendar = Calendar.getInstance();
	// timestampCalendar.setTimeInMillis(timestamp.getTime());
	// Calendar dateCalendar = Calendar.getInstance();
	// dateCalendar.setTimeInMillis(date.getTime());
	// timestampCalendar.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR));
	// timestampCalendar.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH));
	// timestampCalendar.set(Calendar.DAY_OF_MONTH,
	// dateCalendar.get(Calendar.DAY_OF_MONTH));
	//
	// return new Timestamp(timestampCalendar.getTimeInMillis());
	// }
	//
	// public static Timestamp setSqlDate(Timestamp timestamp, java.sql.Date
	// sqlDate)
	// {
	// return setDate(timestamp, sqlDate);
	// }
	//
	// public static Timestamp setTime(Timestamp timestamp, Time time)
	// {
	// if (timestamp == null || time == null)
	// {
	// return null;
	// }
	// Calendar timestampCalendar = Calendar.getInstance();
	// timestampCalendar.setTimeInMillis(timestamp.getTime());
	// Calendar timeCalendar = Calendar.getInstance();
	// timeCalendar.setTimeInMillis(time.getTime());
	// timestampCalendar.set(Calendar.HOUR, timeCalendar.get(Calendar.HOUR));
	// timestampCalendar.set(Calendar.MINUTE,
	// timeCalendar.get(Calendar.MINUTE));
	// timestampCalendar.set(Calendar.SECOND,
	// timeCalendar.get(Calendar.SECOND));
	//
	// return new Timestamp(timestampCalendar.getTimeInMillis());
	// }
}
