package adv.vadym.com.verticalrecyclercalendar

import java.text.SimpleDateFormat
import java.util.*


class DateModel {
    var calendar = Calendar.getInstance()

    fun days(): List<Date> {

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = calendar.time //formatter.parse(formatter.format(calendar.time))
        val count = getWeeks() * 7

        calendar.set(Calendar.DATE, 1)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        calendar.add(Calendar.DATE, -dayOfWeek)

        val days = ArrayList<Date>()
        for (i in 0 until count) {
            days.add(calendar.time)
            calendar.add(Calendar.DATE, 1)
        }

        calendar.time = startDate
        return days
    }

    private fun getWeeks(): Int {
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

    fun getDayOfWeek(date: Date): Int {
        val sCalendar = Calendar.getInstance()
        sCalendar.time = date
        return sCalendar.get(Calendar.DAY_OF_MONTH)
    }

    fun isCurrentMonth(date: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy.MM", Locale.US)
        val currentMonth = dateFormat.format(calendar.time)
        return currentMonth == dateFormat.format(date)
    }

    fun isFutureDays(date: Date) : Boolean {
        return date.after(Calendar.getInstance().time)
    }

    fun isToday(date: Date): Boolean {
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.US)
        val today = format.format(Calendar.getInstance().time)
        return today == format.format(date)
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
    }

    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
    }
}