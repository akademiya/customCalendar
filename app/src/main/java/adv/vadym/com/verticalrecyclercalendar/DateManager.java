package adv.vadym.com.verticalrecyclercalendar;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateManager {
    Calendar calendar;

    public DateManager() {
        calendar = Calendar.getInstance();
    }

    public List<Date> getDays() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        //formatter.parse(formatter.format(selectedFirstDay))

        Date startDate = calendar.getTime();
        int count = getWeeks() * 7;

        calendar.set(Calendar.DATE, 1);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, -dayOfWeek);

        List<Date> days = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            days.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }

        calendar.setTime(startDate);
        return days;
    }

    public int getWeeks() {
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    public int getDayOfWeek(Date date) {
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(date);
        return sCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public boolean isCurrentMonth(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM", Locale.US);
        String currentMonth = dateFormat.format(calendar.getTime());
        if (currentMonth.equals(dateFormat.format(date))) {
            return true;
        } else return false;
    }

    public boolean isToday(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        String today = format.format(Calendar.getInstance().getTime());
        return today.equals(format.format(date));
    }

    public void nextMonth() {
        calendar.add(Calendar.MONTH, 1);
    }

    public void prevMonth() {
        calendar.add(Calendar.MONTH, -1);
    }

}
