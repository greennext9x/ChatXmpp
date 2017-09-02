package hoangcuongdev.com.xmpp.utils;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

public class DateUtil {

	public static long getNow(){
		long time = DateTime.now().getMillis();
		return time;
	}

	/**
	 * @param msgTimeMillis
	 * @return
	 */
	public static String getMsgFormatTime(long msgTimeMillis) {
		DateTime nowTime = new DateTime();
		DateTime msgTime = new DateTime(msgTimeMillis);
		int days = Math.abs( Days.daysBetween(msgTime, nowTime).getDays());
		if (days < 1) {
			return getTime(msgTime);
		} else if (days == 1) {
			return "Hôm qua " + getTime(msgTime);
		} else if (days <= 7) {
			switch (msgTime.getDayOfWeek()) {
				case DateTimeConstants.SUNDAY:
					return "Thứ 2 " + getTime(msgTime);
				case DateTimeConstants.MONDAY:
					return "Thứ 3 " + getTime(msgTime);
				case DateTimeConstants.TUESDAY:
					return "Thứ 4 " + getTime(msgTime);
				case DateTimeConstants.WEDNESDAY:
					return "Thứ 5 " + getTime(msgTime);
				case DateTimeConstants.THURSDAY:
					return "Thứ 6 " + getTime(msgTime);
				case DateTimeConstants.FRIDAY:
					return "Thứ 7 " + getTime(msgTime);
				case DateTimeConstants.SATURDAY:
					return "Chủ nhật " + getTime(msgTime);
			}
			return "";
		} else {
			return msgTime.toString("MM-dd- " + getTime(msgTime));
		}
	}

	@NonNull
	private static String getTime(DateTime msgTime) {
		int hourOfDay = msgTime.getHourOfDay();
		String when;
		if (hourOfDay >= 18) {//18-24
			when = "Ban đêm";
		} else if (hourOfDay >= 13) {//13-18
			when = "Chiều";
		} else if (hourOfDay >= 11) {//11-13
			when = "Trưa";
		} else if (hourOfDay >= 5) {//5-11
			when = "Sáng";
		} else {//0-5
			when = "Sáng sớm";
		}
		return when + " " + msgTime.toString("hh:mm");
	}
}
